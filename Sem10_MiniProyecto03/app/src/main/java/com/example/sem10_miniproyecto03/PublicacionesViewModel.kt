
package com.example.sem10_miniproyecto03

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PublicacionesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao = AppDatabase
        .obtenerBaseDatos(application)
        .publicacionDao()

    private val repositorio = PublicacionRepository(dao)

    val publicaciones = repositorio.publicaciones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun guardarPublicacion(
        titulo: String,
        descripcion: String,
        fotoUri: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val nuevaPublicacion = Publicacion(
                titulo = titulo,
                descripcion = descripcion,
                fotoUri = fotoUri
            )

            repositorio.guardar(nuevaPublicacion)
        }
    }
}
