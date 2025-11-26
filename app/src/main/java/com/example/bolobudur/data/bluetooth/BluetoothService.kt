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
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.bolobudur.MainActivity
import com.example.bolobudur.data.model.BtState
import com.example.bolobudur.data.model.DummyPoint
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

    private var lastLat = 0.0
    private var lastLon = 0.0
    private var lastImu = 0f


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val filter = android.content.IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(bluetoothDisconnectReceiver, filter)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!hasRequiredPermissions()) {
            Log.e("BluetoothService", "Missing required runtime permissions. Stopping service.")
            // Handle this error appropriately, usually by stopping the service
            // and prompting the user in the main activity to grant permissions.
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIF_ID, buildNotification("Preparing...", isPaused, isConnected))

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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun hasRequiredPermissions(): Boolean {
        val bluetoothConnect = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        val fineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // dataSync and connectedDevice types don't strictly require *runtime* permissions
        // to be active, but location does.
        return bluetoothConnect && fineLocation
    }

    // ----------------------------- DUMMY MODE ---------------------------------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @SuppressLint("ForegroundServiceType")
    private fun startDummyStream() {
        isConnected = true
        readJob?.cancel()

        val dummyPath = listOf(
            DummyPoint(110.20429209608466, -7.607945260356175, 0f),
            DummyPoint(110.20423611956676, -7.607955464071338, 0f),
            DummyPoint(110.20423611956676, -7.60800220482227, 0f),
            DummyPoint(110.20423464594575, -7.608040181678348, 0f),
            DummyPoint(110.20423759318624, -7.6080620914020045, 0f),
            DummyPoint(110.2042125416367, -7.60805917010552, 0f),
            DummyPoint(110.20421401525772, -7.608082540475934, 0f),
            DummyPoint(110.20421401525772, -7.608116135381991, 0f),
            DummyPoint(110.20420959439616, -7.608152651580454, 0f),
            DummyPoint(110.20420959439616, -7.60819501036697, 0f),
            DummyPoint(110.2041845428451, -7.6082037742528, 0f),
            DummyPoint(110.20418896370654, -7.608238829796548, 0f),
            DummyPoint(110.20418643053603, -7.608269253445556, 0f),
            DummyPoint(110.20418643053603, -7.608302061029576, 0f),
            DummyPoint(110.20418448353695, -7.60832232453636, 0f),
            DummyPoint(110.2041435965686, -7.608323289465488, 0f),
            DummyPoint(110.20410660359641, -7.60832521932376, 0f),
            DummyPoint(110.2040851866127, -7.60832521932376, 0f),
            DummyPoint(110.20408323961573, -7.6083493425448125, 0f),
            DummyPoint(110.2040160681671, -7.608350307472904, 0f),
            DummyPoint(110.20396057870875, -7.608354167188267, 0f),
            DummyPoint(110.20392261223805, -7.60835223733109, 0f),
            DummyPoint(110.20392066523897, -7.608377325479594, 0f),
            DummyPoint(110.20389924825525, -7.60837443069245, 0f),
            DummyPoint(110.20387491077406, -7.608375395621536, 0f),
            DummyPoint(110.20385154679332, -7.608375395621536, 0f),
            DummyPoint(110.20383791780387, -7.608377325479594, 0f),
            DummyPoint(110.20383278753809, -7.608376308459341, 0f)
        )





        readJob = serviceScope.launch {
            while (isActive && isConnected) {
                for (point in dummyPath) {
                    if (!isActive || isPaused) break

                    lastLat = point.lat
                    lastLon = point.lon
                    lastImu = point.imu

                    locationRepository.updateFromBluetooth(point.lat, point.lon, point.imu)
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
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

                        lastLat = lat
                        lastLon = lon
                        lastImu = imu
                        locationRepository.updateFromBluetooth(lat, lon, imu)
                        updateNotification()
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
        val baseMsg = when {
            isDummyMode && isConnected -> "Dummy GPS running..."
            isDummyMode && !isConnected -> "Dummy GPS stopped"
            !isConnected -> "Disconnected"
            isPaused -> "Paused"
            else -> "Connected: ${currentDevice?.name ?: "Unknown"}"
        }

        val gpsMsg = if (isConnected) {
            "Lat: %.6f\nLon: %.6f\nIMU: %.2f°".format(lastLat, lastLon, lastImu)
        } else ""

        val finalMsg = baseMsg + "\n" + gpsMsg

        val notif = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setContentTitle("GPS Status")
            .setContentText(finalMsg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(finalMsg))
            .build()

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // 🔥 WAJIB Android 14 — nm.notify() harus di Main Thread
        Handler(Looper.getMainLooper()).post {
            nm.notify(NOTIF_ID, notif)
        }


//        Log.d("BluetoothService", "Notification updated: $msg")
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
