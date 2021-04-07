package io.github.kiranshny.wifiinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.ScanResult.*
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

internal const val SCAN_RESULTS = "wifi_scan_results"
internal const val NOT_AVAILABLE = "N/A"

class WiFiDetailsActivity : AppCompatActivity() {
    companion object {
        fun create(context: Context, scanResult: ScanResult): Intent {
            return Intent(context, WiFiDetailsActivity::class.java)
                .putExtra(SCAN_RESULTS, scanResult)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        extractData()
    }

    @SuppressLint("SetTextI18n")
    private fun extractData() {
        intent?.getParcelableExtra<ScanResult>(SCAN_RESULTS)?.let { scanResult ->
            supportActionBar?.title = getSSID(scanResult)
            supportActionBar?.subtitle = scanResult.BSSID
            findViewById<TextView>(R.id.ssidTv).text =
                "SSID: " + getSSID(scanResult)
            findViewById<TextView>(R.id.bssidTv).text =
                "BSSID: " + scanResult.BSSID
            findViewById<TextView>(R.id.rssiTv).text =
                "RSSI: " + scanResult.level.toString()
            findViewById<TextView>(R.id.frequencyTv).text =
                "Frequency: " + scanResult.frequency.toString()
            findViewById<TextView>(R.id.centerFrequency0Tv).text =
                "Canter Frequency 0: " + getCenterFrequency0(scanResult)
            findViewById<TextView>(R.id.centerFrequency1Tv).text =
                "Canter Frequency 1: " + getCenterFrequency1(scanResult)
            findViewById<TextView>(R.id.capabilitiesTv).text =
                "Capabilities: " + scanResult.capabilities
            findViewById<TextView>(R.id.channelWidthTv).text =
                "Channel Width: " + getChannelWidth(scanResult)
            findViewById<TextView>(R.id.operatorNameTv).text =
                "Operator friendly name: " + getOperatorFriendlyName(scanResult)
            findViewById<TextView>(R.id.venueNameTv).text =
                "Venue name: " + getVenueName(scanResult)
            findViewById<TextView>(R.id.wiFiStandardTv).text =
                "WiFi Standard: " + getWiFiStandard(scanResult)
            findViewById<TextView>(R.id.is802responderTv).text =
                "Is 802.11mc Responder: " + isWiFi802Responder(scanResult)
            findViewById<TextView>(R.id.isPasspointNetworkTv).text =
                "Is Passpoint Network: " + isWiFiPassPointNetwork(scanResult)
            findViewById<TextView>(R.id.timeSinceBootTv).text =
                "Timestamp in μs (since boot): " + scanResult.timestamp
        }
    }

    private fun getSSID(scanResult: ScanResult): String {
        return if (scanResult.SSID.isNullOrEmpty()) "Hidden SSID" else scanResult.SSID
    }

    private fun getCenterFrequency1(scanResult: ScanResult): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            scanResult.centerFreq1.toString()
        } else {
            NOT_AVAILABLE
        }
    }

    private fun getCenterFrequency0(scanResult: ScanResult): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            scanResult.centerFreq0.toString()
        } else {
            NOT_AVAILABLE
        }
    }

    private fun getVenueName(scanResult: ScanResult): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            scanResult.venueName.toString()
        } else {
            NOT_AVAILABLE
        }
    }

    private fun isWiFiPassPointNetwork(scanResult: ScanResult): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            scanResult.isPasspointNetwork.toString()
        } else {
            NOT_AVAILABLE
        }
    }

    private fun isWiFi802Responder(scanResult: ScanResult): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            scanResult.is80211mcResponder.toString()
        } else {
            NOT_AVAILABLE
        }
    }

    private fun getWiFiStandard(scanResult: ScanResult): String {
        return when (scanResult.wifiStandard) {
            WIFI_STANDARD_UNKNOWN -> "WIFI_STANDARD_UNKNOWN"
            WIFI_STANDARD_LEGACY -> "WIFI_STANDARD_LEGACY"
            WIFI_STANDARD_11N -> "WIFI_STANDARD_11N"
            WIFI_STANDARD_11AC -> "WIFI_STANDARD_11AC"
            WIFI_STANDARD_11AX -> "WIFI_STANDARD_11AX"
            else -> "Unknown Type: " + scanResult.wifiStandard.toString()
        }
    }

    private fun getOperatorFriendlyName(scanResult: ScanResult): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            scanResult.operatorFriendlyName.toString()
        } else {
            NOT_AVAILABLE
        }
    }

    /**
     * AP Channel bandwidth; one of {@link #CHANNEL_WIDTH_20MHZ}, {@link #CHANNEL_WIDTH_40MHZ},
     * {@link #CHANNEL_WIDTH_80MHZ}, {@link #CHANNEL_WIDTH_160MHZ}
     * or {@link #CHANNEL_WIDTH_80MHZ_PLUS_MHZ}.
     */
    private fun getChannelWidth(result: ScanResult): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            when (result.channelWidth) {
                CHANNEL_WIDTH_20MHZ -> "20MHZ"
                CHANNEL_WIDTH_40MHZ -> "40MHZ"
                CHANNEL_WIDTH_80MHZ -> "80MHZ"
                CHANNEL_WIDTH_160MHZ -> "160MHZ"
                CHANNEL_WIDTH_80MHZ_PLUS_MHZ -> "80MHZ PLUS MHZ"
                else -> "Unknown Type: " + result.channelWidth.toString()
            }
        } else {
            NOT_AVAILABLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}