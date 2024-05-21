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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.edwardkim.weatherforecast.R

@Composable
fun AppNavigationBar(
    items: List<NavigationBarItemInfo>,
    onItemClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedItem by remember {
        mutableIntStateOf(0)
    }
    NavigationBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick =
                {
                    selectedItem = index
                    onItemClick(index)
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