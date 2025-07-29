package com.ferpett.lunicrea.Entidad

import com.google.firebase.Timestamp


data class Visitas(
    val Dia: Timestamp = Timestamp.now(),
    val idNino: String = "",
    var idVisita: String = "",
    val nombreNino: String = ""
){}

