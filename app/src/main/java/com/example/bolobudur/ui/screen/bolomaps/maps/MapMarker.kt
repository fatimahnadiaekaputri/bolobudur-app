package com.example.bolobudur.ui.screen.bolomaps.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.example.bolobudur.R
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource

fun Style.addPoiLayers(context: Context, geoJsonString: String) {
    if (!styleSourceExists("poi-source")) {
        addSource(
            geoJsonSource("poi-source") {
                data(geoJsonString)
            }
        )
    }

    val icons = mapOf(
        "icon_pintu" to R.drawable.icon_gate,
        "icon_arca" to R.drawable.icon_stupa,
        "icon_stupa" to R.drawable.icon_stupa,
        "icon_default" to R.drawable.icon_gate
    )

    icons.forEach { (key, resId) ->
        val drawable = ContextCompat.getDrawable(context, resId)
        if (drawable is BitmapDrawable) {
            val bitmap: Bitmap = drawable.bitmap
                addImage(key, bitmap)
        }
    }

    // Circle layer (opsional, buat efek latar POI)
    if (!styleLayerExists("poi-circle")) {
        addLayer(
            circleLayer("poi-circle", "poi-source") {
                circleColor("#C19A6B")
                circleOpacity(0.6)
                circleRadius(8.0)
                circleStrokeColor("#8B5E3C")
                circleStrokeWidth(1.5)
            }
        )
    }

    // Icon layer
    if (!styleLayerExists("poi-icon")) {
        addLayer(
            symbolLayer("poi-icon", "poi-source") {
                iconImage(getPoiIconExpression())
                iconSize(0.5)
                iconAllowOverlap(true)
            }
        )
    }

//    // Label layer
//    if (!styleLayerExists("poi-label")) {
//        addLayer(
//            symbolLayer("poi-label", "poi-source") {
//                textField("{name}")
//                textColor("#000000")
//                textSize(11.0)
//                textOffset(listOf(0.0, 1.2))
//                textHaloColor("#FFFFFF")
//                textHaloWidth(1.5)
//                textAllowOverlap(true)
//            }
//        )
//    }
}

fun getPoiIconExpression(): Expression {
    return Expression.match(
        Expression.get("poi"), // ambil nilai properti 'poi' dari feature
        Expression.literal("pintu"), Expression.literal("icon_pintu"),
        Expression.literal("arca"), Expression.literal("icon_arca"),
        Expression.literal("stupa"), Expression.literal("icon_stupa"),
        Expression.literal("icon_default") // default (wajib!)
    )
}

