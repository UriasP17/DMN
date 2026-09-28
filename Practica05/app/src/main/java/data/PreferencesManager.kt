package com.example.practica05.data

import android.content.Context

class PreferencesManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "configuracion_usuario",
        Context.MODE_PRIVATE
    )

    fun guardarNombre(nombre: String) {
        preferences.edit()
            .putString("nombre", nombre)
            .apply()
    }

    fun obtenerNombre(): String {
        return preferences.getString("nombre", "") ?: ""
    }

    fun guardarModoOscuro(activado: Boolean) {
        preferences.edit()
            .putBoolean("modo_oscuro", activado)
            .apply()
    }

    fun obtenerModoOscuro(): Boolean {
        return preferences.getBoolean("modo_oscuro", false)
    }

    fun guardarNotificaciones(activadas: Boolean) {
        preferences.edit()
            .putBoolean("notificaciones", activadas)
            .apply()
    }

    fun obtenerNotificaciones(): Boolean {
        return preferences.getBoolean("notificaciones", false)
    }
}