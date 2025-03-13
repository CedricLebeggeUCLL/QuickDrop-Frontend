// com.example.quickdropapp.composables/PackageItem.kt
package com.example.quickdropapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Package
import com.example.quickdropapp.network.RetrofitClient
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pakket ID: ${packageItem.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Text(
                    text = "Omschrijving: ${packageItem.description ?: "Geen omschrijving"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Status: ${packageItem.status}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            // Actieknoppen
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        isLoading = true
                        RetrofitClient.instance.trackPackage(packageItem.id).enqueue(object : Callback<Map<String, Any>> {
                            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    navController.navigate("trackPackage/${packageItem.id}")
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
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Track Pakket",
                        tint = if (isLoading) Color.Gray else Color.Green
                    )
                }

                IconButton(
                    onClick = { onUpdate(packageItem.id) },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Update Pakket",
                        tint = Color.Blue
                    )
                }

                IconButton(
                    onClick = {
                        isLoading = true
                        RetrofitClient.instance.deletePackage(packageItem.id).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    onDelete(packageItem.id)
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
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Verwijder Pakket",
                        tint = if (isLoading) Color.Gray else Color.Red
                    )
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}