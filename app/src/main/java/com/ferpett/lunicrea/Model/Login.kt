package com.ferpett.lunicrea.Model

import com.google.firebase.auth.FirebaseAuth

class Login
{
    fun iniciarSesion(correo: String, contrasenia: String, onResult: (Boolean) -> Unit)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contrasenia)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful) // Retorna true si es exitoso, false si falla
            }
    }

    fun getAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun getUserId(): String?{
        val currentUser = getAuth().currentUser
        return currentUser?.uid // Retorna la id del usuario, en caso de no estar logueado retorna null
    }
}