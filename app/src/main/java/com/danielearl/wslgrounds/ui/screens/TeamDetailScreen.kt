package com.danielearl.wslgrounds.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.danielearl.wslgrounds.data.Team

private enum class DetailTab(val label: String) {
    INFO("Info"),
    MAP("Map"),
    TRAINS("Trains"),
    FIXTURES("Fixtures"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(team: Team, onBack: () -> Unit, onSupportClick: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = DetailTab.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(team.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(iconFor(tab), contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (tabs[selectedTab]) {
                DetailTab.INFO -> InfoTab(team = team, onSupportClick = onSupportClick)
                DetailTab.MAP -> MapTab(team = team)
                DetailTab.TRAINS -> TrainsTab(team = team)
                DetailTab.FIXTURES -> FixturesTab(team = team)
            }
        }
    }
}

private fun iconFor(tab: DetailTab) = when (tab) {
    DetailTab.INFO -> Icons.Filled.Info
    DetailTab.MAP -> Icons.Filled.Map
    DetailTab.TRAINS -> Icons.Filled.Train
    DetailTab.FIXTURES -> Icons.AutoMirrored.Filled.EventNote
}
