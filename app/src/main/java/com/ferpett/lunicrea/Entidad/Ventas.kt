package com.ferpett.lunicrea.Entidad

import com.google.firebase.Timestamp

data class Ventas(
    var ventaId: String ="",
    val fecha: Timestamp? = Timestamp.now(),
    val total: Double? =0.0,
    val productosTotal: Double?=0.0,
    val nombreNino: String? ="",
    val categoria: String?=""
)
