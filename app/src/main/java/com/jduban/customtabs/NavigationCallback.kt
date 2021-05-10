package com.jduban.customtabs

import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsCallback

class NavigationCallback : CustomTabsCallback() {
    val TAG = "NavigationCallback"

    override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {

        when (navigationEvent) {
            NAVIGATION_STARTED -> Log.v(TAG,"Navigation started")
            NAVIGATION_FINISHED -> Log.v(TAG,"Navigation finished")
            NAVIGATION_FAILED -> Log.v(TAG,"Navigation failed")
            NAVIGATION_ABORTED -> Log.v(TAG,"Navigation aborted")
            TAB_SHOWN -> Log.v(TAG,"Tab shown")
            TAB_HIDDEN -> Log.v(TAG,"Tab hidden")
        }
    }

}
