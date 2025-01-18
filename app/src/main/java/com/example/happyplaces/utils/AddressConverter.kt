package com.example.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationAddressConverter(private val context: Context)
{
    suspend fun getFormattedAddress(latitude: Double, longitude: Double): String
    {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address: Address = addresses[0]
                    address.getAddressLine(0) ?: "Address not available"
                }
                else {
                    "Address not found"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Unable to get address"
            }
        }
    }
}
