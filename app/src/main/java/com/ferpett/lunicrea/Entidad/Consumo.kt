package com.ferpett.lunicrea.Entidad

import com.google.firebase.Timestamp

data class Consumo(
    var ninoid: String,
    val nombreNino:String,
    val producto: String,
    val cantidad: Int,
    val precio: Double,
    val fecha: Timestamp,
    var consumoId: String
){

}
