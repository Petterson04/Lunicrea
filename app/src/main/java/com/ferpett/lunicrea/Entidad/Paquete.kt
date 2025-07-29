package com.ferpett.lunicrea.Entidad

data class Paquete(
    val costo: Double = 0.0,
    val duracion: Int = 0,
    val nombre: String = "",
    val ninoId: String = "",
    var paqueteId: String?="",
    var nombreNino: String?=""
){
    fun toMap(): Map<String, Any?> = mapOf(
        "costo" to costo,
        "duracion" to duracion,
        "ninoId" to ninoId,
        "nombre" to nombre
    )
}
