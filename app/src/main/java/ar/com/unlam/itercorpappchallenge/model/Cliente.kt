package ar.com.unlam.itercorpappchallenge.model

import java.io.Serializable

data class Cliente(
    val id: String? = null,
    var nombre: String? = null,
    var apellido: String? = null,
    val edad: Int? = null,
    var fechaNacimiento: String? = null
) : Serializable