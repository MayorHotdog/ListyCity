package com.example.listycity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listycity.ui.theme.ListyCityTheme


class CityRepository {
    // Keep mutable app data private so other classes cannot change it directly.
    private val _cities = mutableStateListOf(
        "Edmonton", "Vancouver", "Moscow",
        "Sydney", "Berlin", "Vienna",
        "Tokyo", "Beijing", "Osaka",
        "New Delhi",
    )

    var addOptionSelected: MutableState<Boolean> = mutableStateOf<Boolean>(false)

    // Get a read-only list for the UI to display
    val cities: List<String>
        get() = _cities

    fun addCity(city: String) {
        _cities.add(city)
    }

    fun deleteCity(city: String) {
        _cities.remove(city)
    }

    var selectedCity: MutableState<String> = mutableStateOf<String>("")


}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val cityRepository = CityRepository()
        setContent {
            ListyCityTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CityListScreen(
                        cities = cityRepository.cities,
                        onAddCity = { cityRepository.addCity(it) },
                        onDeleteCity = { cityRepository.deleteCity(it) },
                        modifier = Modifier.padding(innerPadding),
                        selectedCity = cityRepository.selectedCity,
                        addOptionBool = cityRepository.addOptionSelected
                    )
                }
            }
        }
    }
}


// @Composable means this function describes part of the app's UI

@Composable
fun CityListScreen(
    // cities: List<String> is the list of city names that
    // this screen receives from MainActivity
    cities: List<String>,
    onAddCity: (String) -> Unit,
    onDeleteCity: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedCity: MutableState<String>,
    addOptionBool: MutableState<Boolean>
) {
    var newCityName by remember {mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize()) {

        Row(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = newCityName,
                onValueChange = { newCityName = it },
                label = { Text("City name") },
                modifier = Modifier.weight(1f),
                enabled = addOptionBool.value
            )

            Spacer(modifier = Modifier.width(80.dp))

            Button(
                onClick = {
                    addOptionBool.value = !addOptionBool.value
                }
            ) {
                Text("Add City")
            }

            Button(
                enabled = selectedCity.value.isNotBlank() || addOptionBool.value,
                onClick = {
                    if (addOptionBool.value) {
                        if (newCityName.isNotBlank()) {
                            onAddCity(newCityName)
                            newCityName = ""
                        }
                    } else {
                        if (selectedCity.value.isNotBlank()) {
                            onDeleteCity(selectedCity.value)
                            selectedCity.value = ""
                        }
                    }
                }
            ) {
                if (addOptionBool.value) {
                    Text("Confirm City")
                } else {
                    Text("Delete City")
                }
            }


        }
        Spacer(modifier = Modifier.height(50.dp))

        // LazyColumn is the Compose for a basic scrolling ListView
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(cities) { city ->
                CityRow(city = city, selectedCity=selectedCity)

            }
        }
    }
}

@Composable
fun CityRow(city: String, selectedCity: MutableState<String>){
    var displayText = city
    if (city == selectedCity.value) {
        displayText = "~~ $city ~~"
    }
    Button(
        onClick = {
            // We need to change selectedCity but cant because this is a child.
            if (selectedCity.value == city) {
                selectedCity.value = ""
            } else {
                selectedCity.value = city
            }
        },
    ) {
        Text(
            text = displayText,
            fontSize = 28.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        )}

}
