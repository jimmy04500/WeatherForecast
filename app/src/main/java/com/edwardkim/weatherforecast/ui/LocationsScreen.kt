package com.edwardkim.weatherforecast.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edwardkim.weatherforecast.SearchLocationListItemInfo
import com.edwardkim.weatherforecast.SearchLocationListUiState
import com.edwardkim.weatherforecast.WeatherListViewModel

@Composable
fun LocationsScreen(
    viewModel: WeatherListViewModel = hiltViewModel(),
    onNavigateToWeatherDetail: (lat: Double, lon: Double, locationName: String) -> Unit = { _, _, _ -> }
) {
    val searchLocationListUiState =
        viewModel.searchListUiState.collectAsStateWithLifecycle().value
    val data = viewModel.savedLocations.collectAsStateWithLifecycle().value

    LocationsScreen(
        locationList = data,
        uiState = searchLocationListUiState,
        onSearch = viewModel::searchLocation,
        onNavigateToWeatherDetail = onNavigateToWeatherDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    locationList: List<SearchLocationListItemInfo>,
    uiState: SearchLocationListUiState,
    onSearch: (String) -> Unit = {},
    onNavigateToWeatherDetail: (lat: Double, lon: Double, locationName: String) -> Unit = { _, _, _ -> }
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    Column {
        SearchBar(
            query = query,
            onQueryChange = {
                query = it
                if (it.isNotEmpty()) {
                    onSearch(it)
                }
            },
            onSearch = {
                active = false
            },
            active = active,
            onActiveChange = {
                active = it
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        ) {
            if (uiState is SearchLocationListUiState.Success) {
                for (location in uiState.locations) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = location.name
                            )
                        },
                        modifier = Modifier.clickable {
                            onNavigateToWeatherDetail(
                                location.latitude,
                                location.longitude,
                                location.name
                            )
                        }
                    )
                }
            } else if (uiState is SearchLocationListUiState.NoResult) {
                ListItem(headlineContent = {
                    Text("No results")
                })
            }
        }
        for (location in locationList) {
            ListItem(
                headlineContent = {
                    Text(text = location.name)
                },
                trailingContent = {
                    Text(text = "79°C")
                },
                modifier = Modifier.clickable {
                    onNavigateToWeatherDetail(
                        location.latitude,
                        location.longitude,
                        location.name
                    )
                }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewWeatherListSuccess() {
//    WeatherList(
//        SearchLocationListUiState.Success(
//            listOf(
//                SearchLocationListItemInfo(
//                    "San Francisco",
//                    "CA",
//                    "US",
//                    123.0,
//                    123.0
//                )
//            )
//        )
//    )
//}

//@Preview(showBackground = true)
//@Composable
//fun PreviewWeatherListEmpty() {
//    WeatherList(
//        SearchLocationListUiState.NoResult
//    )
//}