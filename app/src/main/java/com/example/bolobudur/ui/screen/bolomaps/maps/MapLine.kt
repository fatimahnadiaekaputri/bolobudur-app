package com.example.bolobudur.ui.screen.bolomaps.maps

import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.StyleExtensionImpl
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.SymbolPlacement
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource

fun Style.addLineAndLabelLayer(geoJsonString: String) {
    if (!styleSourceExists("line-source")) {
        addSource(
            geoJsonSource("line-source") {
                data(geoJsonString)
            }
        )
    }

    if (!styleLayerExists("line-layer")) {
        addLayer(
            lineLayer("line-layer", "line-source") {
                lineColor("#C0C0C0")
                lineWidth(5.0)
                lineOpacity(0.6)
            }
        )
    }

//    if (!styleLayerExists("line-label-layer")) {
//        addLayer(
//            symbolLayer("line-label-layer", "line-source") {
//                textField("{name}")
//                textSize(10.0)
//                textColor("#555555")
//                textOpacity(0.8)
//                textHaloColor("#FFFFFF")
//                textHaloWidth(1.0)
//                symbolPlacement(SymbolPlacement.LINE)
//                textFont(listOf("Noto Sans Regular"))
//            }
//        )
//    }
}