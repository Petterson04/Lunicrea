package com.ferpett.lunicrea.Entidad

import com.ferpett.lunicrea.Entidad.Nino

data class Producto(
    var productoId: String = "",
    val categoria: String = "",
    var cantidad: Int = 0,
    val precio: Double = 0.0,
    val nombreProducto: String = "",


    ){
    fun toMap(): Map<String, Any?> = mapOf(
        "categoria"         to categoria,
        "cantidad"           to cantidad,
        "precio"            to precio,
        "nombreProducto"    to nombreProducto,
    )
}
