package com.danielearl.wslgrounds.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.danielearl.wslgrounds.R
import com.danielearl.wslgrounds.billing.BillingManager

private const val PREFS_NAME = "wsl_grounds_prefs"
private const val KEY_TIP_COUNT = "countOfTipsGiven"
private const val TIP_LIMIT = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE) }
    val billingManager = remember { BillingManager(context) }

    var tipsGiven by remember { mutableIntStateOf(prefs.getInt(KEY_TIP_COUNT, 0)) }
    var product by remember { mutableStateOf<ProductDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPurchasing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (tipsGiven <= TIP_LIMIT) {
            billingManager.startConnection(
                onReady = {
                    billingManager.getTipProduct { result ->
                        isLoading = false
                        result.onSuccess { product = it }
                        result.onFailure { errorMessage = it.message }
                    }
                },
                onError = { message ->
                    isLoading = false
                    errorMessage = message
                },
            )
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.support_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                tipsGiven > TIP_LIMIT || product == null -> {
                    Text(stringResource(R.string.support_unavailable))
                }
                else -> {
                    val details = product!!
                    Text(details.title)
                    Text(details.description)
                    Text(billingManager.formattedPrice(details) ?: "")
                    Button(
                        enabled = !isPurchasing,
                        onClick = {
                            if (!billingManager.canMakePayments() || activity == null) {
                                errorMessage = "WARNING: In App Purchases not allowed on this device."
                                return@Button
                            }
                            isPurchasing = true
                            billingManager.buy(activity, details) { result ->
                                isPurchasing = false
                                result.onSuccess {
                                    tipsGiven += 1
                                    prefs.edit().putInt(KEY_TIP_COUNT, tipsGiven).apply()
                                    onBack()
                                }
                                result.onFailure { errorMessage = it.message }
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(stringResource(R.string.support_title))
                    }
                    if (isPurchasing) {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                    }
                }
            }
        }
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text(stringResource(R.string.iap_error_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}
