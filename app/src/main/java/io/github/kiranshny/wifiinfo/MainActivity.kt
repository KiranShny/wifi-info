package io.github.kiranshny.wifiinfo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.florent37.runtimepermission.RuntimePermission.askPermission
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val adapter by lazy {
        WiFiAdapter()
    }
    private val locationPermissionBottomSheet by lazy {
        LocationPermissionBottomSheet().apply {
            withAction {
                dismiss()
                if (it.hasForeverDenied() or it.hasDenied()) {
                    startActivity(permissionSettingsIntent())
                } else {
                    requestGPSPermission()
                }
            }
        }
    }

    private val mWifiBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.tag("WifiManagerTesting")
                .e("WiFi Scan Results available")
            val wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val results = wifiManager.scanResults
            runOnUiThread {
                progressBar.visibility = View.GONE
                adapter.setData(results)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(
            mWifiBroadcastReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mWifiBroadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        hintTv.visibility = View.VISIBLE
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            hintTv.visibility = View.GONE
            scanWifi()
        }
        availableWiFiRv.layoutManager = LinearLayoutManager(this@MainActivity)
        availableWiFiRv.adapter = adapter
    }

    private fun requestWifiPermission() {
        askPermission(
            this,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
        )
            .onAccepted {
                if (it.accepted.size == 2) {
                    scanWifi()
                }
            }
            .onDenied {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_denied))
                    .setMessage(getString(R.string.wifi_scan_permission_denied))
                    .setPositiveButton(getString(R.string.okay)) { _, _ ->
                        requestWifiPermission()
                    }
                    .show()
            }
            .onForeverDenied {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_denied))
                    .setMessage(getString(R.string.wifi_scan_permission_denied))
                    .setPositiveButton(getString(R.string.okay)) { _, _ ->
                        it.goToSettings()
                    }
                    .show()
            }
            .ask()
    }

    @SuppressLint("SetTextI18n", "HardwareIds")
    private fun scanWifi() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestGPSPermission()
            return
        }
        if (
            !isPermissionGranted(Manifest.permission.CHANGE_WIFI_STATE)
            || !isPermissionGranted(Manifest.permission.ACCESS_WIFI_STATE)
        ) {
            requestWifiPermission()
            return
        }
        adapter.setData(listOf())
        progressBar.visibility = View.VISIBLE
        val wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (isWiFiConnected()) {
            Timber.tag("WifiManagerTesting").e("WIFI Available")
            val result1 = (wifiManager.setWifiEnabled(false))
            val result = wifiManager.disconnect()
            Timber.tag("WifiManagerTesting").e("WIFI Disconnect called : $result, $result1")
        }
        Timber.tag("WifiManagerTesting").e("scanning started")
        wifiManager.startScan()
    }

    private fun Activity.isWiFiConnected(): Boolean {
        val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val capabilities = connManager.getNetworkCapabilities(connManager.activeNetwork)
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        } else {
            connManager.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
        }
    }

    private fun requestGPSPermission() {
        if (locationPermissionBottomSheet.isAdded)
            locationPermissionBottomSheet.dismiss()
        askPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
            .onAccepted {
                scanWifi()
            }
            .onDenied {
                locationPermissionBottomSheet.show(supportFragmentManager, it)
            }
            .onForeverDenied {
                locationPermissionBottomSheet.show(supportFragmentManager, it)
            }
            .ask()
    }

}