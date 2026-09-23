package com.danielearl.wslgrounds.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.danielearl.wslgrounds.R
import com.danielearl.wslgrounds.data.Team

private const val NO_STATION_CODE = "XXX"

/** Port of TeamTrainViewController: a live National Rail departure board for the team's station. */
@Composable
fun TrainsTab(team: Team) {
    if (team.stationCode == NO_STATION_CODE) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.no_nearby_station),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
            )
        }
    } else {
        val url = "http://m.nationalrail.co.uk/pj/ldbboard/dep/${team.stationCode}"
        WebViewScreen(url = url, allowInAppBack = true)
    }
}
