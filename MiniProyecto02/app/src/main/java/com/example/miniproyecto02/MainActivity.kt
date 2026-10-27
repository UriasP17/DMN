package com.example.miniproyecto02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Naranja = Color(0xFFFF9800)
private val Negro = Color(0xFF0D0D10)
private val GrisTarjeta = Color(0xFF1C1C22)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Naranja,
                    onPrimary = Color.Black,
                    secondary = Color(0xFFFFCC80),
                    background = Negro,
                    onBackground = Color(0xFFF5F5F7),
                    surface = GrisTarjeta,
                    onSurface = Color(0xFFF5F5F7),
                    surfaceVariant = Color(0xFF2B2B33),
                    onSurfaceVariant = Color(0xFFCBCBD2)
                )
            ) {
                PantallaTareas()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    vm: TareaViewModel = viewModel()
) {
    val tareas by vm.tareas.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val guardando by vm.guardando.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    var mostrarBusqueda by rememberSaveable { mutableStateOf(false) }
    var busqueda by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf("Todas") }
    var orden by rememberSaveable { mutableStateOf("Más recientes") }

    var menuSuperior by remember { mutableStateOf(false) }
    var mostrarEditor by rememberSaveable { mutableStateOf(false) }
    var idEdicion by rememberSaveable { mutableStateOf<Int?>(null) }

    var pendienteEliminar by remember { mutableStateOf<TareaEntity?>(null) }
    var confirmarLimpieza by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { mensaje ->
            snackbar.showSnackbar(mensaje)
            vm.limpiarError()
        }
    }

    val completadas = tareas.count { it.completada }
    val pendientes = tareas.size - completadas

    val filtradas = tareas.filter { tarea ->
        val coincideBusqueda =
            tarea.titulo.contains(busqueda, ignoreCase = true) ||
                    tarea.descripcion.contains(busqueda, ignoreCase = true)

        val coincideEstado = when (filtro) {
            "Pendientes" -> !tarea.completada
            "Completadas" -> tarea.completada
            else -> true
        }

        coincideBusqueda && coincideEstado
    }

    val visibles = when (orden) {
        "Título A-Z" ->
            filtradas.sortedBy { it.titulo.lowercase(Locale.getDefault()) }

        "Pendientes primero" ->
            filtradas.sortedWith(
                compareBy<TareaEntity> { it.completada }
                    .thenByDescending { it.fechaCreacion }
            )

        "Más antiguas" ->
            filtradas.sortedBy { it.fechaCreacion }

        else ->
            filtradas.sortedByDescending { it.fechaCreacion }
    }

    Scaffold(
        containerColor = Negro,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "FOCUS / 02",
                            fontWeight = FontWeight.ExtraBold,
                            color = Naranja
                        )
                        Text(
                            "Gestor local de tareas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Negro,
                    actionIconContentColor = Naranja
                ),
                actions = {
                    IconButton(
                        onClick = {
                            mostrarBusqueda = !mostrarBusqueda
                            if (!mostrarBusqueda) busqueda = ""
                        }
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Mostrar u ocultar buscador"
                        )
                    }

                    Box {
                        IconButton(onClick = { menuSuperior = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menú de gestión"
                            )
                        }

                        DropdownMenu(
                            expanded = menuSuperior,
                            onDismissRequest = { menuSuperior = false }
                        ) {
                            listOf(
                                "Más recientes",
                                "Más antiguas",
                                "Título A-Z",
                                "Pendientes primero"
                            ).forEach { opcion ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (orden == opcion)
                                                "✓ $opcion"
                                            else
                                                opcion
                                        )
                                    },
                                    onClick = {
                                        orden = opcion
                                        menuSuperior = false
                                    }
                                )
                            }

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text("Borrar completadas") },
                                enabled = completadas > 0,
                                onClick = {
                                    menuSuperior = false
                                    confirmarLimpieza = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    idEdicion = null
                    mostrarEditor = true
                },
                containerColor = Naranja,
                contentColor = Color.Black,
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text("Nueva tarea") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                bottom = 110.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        "Menos pendientes.\nMás enfoque.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Organiza tus actividades sin conexión.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GrisTarjeta
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            "RESUMEN DE ACTIVIDADES",
                            color = Naranja,
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Contador("Total", tareas.size)
                            Contador("Pendientes", pendientes)
                            Contador("Listas", completadas)
                        }

                        Spacer(Modifier.height(18.dp))

                        LinearProgressIndicator(
                            progress = {
                                if (tareas.isEmpty()) 0f
                                else completadas.toFloat() / tareas.size
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = Naranja,
                            trackColor = Color(0xFF36363F)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Tu avance se guarda automáticamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (mostrarBusqueda) {
                item {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        label = { Text("Buscar título o descripción") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Todas",
                        "Pendientes",
                        "Completadas"
                    ).forEach { opcion ->
                        FilterChip(
                            selected = filtro == opcion,
                            onClick = { filtro = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }
            }

            item {
                Text(
                    "${visibles.size} resultados · $orden",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (cargando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (visibles.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = GrisTarjeta
                        )
                    ) {
                        Column(Modifier.padding(24.dp)) {
                            Text(
                                if (tareas.isEmpty())
                                    "Empieza con tu primera tarea"
                                else
                                    "No hay coincidencias",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                if (tareas.isEmpty())
                                    "Pulsa Nueva tarea para registrar una actividad."
                                else
                                    "Cambia la búsqueda o selecciona otro filtro.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = visibles,
                    key = { it.id }
                ) { tarea ->
                    TarjetaTarea(
                        tarea = tarea,
                        onEstado = { estado ->
                            vm.cambiarEstado(tarea, estado)
                        },
                        onEditar = {
                            idEdicion = tarea.id
                            mostrarEditor = true
                        },
                        onEliminar = {
                            pendienteEliminar = tarea
                        }
                    )
                }
            }
        }
    }

    if (mostrarEditor && !cargando) {
        val original = tareas.firstOrNull { it.id == idEdicion }

        EditorTarea(
            original = original,
            guardando = guardando,
            onCerrar = {
                if (!guardando) mostrarEditor = false
            },
            onGuardar = { titulo, descripcion ->
                vm.guardar(
                    original = original,
                    titulo = titulo,
                    descripcion = descripcion,
                    alGuardar = { mostrarEditor = false }
                )
            }
        )
    }

    pendienteEliminar?.let { tarea ->
        AlertDialog(
            onDismissRequest = { pendienteEliminar = null },
            title = { Text("¿Eliminar tarea?") },
            text = {
                Text(
                    "Se eliminará “${tarea.titulo}”. " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.eliminar(tarea)
                        pendienteEliminar = null
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendienteEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (confirmarLimpieza) {
        AlertDialog(
            onDismissRequest = { confirmarLimpieza = false },
            title = { Text("¿Borrar completadas?") },
            text = {
                Text(
                    "Se eliminarán todas las tareas completadas, " +
                            "incluidas las ocultas por la búsqueda o los filtros."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.eliminarCompletadas()
                        confirmarLimpieza = false
                    }
                ) {
                    Text(
                        "Borrar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarLimpieza = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun Contador(
    etiqueta: String,
    cantidad: Int
) {
    Column {
        Text(
            text = cantidad.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Naranja
        )

        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TarjetaTarea(
    tarea: TareaEntity,
    onEstado: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    val fecha = remember(tarea.fechaCreacion) {
        SimpleDateFormat(
            "dd/MM/yyyy · HH:mm",
            Locale.getDefault()
        ).format(Date(tarea.fechaCreacion))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GrisTarjeta
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onEstado
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (tarea.completada)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
                )

                if (tarea.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = if (tarea.completada)
                        "COMPLETADA"
                    else
                        "PENDIENTE",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (tarea.completada)
                        Color(0xFFA5D66D)
                    else
                        Naranja
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Creada: $fecha",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                IconButton(onClick = { menuAbierto = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones de ${tarea.titulo}"
                    )
                }

                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        onClick = {
                            menuAbierto = false
                            onEditar()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                if (tarea.completada)
                                    "Marcar como pendiente"
                                else
                                    "Marcar como completada"
                            )
                        },
                        onClick = {
                            menuAbierto = false
                            onEstado(!tarea.completada)
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                "Eliminar",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuAbierto = false
                            onEliminar()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorTarea(
    original: TareaEntity?,
    guardando: Boolean,
    onCerrar: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by rememberSaveable(original?.id) {
        mutableStateOf(original?.titulo.orEmpty())
    }

    var descripcion by rememberSaveable(original?.id) {
        mutableStateOf(original?.descripcion.orEmpty())
    }

    AlertDialog(
        onDismissRequest = {
            if (!guardando) onCerrar()
        },
        title = {
            Text(
                text = if (original == null)
                    "Nueva tarea"
                else
                    "Editar tarea",
                color = Naranja
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    enabled = !guardando,
                    label = { Text("Título obligatorio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    enabled = !guardando,
                    label = { Text("Descripción o materia") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = titulo.isNotBlank() && !guardando,
                onClick = { onGuardar(titulo, descripcion) }
            ) {
                Text(
                    if (guardando) "Guardando…" else "Guardar"
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !guardando,
                onClick = onCerrar
            ) {
                Text("Cancelar")
            }
        }
    )
}