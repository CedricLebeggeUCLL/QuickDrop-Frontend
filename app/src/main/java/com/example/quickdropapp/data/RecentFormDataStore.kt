package com.example.quickdropapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quickdropapp.models.Address
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore-definitie
val Context.recentFormDataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_form_data")

object RecentFormDataStore {
    // Sleutels voor SendPackageScreen
    private val RECIPIENT_NAME = stringPreferencesKey("recipient_name")
    private val PICKUP_LOCATION_NAME = stringPreferencesKey("pickup_location_name") // Nieuw
    private val PACKAGE_HOLDER_NAME = stringPreferencesKey("package_holder_name") // Nieuw
    private val PACKAGE_DESCRIPTION = stringPreferencesKey("package_description")
    private val PACKAGE_WEIGHT = stringPreferencesKey("package_weight")
    private val PICKUP_STREET = stringPreferencesKey("pickup_street")
    private val PICKUP_HOUSE_NUMBER = stringPreferencesKey("pickup_house_number")
    private val PICKUP_POSTAL_CODE = stringPreferencesKey("pickup_postal_code")
    private val PICKUP_CITY = stringPreferencesKey("pickup_city")
    private val PICKUP_COUNTRY = stringPreferencesKey("pickup_country")
    private val PICKUP_EXTRA_INFO = stringPreferencesKey("pickup_extra_info")
    private val DROPOFF_STREET = stringPreferencesKey("dropoff_street")
    private val DROPOFF_HOUSE_NUMBER = stringPreferencesKey("dropoff_house_number")
    private val DROPOFF_POSTAL_CODE = stringPreferencesKey("dropoff_postal_code")
    private val DROPOFF_CITY = stringPreferencesKey("dropoff_city")
    private val DROPOFF_COUNTRY = stringPreferencesKey("dropoff_country")
    private val DROPOFF_EXTRA_INFO = stringPreferencesKey("dropoff_extra_info")

    // Sleutels voor StartDeliveryScreen
    private val START_STREET = stringPreferencesKey("start_street")
    private val START_HOUSE_NUMBER = stringPreferencesKey("start_house_number")
    private val START_POSTAL_CODE = stringPreferencesKey("start_postal_code")
    private val START_CITY = stringPreferencesKey("start_city")
    private val START_COUNTRY = stringPreferencesKey("start_country")
    private val START_EXTRA_INFO = stringPreferencesKey("start_extra_info")
    private val DEST_STREET = stringPreferencesKey("dest_street")
    private val DEST_HOUSE_NUMBER = stringPreferencesKey("dest_house_number")
    private val DEST_POSTAL_CODE = stringPreferencesKey("dest_postal_code")
    private val DEST_CITY = stringPreferencesKey("dest_city")
    private val DEST_COUNTRY = stringPreferencesKey("dest_country")
    private val DEST_EXTRA_INFO = stringPreferencesKey("dest_extra_info")
    private val PICKUP_RADIUS = stringPreferencesKey("pickup_radius")
    private val DROPOFF_RADIUS = stringPreferencesKey("dropoff_radius")

    // Opslaan voor SendPackageScreen
    suspend fun saveSendPackageData(
        context: Context,
        recipientName: String,
        pickupAddress: Address,
        dropoffAddress: Address,
        packageDescription: String,
        packageWeight: String,
        pickupLocationName: String = "", // Nieuw
        packageHolderName: String = "" // Nieuw
    ) {
        context.recentFormDataStore.edit { preferences ->
            preferences[RECIPIENT_NAME] = recipientName
            preferences[PICKUP_LOCATION_NAME] = pickupLocationName
            preferences[PACKAGE_HOLDER_NAME] = packageHolderName
            preferences[PACKAGE_DESCRIPTION] = packageDescription
            preferences[PACKAGE_WEIGHT] = packageWeight
            preferences[PICKUP_STREET] = pickupAddress.street_name
            preferences[PICKUP_HOUSE_NUMBER] = pickupAddress.house_number
            preferences[PICKUP_POSTAL_CODE] = pickupAddress.postal_code
            preferences[PICKUP_CITY] = pickupAddress.city ?: ""
            preferences[PICKUP_COUNTRY] = pickupAddress.country ?: ""
            preferences[PICKUP_EXTRA_INFO] = pickupAddress.extra_info ?: ""
            preferences[DROPOFF_STREET] = dropoffAddress.street_name
            preferences[DROPOFF_HOUSE_NUMBER] = dropoffAddress.house_number
            preferences[DROPOFF_POSTAL_CODE] = dropoffAddress.postal_code
            preferences[DROPOFF_CITY] = dropoffAddress.city ?: ""
            preferences[DROPOFF_COUNTRY] = dropoffAddress.country ?: ""
            preferences[DROPOFF_EXTRA_INFO] = dropoffAddress.extra_info ?: ""
        }
    }

