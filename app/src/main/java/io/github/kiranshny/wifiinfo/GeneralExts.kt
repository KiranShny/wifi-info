package io.github.kiranshny.wifiinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun Context.isPermissionGranted(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.permissionSettingsIntent() = Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", packageName, null)
}

fun Activity.canEnableGps(
    locationSettingsRequest: LocationSettingsRequest,
    onSuccess: (ResolvableApiException) -> Unit,
    onFailure: () -> Unit
) {
    val requestTask = LocationServices
        .getSettingsClient(this)
        .checkLocationSettings(locationSettingsRequest)

    requestTask
        .addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                onSuccess(exception)
            } else {
                onFailure()
            }
        }
        .addOnSuccessListener { onFailure() }
}

fun Activity.requestToEnableGps(e: ResolvableApiException) {
    e.startResolutionForResult(this, 100)
}

fun Fragment.requestToEnableGpsFromFragment(e: ResolvableApiException) {
    startIntentSenderForResult(
        e.resolution.intentSender,
        100 /*requestCode*/,
        null /*fillInIntent*/,
        0 /*flagsMask*/,
        0 /*flagsValues*/,
        0 /*extraFlags*/,
        null /*options*/
    )
}

fun Long.getTimeString(): String {
    var secondsElapsed = this
    var negative = false
    if (secondsElapsed < 0) {
        negative = true
        secondsElapsed *= -1
    }
    var timeString = when {
        secondsElapsed >= 3600 -> {
            val hours = secondsElapsed / 3600
            val mins = (secondsElapsed % 3600) / 60
            val seconds = secondsElapsed % 60
            "${hoursToString(hours)}:${minutesToString(mins)}:${secondsToString(seconds)}"
        }
        secondsElapsed >= 60 -> {
            val seconds = secondsElapsed % 60
            "00:${minutesToString(secondsElapsed / 60)}:${secondsToString(seconds)}"
        }
        else -> {
            "00:00:${secondsToString(secondsElapsed)}"
        }
    }
    if (negative)
        timeString = "-$timeString"
    return timeString
}
fun hoursToString(hours: Long): String {
    return when {
        hours < 10 -> {
            "0$hours"
        }
        else -> hours.toString()
    }
}

fun minutesToString(mins: Long): String {
    return when {
        mins < 10 -> {
            "0$mins"
        }
        else -> mins.toString()
    }
}

fun secondsToString(seconds: Long): String {
    return when {
        seconds < 10 -> {
            "0$seconds"
        }
        else -> seconds.toString()
    }
}

fun <T> observableIo(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable =
    apply { compositeDisposable.add(this) }
