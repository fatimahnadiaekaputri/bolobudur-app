package com.example.bolobudur.ui.screen.home.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.runtime.Composable
import com.example.bolobudur.data.model.SearchResponse
import com.example.bolobudur.ui.components.RowResultItem
import compose.icons.FeatherIcons
import compose.icons.feathericons.Book
import compose.icons.feathericons.File

@Composable
fun SearchResultSheet(
    searchResponse: SearchResponse?,
    onPoiClick: (Double, Double, String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSiteClick: (String) -> Unit
) {
    searchResponse?.data?.let { data ->
        LazyColumn {
            items(data.poi.features) { feature ->
                val props = feature.properties
                RowResultItem(
                    icon = Icons.Outlined.Place,
                    title = props.label,
                    subtitle = props.lokasi,
                    onClick = {
                        val coords = feature.geometry?.coordinates
                        if (coords != null && coords.size == 2) {
                            onPoiClick(coords[1], coords[0], props.label)
                        }
                    }
                )
            }
            items(data.categories.features) { feature ->
                RowResultItem(
                    icon = FeatherIcons.Book,
                    title = feature.properties.label,
                    subtitle = "Kategori: ${feature.properties.description}",
                    onClick = { onCategoryClick(feature.properties.label) }
                )
            }
            items(data.sites.features) { feature ->
                RowResultItem(
                    icon = FeatherIcons.File,
                    title = feature.properties.label,
                    subtitle = feature.properties.description,
                    onClick = { onSiteClick(feature.properties.label) }
                )
            }
        }
    }
}