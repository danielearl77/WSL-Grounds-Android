package com.danielearl.wslgrounds.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danielearl.wslgrounds.R
import com.danielearl.wslgrounds.data.Team
import com.danielearl.wslgrounds.util.maybeRequestReview

@Composable
fun InfoTab(team: Team, onSupportClick: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { maybeRequestReview(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(stringResource(R.string.header_by_car), fontWeight = FontWeight.Bold)
        Text(team.carInfo)

        Text(stringResource(R.string.header_by_train), fontWeight = FontWeight.Bold)
        Text(team.trainInfo)

        Text(stringResource(R.string.header_food_drink), fontWeight = FontWeight.Bold)
        Text(team.drinkInfo)

        Button(onClick = onSupportClick) {
            Text(stringResource(R.string.support_title))
        }
    }
}
