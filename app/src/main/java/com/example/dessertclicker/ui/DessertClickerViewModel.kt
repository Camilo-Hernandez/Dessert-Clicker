package com.example.dessertclicker.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.R
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState(
    var revenue: Int = 0,
    var dessertsSold: Int = 0,
    val currentDessertIndex: Int = 0,
    val desserts: List<Dessert> = run {
        Datasource.dessertList
    },
    var currentDessertPrice: Int = desserts[currentDessertIndex].price,
    var currentDessertImageId: Int = desserts[currentDessertIndex].imageId,
)

class DessertClickerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        start()
    }

    private fun start() {
        _uiState.value = UiState()
    }

    val onDessertClicked = {
        with(_uiState.value) {
            // Update the revenue
            revenue = _uiState.value.currentDessertPrice.inc()
            dessertsSold++

            // Show the next dessert
            val dessertToShow = determineDessertToShow(desserts, dessertsSold)
            currentDessertImageId = dessertToShow.imageId
            currentDessertPrice = dessertToShow.price
        }
    }

    /**
     * Determine which dessert to show.
     */
    private fun determineDessertToShow(
        desserts: List<Dessert>,
        dessertsSold: Int,
    ): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }

        return dessertToShow
    }

    /**
     * Share desserts sold information using ACTION_SEND intent
     */
    fun shareSoldDessertsInformation(
        intentContext: Context,
        revenue: Int,
        dessertsSold: Int,
    ) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                intentContext.getString(R.string.share_text, dessertsSold, revenue)
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        try {
            ContextCompat.startActivity(intentContext, shareIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                intentContext,
                intentContext.getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

}
