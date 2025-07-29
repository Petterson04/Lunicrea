package com.ferpett.lunicrea.Entidad

import com.google.firebase.Timestamp

data class Nino(
    var userId: String = "",
    var nombre: String = "",
    var edad: String="",
    var nombrePadres: String = "",
    var nombreAutorizado: String = "",
    var numeroEmergencia: String = "",
    var estado: Boolean= false,

    // ──────── Registro de horas ────────
    var horaEntrada: Timestamp? = null,
    var horaSalida:  Timestamp? = null,

    var horasTotales: Int = 0,

) {
    /**
     *  Útil para operaciones update() parciales.
     *  Sólo incluye campos que deben subir a Firestore.
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "userId"            to userId,
        "nombre"            to nombre,
        "edad"              to edad,
        "nombrePadres"      to nombrePadres,
        "nombreAutorizado"  to nombreAutorizado,
        "numeroEmergencia"  to numeroEmergencia,
        "estado"            to estado,
        "horaEntrada"       to horaEntrada,
        "horaSalida"        to horaSalida,
        "horasTotales"      to horasTotales
    )
}
