package com.danielearl.wslgrounds.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.danielearl.wslgrounds.data.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamListScreen(
    teams: List<Team>,
    onTeamClick: (Team) -> Unit,
    onAboutClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WSL Grounds") },
                actions = {
                    IconButton(onClick = onAboutClick) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "About")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(teams, key = { it.name }) { team ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTeamClick(team) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = team.logoRes),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = team.name,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }
        }
    }
}
