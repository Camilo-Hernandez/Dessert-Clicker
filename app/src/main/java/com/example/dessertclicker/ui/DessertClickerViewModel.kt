package com.example.dessertclicker.ui

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val revenue: Int = 0,
    val dessertsSold: Int = 0,
    val currentDessertIndex: Int = 0,
    val currentDessertPrice: Int,
    @DrawableRes val currentDessertImageId: Int,
)

class DessertClickerViewModel : ViewModel() {
    private val desserts: List<Dessert> = Datasource.dessertList

    private val _uiState = MutableStateFlow(UiState(
        currentDessertPrice = desserts.first().price,
        currentDessertImageId = desserts.first().imageId,
    ))
    val uiState = _uiState.asStateFlow()

    val onDessertClicked = {
        _uiState.update {
            // Update the revenue
            val newDessertsSold = it.dessertsSold + 1
            val dessertToShow = determineDessertToShow(newDessertsSold)

            // Show the next dessert
            it.copy(
                revenue = it.revenue + it.currentDessertPrice,
                dessertsSold = newDessertsSold,
                currentDessertIndex = it.currentDessertIndex + 1,
                currentDessertPrice = dessertToShow.price,
                currentDessertImageId = dessertToShow.imageId,
            )
        }
    }

    /**
     * Determine which dessert to show.
     */
    private fun determineDessertToShow(
        dessertsSold: Int,
        desserts: List<Dessert> = Datasource.dessertList,
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

}