    // Opslaan voor StartDeliveryScreen
    suspend fun saveStartDeliveryData(
        context: Context,
        startAddress: Address,
        destinationAddress: Address,
        pickupRadius: String,
        dropoffRadius: String
    ) {
        context.recentFormDataStore.edit { preferences ->
            preferences[START_STREET] = startAddress.street_name
            preferences[START_HOUSE_NUMBER] = startAddress.house_number
            preferences[START_POSTAL_CODE] = startAddress.postal_code
            preferences[START_CITY] = startAddress.city ?: ""
            preferences[START_COUNTRY] = startAddress.country ?: ""
            preferences[START_EXTRA_INFO] = startAddress.extra_info ?: ""
            preferences[DEST_STREET] = destinationAddress.street_name
            preferences[DEST_HOUSE_NUMBER] = destinationAddress.house_number
            preferences[DEST_POSTAL_CODE] = destinationAddress.postal_code
            preferences[DEST_CITY] = destinationAddress.city ?: ""
            preferences[DEST_COUNTRY] = destinationAddress.country ?: ""
            preferences[DEST_EXTRA_INFO] = destinationAddress.extra_info ?: ""
            preferences[PICKUP_RADIUS] = pickupRadius
            preferences[DROPOFF_RADIUS] = dropoffRadius
        }
    }

    // Ophalen voor SendPackageScreen
    fun getRecentSendPackageDataFlow(context: Context): Flow<SendPackageData> {
        return context.recentFormDataStore.data.map { preferences ->
            SendPackageData(
                recipientName = preferences[RECIPIENT_NAME] ?: "",
                pickupLocationName = preferences[PICKUP_LOCATION_NAME] ?: "", // Nieuw
                packageHolderName = preferences[PACKAGE_HOLDER_NAME] ?: "", // Nieuw
                pickupAddress = Address(
                    street_name = preferences[PICKUP_STREET] ?: "",
                    house_number = preferences[PICKUP_HOUSE_NUMBER] ?: "",
                    postal_code = preferences[PICKUP_POSTAL_CODE] ?: "",
                    city = preferences[PICKUP_CITY] ?: "",
                    country = preferences[PICKUP_COUNTRY] ?: "",
                    extra_info = preferences[PICKUP_EXTRA_INFO]?.takeIf { it.isNotBlank() }
                ),
                dropoffAddress = Address(
                    street_name = preferences[DROPOFF_STREET] ?: "",
                    house_number = preferences[DROPOFF_HOUSE_NUMBER] ?: "",
                    postal_code = preferences[DROPOFF_POSTAL_CODE] ?: "",
                    city = preferences[DROPOFF_CITY] ?: "",
                    country = preferences[DROPOFF_COUNTRY] ?: "",
                    extra_info = preferences[DROPOFF_EXTRA_INFO]?.takeIf { it.isNotBlank() }
                ),
                packageDescription = preferences[PACKAGE_DESCRIPTION] ?: "",
                packageWeight = preferences[PACKAGE_WEIGHT] ?: ""
            )
        }
    }

    // Ophalen voor StartDeliveryScreen
    fun getRecentStartDeliveryDataFlow(context: Context): Flow<StartDeliveryData> {
        return context.recentFormDataStore.data.map { preferences ->
            StartDeliveryData(
                startAddress = Address(
                    street_name = preferences[START_STREET] ?: "",
                    house_number = preferences[START_HOUSE_NUMBER] ?: "",
                    postal_code = preferences[START_POSTAL_CODE] ?: "",
                    city = preferences[START_CITY] ?: "",
                    country = preferences[START_COUNTRY] ?: "",
                    extra_info = preferences[START_EXTRA_INFO]?.takeIf { it.isNotBlank() }
                ),
                destinationAddress = Address(
                    street_name = preferences[DEST_STREET] ?: "",
                    house_number = preferences[DEST_HOUSE_NUMBER] ?: "",
                    postal_code = preferences[DEST_POSTAL_CODE] ?: "",
                    city = preferences[DEST_CITY] ?: "",
                    country = preferences[DEST_COUNTRY] ?: "",
                    extra_info = preferences[DEST_EXTRA_INFO]?.takeIf { it.isNotBlank() }
                ),
                pickupRadius = preferences[PICKUP_RADIUS] ?: "30.0",
                dropoffRadius = preferences[DROPOFF_RADIUS] ?: "40.0"
            )
        }
    }
}

// Data classes voor het ophalen van recente gegevens
data class SendPackageData(
    val recipientName: String,
    val pickupLocationName: String, // Nieuw
    val packageHolderName: String, // Nieuw
    val pickupAddress: Address,
    val dropoffAddress: Address,
    val packageDescription: String,
    val packageWeight: String
)

data class StartDeliveryData(
    val startAddress: Address,
    val destinationAddress: Address,
    val pickupRadius: String,
    val dropoffRadius: String
)