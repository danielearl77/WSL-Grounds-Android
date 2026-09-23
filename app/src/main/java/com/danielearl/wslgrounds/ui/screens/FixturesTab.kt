package com.danielearl.wslgrounds.ui.screens

import androidx.compose.runtime.Composable
import com.danielearl.wslgrounds.data.Team

/** Port of TeamFixturesViewController: the team's fixtures page. */
@Composable
fun FixturesTab(team: Team) {
    WebViewScreen(url = team.fixturesUrl, allowInAppBack = false)
}
