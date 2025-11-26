package com.example.bolobudur.ui.screen.borobudurpedia

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.bolobudur.data.model.SiteItem
import com.example.bolobudur.ui.components.RowResultItem
import compose.icons.FeatherIcons
import compose.icons.feathericons.File

@Composable
fun SearchSiteResultSheet(
    sites: List<SiteItem>,
    onSiteClick: (SiteItem) -> Unit
) {
    LazyColumn {
        items(sites) { site ->
            RowResultItem(
                icon = FeatherIcons.File,
                title = site.name,
                subtitle = site.description.take(120) + "...",
                onClick = { onSiteClick(site) }
            )
        }
    }
}
