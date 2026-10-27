package com.example.practica08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Paleta oscura personalizada: Negro profundo y Naranjas eléctricos
val ColorFondo = Color(0xFF121212)
val ColorSuperficie = Color(0xFF1E1E1E)
val ColorNaranjaPrimario = Color(0xFFFF6D00)
val ColorNaranjaSecundario = Color(0xFFFF9E80)
val ColorTexto = Color(0xFFEEEEEE)
val ColorTextoSecundario = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coloresOscuros = darkColorScheme(
                primary = ColorNaranjaPrimario,
                onPrimary = Color.Black,
                background = ColorFondo,
                surface = ColorSuperficie,
                onBackground = ColorTexto,
                onSurface = ColorTexto
            )

            MaterialTheme(colorScheme = coloresOscuros) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaTareas()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(miViewModel: TareaViewModel = viewModel()) {
    val listaTareas by miViewModel.tareas.collectAsState()
    var dialogoAbierto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DMN - Persistencia Room",
                        fontWeight = FontWeight.Bold,
                        color = ColorNaranjaPrimario
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF000000)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogoAbierto = true },
                containerColor = ColorNaranjaPrimario,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Tarea")
            }
        }
    ) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(16.dp)
        ) {
            Text(
                text = "Registros en SQLite (${listaTareas.size})",
                color = ColorNaranjaSecundario,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (listaTareas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay registros en la base de datos",
                        color = ColorTextoSecundario
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(listaTareas, key = { it.id }) { tarea ->
                        ItemTareaCard(
                            tarea = tarea,
                            onToggle = { miViewModel.alternarEstado(tarea) },
                            onDelete = { miViewModel.eliminarTarea(tarea) }
                        )
                    }
                }
            }
        }

        if (dialogoAbierto) {
            DialogoRegistro(
                onDismiss = { dialogoAbierto = false },
                onConfirmar = { titulo, desc ->
                    miViewModel.agregarTarea(titulo, desc)
                    dialogoAbierto = false
                }
            )
        }
    }
}

@Composable
fun ItemTareaCard(
    tarea: TareaEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSuperficie),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Estado",
                    tint = if (tarea.completada) ColorNaranjaPrimario else Color(0xFF444444)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    color = ColorTexto,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        color = ColorTextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFFF5252)
                )
            }
        }
    }
}

@Composable
fun DialogoRegistro(
    onDismiss: () -> Unit,
    onConfirmar: (String, String) -> Unit
) {
    var txtTitulo by remember { mutableStateOf("") }
    var txtDescripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ColorSuperficie,
        title = {
            Text(text = "Nuevo Registro Room", color = ColorNaranjaPrimario)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = txtTitulo,
                    onValueChange = { txtTitulo = it },
                    label = { Text("Título de la tarea") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorNaranjaPrimario,
                        focusedLabelColor = ColorNaranjaPrimario,
                        unfocusedTextColor = ColorTexto,
                        focusedTextColor = ColorTexto
                    )
                )
                OutlinedTextField(
                    value = txtDescripcion,
                    onValueChange = { txtDescripcion = it },
                    label = { Text("Descripción o Materia") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorNaranjaPrimario,
                        focusedLabelColor = ColorNaranjaPrimario,
                        unfocusedTextColor = ColorTexto,
                        focusedTextColor = ColorTexto
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (txtTitulo.isNotBlank()) {
                        onConfirmar(txtTitulo, txtDescripcion)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaPrimario)
            ) {
                Text("Guardar", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = ColorTextoSecundario)
            }
        }
    )
}