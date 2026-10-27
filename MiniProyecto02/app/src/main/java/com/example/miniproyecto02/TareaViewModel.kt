package com.example.miniproyecto02

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.obtener(application).tareaDao()

    private val _tareas = MutableStateFlow<List<TareaEntity>>(emptyList())
    val tareas = _tareas.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando = _cargando.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando = _guardando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                dao.observarTareas().collect { lista ->
                    _tareas.value = lista
                    _cargando.value = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _cargando.value = false
                _error.value = "No se pudieron cargar las tareas."
            }
        }
    }

    fun guardar(
        original: TareaEntity?,
        titulo: String,
        descripcion: String,
        alGuardar: () -> Unit
    ) {
        if (titulo.isBlank() || _guardando.value) return

        _guardando.value = true

        viewModelScope.launch {
            try {
                if (original == null) {
                    dao.insertar(
                        TareaEntity(
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim()
                        )
                    )
                } else {
                    dao.actualizar(
                        original.copy(
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim()
                        )
                    )
                }

                alGuardar()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo guardar la tarea."
            } finally {
                _guardando.value = false
            }
        }
    }

    private fun ejecutar(operacion: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                operacion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo realizar la operación."
            }
        }
    }

    fun cambiarEstado(tarea: TareaEntity, completada: Boolean) {
        ejecutar {
            dao.actualizar(tarea.copy(completada = completada))
        }
    }

    fun eliminar(tarea: TareaEntity) {
        ejecutar {
            dao.eliminar(tarea)
        }
    }

    fun eliminarCompletadas() {
        ejecutar {
            dao.eliminarCompletadas()
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}