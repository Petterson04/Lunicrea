package com.ferpett.lunicrea.Entidad

import com.google.firebase.Timestamp

data class Consumo(
    var ninoid: String="",
    val nombreNino:String="",
    val producto: String="",
    val cantidad: Int=1,
    val precio: Double=0.0,
    val fecha: Timestamp= Timestamp.now(),
    var consumoId: String=""
){

}
