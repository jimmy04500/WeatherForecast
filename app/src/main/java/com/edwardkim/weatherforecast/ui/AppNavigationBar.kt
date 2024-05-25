package com.edwardkim.weatherforecast.ui

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.edwardkim.weatherforecast.R

@Composable
fun AppNavigationBar(
    items: List<NavigationBarItemInfo>,
    onNavigateToItem: (Int) -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    if (selectedItem != index) {
                        selectedItem = index
                        onNavigateToItem(index)
                    }
                },
                label = { Text(item.label) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                }
            )
        }
    }
}

data class NavigationBarItemInfo(
    val label: String,
    @DrawableRes val icon: Int,
    val contentDescription: String
)


@Preview(showBackground = true)
@Composable
fun PreviewNavigationBar() {
    var items = listOf(
        NavigationBarItemInfo("Home", R.drawable.ic_home, "Home"),
        NavigationBarItemInfo("List", R.drawable.ic_list, "List"),
        NavigationBarItemInfo("Settings", R.drawable.ic_settings, "Settings")
    )
    AppNavigationBar(items = items)
}