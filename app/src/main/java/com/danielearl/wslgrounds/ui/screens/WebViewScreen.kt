package com.danielearl.wslgrounds.ui.screens

import android.annotation.SuppressLint
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.danielearl.wslgrounds.R

/**
 * Port of TeamTrainViewController / TeamFixturesViewController: a WebView with a loading
 * spinner and a fixed error message on load failure. When [allowInAppBack] is true (the
 * Trains tab) a visible back button and the system back gesture step the WebView's own
 * history instead of leaving the tab, matching the iOS pageBackButton behaviour.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String, allowInAppBack: Boolean) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    if (allowInAppBack) {
        BackHandler(enabled = canGoBack) {
            webView?.goBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                            isLoading = false
                            errorMessage = null
                            canGoBack = view?.canGoBack() ?: false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?,
                        ) {
                            isLoading = false
                            errorMessage = description
                        }

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler,
                            error: SslError?,
                        ) {
                            isLoading = false
                            errorMessage = "SSL error"
                            handler.cancel()
                        }
                    }
                    webView = this
                    loadUrl(url)
                }
            },
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        errorMessage?.let {
            Text(
                text = stringResource(R.string.error_unable_to_load),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
            )
        }

        if (allowInAppBack && canGoBack) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Button(onClick = { webView?.goBack() }) {
                    Text("Back")
                }
            }
        }
    }
}
