package com.jduban.customtabs

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.jduban.customtabs.CustomTabsHelper.getPackageNameToUse
import com.jduban.customtabs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val useCustomTabs: Boolean = true

    val url = "https://idp-dev.sncf.fr/openam/oauth2/IDP/authorize?" +
            "client_id=sncf.id.sandbox.app" +
            "&redirect_uri=sncf-id-sandbox-2%3A%2F%2Foidc-redirect_uri" +
            "&response_type=code" +
            "&scope=openid%20client_id%20profile" +
            "&acr_values=password"

    private var mCustomTabsSession: CustomTabsSession? = null
    private var mPackageNameToBind: String? = null
    private var mClient: CustomTabsClient? = null
    private lateinit var mCustomTabsServiceConnection: CustomTabsServiceConnection

    private var status = DEFAULT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindCustomTabsService()

        binding.button.setOnClickListener {
            getSession()?.let {
                startCCT(it)
            }

        }

    }

    private fun bindCustomTabsService() {

        if (TextUtils.isEmpty(mPackageNameToBind)) {
            mPackageNameToBind = getPackageNameToUse(this, useCustomTabs)
            if (mPackageNameToBind == null) {
                // No app with custom tab capability
                return
            }
        }
        mCustomTabsServiceConnection = createCustomTabService()

        CustomTabsClient.bindCustomTabsService(this, mPackageNameToBind, mCustomTabsServiceConnection)
        status = BOUND
    }

    private fun createCustomTabService(): CustomTabsServiceConnection {
        return object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(componentName: ComponentName, customTabsClient: CustomTabsClient) {
                status = CONNECTED
                mClient = customTabsClient
                //Warmup
                mClient?.warmup(0)

                //May launch
                val mSession = getSession()
                mSession?.let { s ->
                    s.mayLaunchUrl(Uri.parse(url), null, null)
                }

            }

            override fun onServiceDisconnected(name: ComponentName) {
                status = DEFAULT
                mClient = null
            }
        }
    }

    private fun getSession(): CustomTabsSession? {
        if (mClient == null) {
            mCustomTabsSession = null
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient!!.newSession(NavigationCallback())
        }
        return mCustomTabsSession
    }

    private fun startCCT(session: CustomTabsSession) {

        val builder = CustomTabsIntent.Builder(session)
        val mCustomTabsIntent = builder.build()

        CustomTabsHelper.addKeepAliveExtra(this, mCustomTabsIntent.intent)

//        // Flags Needed to prevent bugs on some phones (Samsing ? Android 5.X ? )
//        mCustomTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//        mCustomTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCustomTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        } else {
            mCustomTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        }

        mCustomTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        mCustomTabsIntent.launchUrl(this, Uri.parse(url))
    }

    companion object {
        private const val DEFAULT = 0
        private const val BOUND = 1
        private const val CONNECTED = 2
    }
}