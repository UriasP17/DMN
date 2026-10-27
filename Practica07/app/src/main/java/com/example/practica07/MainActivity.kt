package com.example.practica07

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

// Modelo de datos para las tareas
data class Tarea(
    val id: Int,
    val titulo: String,
    val materia: String,
    val completada: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaGestor()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestor() {
    val context = LocalContext.current

    // Lista interactiva de tareas iniciales
    var listaTareas by remember {
        mutableStateOf(
            listOf(
                Tarea(1, "Configurar ToolBar y Menús", "Desarrollo Móvil Nativo"),
                Tarea(2, "Subir cambios a GitHub", "DMN"),
                Tarea(3, "Generar reporte PDF con capturas", "DMN"),
                Tarea(4, "Estudiar arquitectura MVVM", "Ingeniería de Software")
            )
        )
    }

    var busquedaActiva by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }
    var menuOverflowAbierto by remember { mutableStateOf(false) }
    var dialogNuevaTarea by remember { mutableStateOf(false) }

    // Filtrado en vivo según el buscador
    val tareasFiltradas = listaTareas.filter {
        it.titulo.contains(textoBusqueda, ignoreCase = true) ||
                it.materia.contains(textoBusqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (busquedaActiva) {
                        TextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            placeholder = { Text("Buscar tarea...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("DMN - Gestor de Prácticas")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Botón para activar/cerrar el buscador
                    IconButton(onClick = {
                        busquedaActiva = !busquedaActiva
                        if (!busquedaActiva) textoBusqueda = ""
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }

                    // Botón de 3 puntos (Overflow Menu)
                    IconButton(onClick = { menuOverflowAbierto = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }

                    // Opciones del Menú Superior (OptionsMenu)
                    DropdownMenu(
                        expanded = menuOverflowAbierto,
                        onDismissRequest = { menuOverflowAbierto = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ordenar alfabéticamente") },
                            onClick = {
                                listaTareas = listaTareas.sortedBy { it.titulo }
                                menuOverflowAbierto = false
                                Toast.makeText(context, "Lista ordenada A-Z", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mostrar pendientes primero") },
                            onClick = {
                                listaTareas = listaTareas.sortedBy { it.completada }
                                menuOverflowAbierto = false
                                Toast.makeText(context, "Pendientes al inicio", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Restablecer lista") },
                            onClick = {
                                listaTareas = listOf(
                                    Tarea(1, "Configurar ToolBar y Menús", "Desarrollo Móvil Nativo"),
                                    Tarea(2, "Subir cambios a GitHub", "DMN"),
                                    Tarea(3, "Generar reporte PDF con capturas", "DMN")
                                )
                                menuOverflowAbierto = false
                                Toast.makeText(context, "Lista restaurada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogNuevaTarea = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Tarea", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Tareas Activas (${tareasFiltradas.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (tareasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron registros", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(tareasFiltradas, key = { it.id }) { tarea ->
                        TarjetaTareaItem(
                            tarea = tarea,
                            onToggleCompletar = {
                                listaTareas = listaTareas.map {
                                    if (it.id == tarea.id) it.copy(completada = !it.completada) else it
                                }
                            },
                            onEliminar = {
                                listaTareas = listaTareas.filter { it.id != tarea.id }
                                Toast.makeText(context, "Tarea eliminada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Diálogo para crear nueva tarea
        if (dialogNuevaTarea) {
            var nuevoTitulo by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { dialogNuevaTarea = false },
                title = { Text("Nueva Actividad") },
                text = {
                    OutlinedTextField(
                        value = nuevoTitulo,
                        onValueChange = { nuevoTitulo = it },
                        label = { Text("Descripción de la tarea") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (nuevoTitulo.isNotBlank()) {
                            val nuevoId = (listaTareas.maxOfOrNull { it.id } ?: 0) + 1
                            listaTareas = listaTareas + Tarea(nuevoId, nuevoTitulo, "DMN")
                            dialogNuevaTarea = false
                            Toast.makeText(context, "Tarea agregada", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { dialogNuevaTarea = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun TarjetaTareaItem(
    tarea: Tarea,
    onToggleCompletar: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuPopupAbierto by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tarea.completada) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = tarea.materia,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Botón con PopupMenu anclado a cada tarjeta
            Box {
                IconButton(onClick = { menuPopupAbierto = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menú de tarjeta")
                }

                DropdownMenu(
                    expanded = menuPopupAbierto,
                    onDismissRequest = { menuPopupAbierto = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (tarea.completada) "Marcar como pendiente" else "Completar tarea") },
                        onClick = {
                            menuPopupAbierto = false
                            onToggleCompletar()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar de la lista") },
                        onClick = {
                            menuPopupAbierto = false
                            onEliminar()
                        }
                    )
                }
            }
        }
    }
}