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
    private var isDummy = false
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
        isDummy = intent?.getBooleanExtra(EXTRA_IS_DUMMY, false) ?: false

        when(intent?.action) {
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

        if (isDummy) {
            startDummyStream()
        } else if (address != null) {
            connectToDevice(address)
        }

        return START_STICKY
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @SuppressLint("ForegroundServiceType")
    private fun startDummyStream() {
        val dummyPath = listOf(
            listOf(110.20349665886357, -7.60789447321352),
            listOf(110.20349551018717, -7.6078625933894415),
            listOf(110.20414057907232, -7.607896097934955)
        )

        readJob?.cancel()
        readJob = serviceScope.launch {
            while (isActive) {
                for (coord in dummyPath) {
                    if (!isActive) break
                    val lon = coord[0]
                    val lat = coord[1]
                    val imu = (0..360).random().toFloat()
                    locationRepository.updateFromBluetooth(lat, lon, imu)
                    delay(2000L)
                }
            }
        }
        startForeground(NOTIF_ID, buildNotification("Dummy GPS running...", false, true))
        pushStatusToRepository()
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == android.content.pm.PackageManager.PERMISSION_GRANTED
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
            currentDevice = device

            if (!bluetoothReceiver.connectToDevice(device)) {
                isConnected = false
                updateNotification()
                pushStatusToRepository()
                stopSelf()
                return@launch
            }

            isConnected = true
            updateNotification()
            pushStatusToRepository()
            startReadingLoop()
        }
    }

    private fun startReadingLoop() {
        readJob?.cancel()
        readJob = serviceScope.launch {
            while (isActive && !isPaused && isConnected) {
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun togglePauseResume() {
        isPaused = !isPaused
        if (!isPaused && isConnected) startReadingLoop()
        updateNotification()
        pushStatusToRepository()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun toggleDisconnectConnect() {
        if (isConnected) {
            readJob?.cancel()
            bluetoothReceiver.disconnect()
            isConnected = false
        } else {
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
        updateNotification()
        pushStatusToRepository()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun updateNotification() {
        val msg = when {
            isDummy -> "Dummy GPS running..."
            !isConnected -> "Disconnected"
            isPaused -> "Paused"
            else -> "Connected: ${currentDevice?.name ?: "Unknown"}"
        }
        val notif = buildNotification(msg, isPaused, isConnected)
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, notif)
    }

    override fun onDestroy() {
        readJob?.cancel()
        serviceScope.cancel()
        bluetoothReceiver.disconnect()
        unregisterReceiver(bluetoothDisconnectReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(NOTIF_CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(NOTIF_CHANNEL_ID, "Bluetooth Service", NotificationManager.IMPORTANCE_LOW)
                )
            }
        }
    }

    private fun buildNotification(msg: String, isPaused: Boolean, isConnected: Boolean) =
        NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("BoloBluetooth")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .addAction(
                if (isPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause,
                if (isPaused) "Resume" else "Pause",
                PendingIntent.getService(
                    this, 0, Intent(this, BluetoothService::class.java).apply { action = ACTION_PAUSE_RESUME },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                if (isConnected) android.R.drawable.stat_sys_data_bluetooth
                else android.R.drawable.stat_notify_error,
                if (isConnected) "Disconnect" else "Connect",
                PendingIntent.getService(
                    this, 1, Intent(this, BluetoothService::class.java).apply { action = ACTION_DISCONNECT_CONNECT },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setContentIntent(
                PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
            )
            .build()

    private val bluetoothDisconnectReceiver = object : android.content.BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReceive(context: Context, intent: Intent) {
            if (!hasBluetoothConnectPermission()) return
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            if (device != null && device == currentDevice) {
                isConnected = false
                updateNotification()
                pushStatusToRepository()
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun pushStatusToRepository() {
        val state = BtState(
            isEnabled = bluetoothReceiver.isBluetoothEnabled(),
            isConnected = isConnected,
            isPaused = isPaused,
            deviceName = currentDevice?.name
        )
        locationRepository.updateBtStateFromService(state)
    }
}
