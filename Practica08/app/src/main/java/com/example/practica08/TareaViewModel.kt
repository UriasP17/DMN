package com.example.practica08

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).tareaDao()

    val tareas: StateFlow<List<TareaEntity>> = dao.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarTarea(titulo: String, descripcion: String) {
        viewModelScope.launch {
            dao.insertar(TareaEntity(titulo = titulo, descripcion = descripcion))
        }
    }

    fun alternarEstado(tarea: TareaEntity) {
        viewModelScope.launch {
            dao.actualizar(tarea.copy(completada = !tarea.completada))
        }
    }

    fun eliminarTarea(tarea: TareaEntity) {
        viewModelScope.launch {
            dao.eliminar(tarea)
        }
    }
}