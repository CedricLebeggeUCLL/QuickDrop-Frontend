// com.example.quickdropapp.composables/PackageItem.kt
package com.example.quickdropapp.composables.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.WhiteSmoke
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun PackageItem(
    packageItem: Package,
    navController: NavController,
    onDelete: (Int) -> Unit,
    onUpdate: (Int) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Veilige toegang tot packageItem.id
    val packageId = packageItem.id ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(WhiteSmoke),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(containerColor = WhiteSmoke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pakket #${packageId}",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Omschrijving: ${packageItem.description ?: "Geen omschrijving"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: ${packageItem.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreenSustainable,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            isLoading = true
                            RetrofitClient.instance.trackPackage(packageId).enqueue(object : Callback<Map<String, Any>> {
                                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        navController.navigate("trackPackage/$packageId")
                                    } else {
                                        errorMessage = "Tracking mislukt: ${response.message()}"
                                    }
                                }

                                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                    isLoading = false
                                    errorMessage = "Netwerkfout: ${t.message}"
                                }
                            })
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .background(GreenSustainable.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Track Pakket",
                            tint = if (isLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else GreenSustainable
                        )
                    }

                    IconButton(
                        onClick = { onUpdate(packageId) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .background(DarkGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Update Pakket",
                            tint = if (isLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else DarkGreen
                        )
                    }

                    IconButton(
                        onClick = {
                            isLoading = true
                            RetrofitClient.instance.deletePackage(packageId).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        onDelete(packageId)
                                    } else {
                                        errorMessage = "Verwijderen mislukt: ${response.message()}"
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    isLoading = false
                                    errorMessage = "Netwerkfout: ${t.message}"
                                }
                            })
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Verwijder Pakket",
                            tint = if (isLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}