package com.danielearl.wslgrounds

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.danielearl.wslgrounds.data.Teams
import com.danielearl.wslgrounds.ui.screens.AboutScreen
import com.danielearl.wslgrounds.ui.screens.SupportScreen
import com.danielearl.wslgrounds.ui.screens.TeamDetailScreen
import com.danielearl.wslgrounds.ui.screens.TeamListScreen
import com.danielearl.wslgrounds.ui.theme.WSLGroundsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WSLGroundsTheme {
                WSLGroundsApp()
            }
        }
    }
}

private const val ROUTE_TEAM_LIST = "teamList"
private const val ROUTE_ABOUT = "about"
private const val ROUTE_SUPPORT = "support"
private const val ROUTE_TEAM_DETAIL = "teamDetail/{teamName}"

@Composable
fun WSLGroundsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_TEAM_LIST) {
        composable(ROUTE_TEAM_LIST) {
            TeamListScreen(
                teams = Teams.all,
                onTeamClick = { team ->
                    navController.navigate("teamDetail/${Uri.encode(team.name)}")
                },
                onAboutClick = { navController.navigate(ROUTE_ABOUT) },
            )
        }
        composable(ROUTE_ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() },
                onSupportClick = { navController.navigate(ROUTE_SUPPORT) },
            )
        }
        composable(ROUTE_SUPPORT) {
            SupportScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = ROUTE_TEAM_DETAIL,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val teamName = Uri.decode(backStackEntry.arguments?.getString("teamName") ?: "")
            val team = Teams.all.first { it.name == teamName }
            TeamDetailScreen(
                team = team,
                onBack = { navController.popBackStack() },
                onSupportClick = { navController.navigate(ROUTE_SUPPORT) },
            )
        }
    }
}
