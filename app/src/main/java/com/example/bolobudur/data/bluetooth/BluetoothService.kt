package com.example.bolobudur.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.example.bolobudur.MainActivity
import com.example.bolobudur.data.model.BtState
import com.example.bolobudur.data.repository.LocationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothService : Service() {

    @Inject lateinit var bluetoothReceiver: BluetoothReceiver
    @Inject lateinit var locationRepository: LocationRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var readJob: Job? = null
    private var currentDevice: BluetoothDevice? = null
    private var currentAddress: String? = null

    private var isDummyMode = false
    private var isPaused = false
    private var isConnected = false

    companion object {
        const val EXTRA_DEVICE_ADDRESS = "extra_device_address"
        const val EXTRA_IS_DUMMY = "extra_is_dummy"
        const val NOTIF_CHANNEL_ID = "bluetooth_service_channel"
        const val NOTIF_ID = 1001

        const val ACTION_PAUSE_RESUME = "action_pause_resume"
        const val ACTION_DISCONNECT_CONNECT = "action_disconnect_connect"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val filter = android.content.IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(bluetoothDisconnectReceiver, filter)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val address = intent?.getStringExtra(EXTRA_DEVICE_ADDRESS)
        val dummyFlag = intent?.getBooleanExtra(EXTRA_IS_DUMMY, isDummyMode) ?: isDummyMode

        // detect if this intent introduces a NEW device address (before overwriting currentAddress)
        val isNewDevice = address != null && address != currentAddress

        if (address != null) currentAddress = address
        isDummyMode = dummyFlag

        when (intent?.action) {
            ACTION_PAUSE_RESUME -> {
                togglePauseResume()
                return START_STICKY
            }
            ACTION_DISCONNECT_CONNECT -> {
                toggleDisconnectConnect()
                return START_STICKY
            }
        }

        startForeground(NOTIF_ID, buildNotification("Preparing...", isPaused, isConnected))

        if (isDummyMode) {
            startDummyStream()
        } else {
            // if new device -> force disconnect existing and connect to new address
            if (isNewDevice && address != null) {
                Log.d("BluetoothService", "New device requested ($address) — resetting connection")
                // reset current connection cleanly
                readJob?.cancel()
                try {
                    bluetoothReceiver.disconnect()
                } catch (e: Exception) {
                    Log.w("BluetoothService", "Error while disconnecting old device", e)
                }
                isConnected = false
                isPaused = false

                connectToDevice(address)
            } else if (address != null && !isConnected) {
                // no current connection but address provided -> connect
                connectToDevice(address)
            }
        }

        return START_STICKY
    }

    // ----------------------------- DUMMY MODE ---------------------------------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @SuppressLint("ForegroundServiceType")
    private fun startDummyStream() {
        isConnected = true
        readJob?.cancel()

        val dummyPath = listOf(
//            listOf(110.20340045586158, -7.608002259549181),
//            listOf(110.20339948672768, -7.607981126273188),
//            listOf(110.20339948672768, -7.607973441446134),
//            listOf(110.20339851759377, -7.60796191420377),
//            listOf(110.20343631384338, -7.607954229375352),
//            listOf(110.203472, -7.607957),
//            listOf(110.20352547423175, -7.607960953599985),
//            listOf(110.20357683836858, -7.607955189979151),
//            listOf(110.20363110990905, -7.60794366273727)
//            listOf(110.365962390237, -7.7358614501326)
            listOf(110.36599842764792, -7.7358836109075355),
            listOf(110.36598151944116, -7.735917119558167),
            listOf(110.36596179320082, -7.73594643962538),
            listOf(110.36594629401162, -7.735971571110213),
            listOf(110.36592656777117, -7.735998098787448),
            listOf(110.36591247759964, -7.736017645495409),
            listOf(110.36589275135913, -7.736042776974571),
            listOf(110.36587443413629, -7.736067908453606),
            listOf(110.36585188986203, -7.736100020896075),
            listOf(110.36583216362163, -7.736126548565139),
            listOf(110.36581102836357, -7.736148887652547),
            listOf(110.36578989310698, -7.736175415318584),
            listOf(110.36576593981516, -7.736207527752839),
            listOf(110.3657476225909, -7.736238243993),
            listOf(110.36572789635045, -7.736252205919229),
            listOf(110.3657476225909, -7.7362731488069585),
            listOf(110.36577721195158, -7.73630107265808),
            listOf(110.3658011652434, -7.736322015543365),
            listOf(110.36580828329193, -7.736324712518908),
            listOf(110.36582421883804, -7.7363377164614775),
            listOf(110.36584202915299, -7.736350720403522),
            listOf(110.36585796469922, -7.736356293521212),
            listOf(110.36587858717115, -7.736368368609519),
            listOf(110.365937561531, -7.73640694308503)
        )

        readJob = serviceScope.launch {
            while (isActive && isConnected) {
                for (coord in dummyPath) {
                    if (!isActive || isPaused) break
                    val lon = coord[0]
                    val lat = coord[1]
                    val imu = (0).toFloat()
                    locationRepository.updateFromBluetooth(lat, lon, imu)
                    delay(2000L)
                }
            }
        }

        updateNotification()
        pushStatusToRepository()
        Log.d("BluetoothService", "Dummy GPS started")
    }

    // ---------------------------- BLUETOOTH MODE ------------------------------

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        } else true
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(address: String) {
        readJob?.cancel()
        readJob = serviceScope.launch {
            if (!hasBluetoothConnectPermission()) {
                stopSelf()
                return@launch
            }

            val adapter = bluetoothReceiver.getAdapter()
            val device = adapter?.getRemoteDevice(address) ?: run { stopSelf(); return@launch }
            currentDevice = device // update device reference

            // Tambahkan log agar kelihatan device mana yang dihubungkan
            Log.d("BluetoothService", "Connecting to ${device.name} [$address]")

            if (!bluetoothReceiver.connectToDevice(device)) {
                isConnected = false
                updateNotification()
                pushStatusToRepository()
                stopSelf()
                return@launch
            }

            // Setelah sukses connect, update notifikasi dengan nama baru
            isConnected = true
            updateNotification()
            pushStatusToRepository()
            startReadingLoop()
        }
    }


    private fun startReadingLoop() {
        readJob?.cancel()
        readJob = serviceScope.launch {
            while (isActive && !isPaused && isConnected && !isDummyMode) {
                bluetoothReceiver.readData()?.let { raw ->
                    try {
                        val j = JSONObject(raw)
                        val lat = j.optDouble("latitude", j.optDouble("lat", 0.0))
                        val lon = j.optDouble("longitude", j.optDouble("lon", 0.0))
                        val imu = j.optDouble("imu", j.optDouble("yaw", 0.0)).toFloat()
                        locationRepository.updateFromBluetooth(lat, lon, imu)
                    } catch (e: Exception) {
                        Log.e("BluetoothService", "Parse error: $raw", e)
                    }
                }
            }
        }
    }

    // ------------------------------- TOGGLES ----------------------------------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun togglePauseResume() {
        isPaused = !isPaused
        if (!isPaused && isConnected) {
            if (isDummyMode) startDummyStream()
            else startReadingLoop()
        } else {
            readJob?.cancel()
        }
        updateNotification()
        pushStatusToRepository()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun toggleDisconnectConnect() {
        if (isDummyMode) {
            if (isConnected) {
                readJob?.cancel()
                isConnected = false
                Log.d("BluetoothService", "Dummy disconnected")
            } else {
                startDummyStream()
            }
            updateNotification()
            pushStatusToRepository()
            return
        }

        if (isConnected) {
            readJob?.cancel()
            bluetoothReceiver.disconnect()
            isConnected = false
            updateNotification()
            pushStatusToRepository()
        } else {
            // if currentDevice is null, try to get it from currentAddress
            if (currentDevice == null && currentAddress != null) {
                val adapter = bluetoothReceiver.getAdapter()
                currentDevice = adapter?.getRemoteDevice(currentAddress)
            }

            currentDevice?.let { device ->
                serviceScope.launch {
                    if (!hasBluetoothConnectPermission()) return@launch
                    if (bluetoothReceiver.connectToDevice(device)) {
                        isConnected = true
                        startReadingLoop()
                    }
                    updateNotification()
                    pushStatusToRepository()
                }
            }
        }
    }

    // --------------------------- NOTIFICATION ---------------------------------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun updateNotification() {
        val msg = when {
            isDummyMode && isConnected -> "Dummy GPS running..."
            isDummyMode && !isConnected -> "Dummy GPS stopped"
            !isConnected -> "Disconnected"
            isPaused -> "Paused"
            else -> "Connected: ${currentDevice?.name ?: "Unknown"}"
        }
        val notif = buildNotification(msg, isPaused, isConnected)
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, notif)

        Log.d("BluetoothService", "Notification updated: $msg")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(NOTIF_CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        NOTIF_CHANNEL_ID,
                        "Bluetooth Service",
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
        }
    }

    private fun buildNotification(msg: String, isPaused: Boolean, isConnected: Boolean): android.app.Notification {
        // Make Intents unique by embedding the currentAddress in the Intent data URI.
        val pauseIntent = Intent(this, BluetoothService::class.java).apply {
            action = ACTION_PAUSE_RESUME
            putExtra(EXTRA_IS_DUMMY, isDummyMode)
            putExtra(EXTRA_DEVICE_ADDRESS, currentAddress)
            data = Uri.parse("bolobluetooth://$ACTION_PAUSE_RESUME/${currentAddress ?: "none"}")
        }

        val connectIntent = Intent(this, BluetoothService::class.java).apply {
            action = ACTION_DISCONNECT_CONNECT
            putExtra(EXTRA_IS_DUMMY, isDummyMode)
            putExtra(EXTRA_DEVICE_ADDRESS, currentAddress)
            data = Uri.parse("bolobluetooth://$ACTION_DISCONNECT_CONNECT/${currentAddress ?: "none"}")
        }

        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("BoloBluetooth")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            // allow full refresh so Android updates text when we call notify
            .setOnlyAlertOnce(false)
            .setOngoing(true)
            .addAction(
                if (isPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause,
                if (isPaused) "Resume" else "Pause",
                PendingIntent.getService(
                    this,
                    // include address hash in requestCode for extra uniqueness
                    (0x1000 + (currentAddress?.hashCode() ?: 0)),
                    pauseIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(
                if (isConnected) android.R.drawable.stat_sys_data_bluetooth else android.R.drawable.stat_notify_error,
                if (isConnected) "Disconnect" else "Connect",
                PendingIntent.getService(
                    this,
                    (0x2000 + (currentAddress?.hashCode() ?: 0)),
                    connectIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
    }

    // --------------------------- RECEIVER -------------------------------------

    private val bluetoothDisconnectReceiver = object : android.content.BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReceive(context: Context, intent: Intent) {
            if (!hasBluetoothConnectPermission()) return
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            if (device != null && device == currentDevice) {
                isConnected = false
                Log.d("BluetoothService", "Device disconnected: ${device.name}")
                updateNotification()
                pushStatusToRepository()
            }
        }
    }

    // ---------------------------- CLEANUP -------------------------------------

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("BluetoothService", "Task removed by user, stopping service.")
        stopSelf()
    }

    override fun onDestroy() {
        readJob?.cancel()
        serviceScope.cancel()
        bluetoothReceiver.disconnect()
        unregisterReceiver(bluetoothDisconnectReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    // ------------------------ STATE SYNC --------------------------------------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun pushStatusToRepository() {
        val state = BtState(
            isEnabled = bluetoothReceiver.isBluetoothEnabled(),
            isConnected = isConnected,
            isPaused = isPaused,
            deviceName = currentDevice?.name
        )
        locationRepository.updateBtStateFromService(state)
        Log.d("BluetoothService", "State pushed: $state")
    }
}
