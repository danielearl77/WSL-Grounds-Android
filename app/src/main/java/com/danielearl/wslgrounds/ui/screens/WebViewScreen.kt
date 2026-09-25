package com.danielearl.wslgrounds.ui.screens

import android.annotation.SuppressLint
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
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
                    settings.domStorageEnabled = true
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.javaScriptCanOpenWindowsAutomatically = true

                    // Remove embedded WebView identifier flags so servers (like National Rail) don't reject the request with Error 55
                    val defaultUserAgent = settings.userAgentString
                    if (defaultUserAgent != null) {
                        settings.userAgentString = defaultUserAgent
                            .replace("; wv", "")
                            .replace(Regex("""Version/\d+\.\d+\s?"""), "")
                    }

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    webChromeClient = WebChromeClient()

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                            isLoading = false
                            errorMessage = null
                            canGoBack = view?.canGoBack() ?: false

                            // Inject CSS stylesheet & MutationObserver to permanently hide dark backdrop filters and auto-accept cookie banners
                            view?.evaluateJavascript(
                                """
                                (function() {
                                    var styleId = 'app-custom-cookie-fix';
                                    if (!document.getElementById(styleId)) {
                                        var style = document.createElement('style');
                                        style.id = styleId;
                                        style.innerHTML = `
                                            #onetrust-consent-sdk,
                                            #onetrust-banner-sdk,
                                            .onetrust-pc-dark-filter,
                                            #onetrust-pc-sdk,
                                            .ot-sdk-row,
                                            .ot-fade-in,
                                            #qc-cmp2-container,
                                            .qc-cmp2-container,
                                            #cookie-banner,
                                            .cookie-banner,
                                            .modal-backdrop,
                                            div[id*="onetrust"],
                                            div[class*="onetrust"],
                                            div[id*="cookie-consent"],
                                            div[class*="cookie-consent"] {
                                                display: none !important;
                                                opacity: 0 !important;
                                                visibility: hidden !important;
                                                pointer-events: none !important;
                                            }
                                            body, html {
                                                overflow: auto !important;
                                                position: static !important;
                                            }
                                        `;
                                        (document.head || document.documentElement).appendChild(style);
                                    }

                                    function autoDismiss() {
                                        var btn = document.getElementById('onetrust-accept-btn-handler') ||
                                                  document.querySelector('.accept-cookies') ||
                                                  document.querySelector('button[id*="accept"]');
                                        if (btn) { try { btn.click(); } catch(e) {} }
                                        var filter = document.querySelector('.onetrust-pc-dark-filter');
                                        if (filter) { filter.remove(); }
                                    }

                                    autoDismiss();

                                    if (!window.__cookieObserverInstalled && window.MutationObserver) {
                                        window.__cookieObserverInstalled = true;
                                        var observer = new MutationObserver(function() {
                                            autoDismiss();
                                        });
                                        observer.observe(document.documentElement, { childList: true, subtree: true });
                                    }
                                })();
                                """.trimIndent(),
                                null,
                            )
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?,
                        ) {
                            if (request?.isForMainFrame == true) {
                                isLoading = false
                                errorMessage = error?.description?.toString() ?: "Error loading page"
                            }
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
