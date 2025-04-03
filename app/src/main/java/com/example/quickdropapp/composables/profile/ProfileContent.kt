package com.example.quickdropapp.composables.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.quickdropapp.models.auth.User

@Composable
fun ProfileContent(user: User?, userId: Int, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show BecomeCourierCard only if the user is not a courier or admin
        if (user?.role == "user") {
            BecomeCourierCard(userId, navController)
        }
        SettingsCard(userId, navController)
        HistoryCard(userId, navController)
        HelpSupportCard(userId, navController)
        Spacer(modifier = Modifier.weight(1f))
    }
}