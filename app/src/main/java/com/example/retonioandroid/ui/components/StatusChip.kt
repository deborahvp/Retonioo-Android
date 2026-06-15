package com.example.retonioandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.retonioandroid.domain.model.GarmentStatus
import com.example.retonioandroid.ui.theme.StatusAvailable
import com.example.retonioandroid.ui.theme.StatusAvailableBg
import com.example.retonioandroid.ui.theme.StatusCleaning
import com.example.retonioandroid.ui.theme.StatusCleaningBg
import com.example.retonioandroid.ui.theme.StatusRented
import com.example.retonioandroid.ui.theme.StatusRentedBg
import com.example.retonioandroid.ui.theme.StatusReserved
import com.example.retonioandroid.ui.theme.StatusReservedBg
import com.example.retonioandroid.ui.theme.StatusRetired
import com.example.retonioandroid.ui.theme.StatusRetiredBg

private data class StatusColors(val fg: Color, val bg: Color)

private fun colorsFor(status: GarmentStatus): StatusColors = when (status) {
    GarmentStatus.AVAILABLE -> StatusColors(StatusAvailable, StatusAvailableBg)
    GarmentStatus.RESERVED -> StatusColors(StatusReserved, StatusReservedBg)
    GarmentStatus.RENTED -> StatusColors(StatusRented, StatusRentedBg)
    GarmentStatus.IN_CLEANING -> StatusColors(StatusCleaning, StatusCleaningBg)
    GarmentStatus.RETIRED, GarmentStatus.UNKNOWN -> StatusColors(StatusRetired, StatusRetiredBg)
}

/** Etiqueta de estado con color semántico (verde/ámbar/azul/lila/gris). */
@Composable
fun StatusChip(
    status: GarmentStatus,
    modifier: Modifier = Modifier,
) {
    val c = colorsFor(status)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(c.bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = c.fg,
        )
    }
}
