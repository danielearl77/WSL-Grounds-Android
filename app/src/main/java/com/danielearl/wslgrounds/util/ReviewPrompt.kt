package com.danielearl.wslgrounds.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.google.android.play.core.review.ReviewManagerFactory

private const val PREFS_NAME = "wsl_grounds_prefs"
private const val KEY_LOAD_COUNT = "userAppLoadCount"
private const val KEY_LAST_VERSION_PROMPTED = "lastVersionPromptedForReview"
private const val LOAD_COUNT_THRESHOLD = 5

/**
 * Port of TeamInfoViewController.showAppReviewPopover(): after the Info tab has been opened
 * [LOAD_COUNT_THRESHOLD] times, prompt for a Play Store review once per app version.
 */
fun maybeRequestReview(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val count = prefs.getInt(KEY_LOAD_COUNT, 0) + 1
    prefs.edit().putInt(KEY_LOAD_COUNT, count).apply()

    val currentVersion = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        return
    }
    val lastVersionPrompted = prefs.getString(KEY_LAST_VERSION_PROMPTED, null)

    if (count >= LOAD_COUNT_THRESHOLD && currentVersion != lastVersionPrompted) {
        val activity = context as? Activity ?: return
        val reviewManager = ReviewManagerFactory.create(context)
        reviewManager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful) {
                reviewManager.launchReviewFlow(activity, request.result)
            }
        }
        prefs.edit().putString(KEY_LAST_VERSION_PROMPTED, currentVersion).apply()
    }
}
