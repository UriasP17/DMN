package com.example.miniproyecto01.data

import android.content.Context

class PreferencesManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "registro_estudiante",
        Context.MODE_PRIVATE
    )

    fun guardarMatricula(matricula: String) {
        preferences.edit()
            .putString("matricula", matricula)
            .apply()
    }

    fun obtenerMatricula(): String {
        return preferences.getString("matricula", "") ?: ""
    }

    fun guardarEstatusActivo(activo: Boolean) {
        preferences.edit()
            .putBoolean("estatus_activo", activo)
            .apply()
    }

    fun obtenerEstatusActivo(): Boolean {
        return preferences.getBoolean("estatus_activo", true)
    }
}