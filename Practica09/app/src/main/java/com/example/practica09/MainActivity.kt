package com.example.practica09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

private val Naranja = Color(0xFFFF9800)
private val Negro = Color(0xFF0E0E11)
private val GrisTarjeta = Color(0xFF1D1D24)

data class Foto(
    val id: Int,
    val titulo: String
) {
    val miniatura: String
        get() = "https://picsum.photos/seed/practica09-$id/480/360"

    val ampliada: String
        get() = "https://picsum.photos/seed/practica09-$id/1200/900"
}

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
                    onBackground = Color(0xFFF4F4F6),
                    surface = GrisTarjeta,
                    onSurface = Color(0xFFF4F4F6),
                    surfaceVariant = Color(0xFF303039),
                    onSurfaceVariant = Color(0xFFCACAD2)
                )
            ) {
                PantallaGaleria()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaGaleria() {

    val fotos = remember {
        List(60) { posicion ->
            Foto(
                id = posicion + 1,
                titulo = "Fotografía ${posicion + 1}"
            )
        }
    }

    var idSeleccionado by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var cantidadColumnas by rememberSaveable {
        mutableIntStateOf(0)
    }

    val estadoGrid = rememberLazyGridState()

    val columnas = when (cantidadColumnas) {
        2 -> GridCells.Fixed(2)
        3 -> GridCells.Fixed(3)
        else -> GridCells.Adaptive(minSize = 150.dp)
    }

    Scaffold(
        containerColor = Negro,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PRÁCTICA 09",
                            fontWeight = FontWeight.ExtraBold,
                            color = Naranja
                        )

                        Text(
                            text = "Galería de imágenes",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Negro
                )
            )
        }
    ) { padding ->

        LazyVerticalGrid(
            columns = columnas,
            state = estadoGrid,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 24.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item(
                key = "encabezado",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Encuentra otra perspectiva.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Explora la colección y toca una fotografía " +
                                "para verla en tamaño ampliado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = GrisTarjeta
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "COLECCIÓN EN LÍNEA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Naranja
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "${fotos.size} fotografías",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "Coil + Compose",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = cantidadColumnas == 0,
                            onClick = { cantidadColumnas = 0 },
                            label = { Text("Adaptativa") }
                        )

                        FilterChip(
                            selected = cantidadColumnas == 2,
                            onClick = { cantidadColumnas = 2 },
                            label = { Text("2 columnas") }
                        )

                        FilterChip(
                            selected = cantidadColumnas == 3,
                            onClick = { cantidadColumnas = 3 },
                            label = { Text("3 columnas") }
                        )
                    }
                }
            }

            items(
                items = fotos,
                key = { it.id }
            ) { foto ->
                TarjetaFoto(
                    foto = foto,
                    onSeleccionar = {
                        idSeleccionado = foto.id
                    }
                )
            }
        }
    }

    val fotoSeleccionada = fotos.firstOrNull {
        it.id == idSeleccionado
    }

    fotoSeleccionada?.let { foto ->
        DetalleFoto(
            foto = foto,
            onCerrar = {
                idSeleccionado = null
            }
        )
    }
}

@Composable
private fun TarjetaFoto(
    foto: Foto,
    onSeleccionar: () -> Unit
) {
    Card(
        onClick = onSeleccionar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = GrisTarjeta
        )
    ) {
        ImagenRemota(
            url = foto.miniatura,
            descripcion = foto.titulo,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
        )

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = foto.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Ver imagen",
                style = MaterialTheme.typography.labelSmall,
                color = Naranja
            )
        }
    }
}

@Composable
private fun ImagenRemota(
    url: String,
    descripcion: String,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var cargando by remember(url) {
        mutableStateOf(true)
    }

    var fallo by remember(url) {
        mutableStateOf(false)
    }

    var intento by remember(url) {
        mutableIntStateOf(0)
    }

    val solicitud = remember(context, url, intento) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build()
    }

    Box(
        modifier = modifier.background(
            MaterialTheme.colorScheme.surfaceVariant
        ),
        contentAlignment = Alignment.Center
    ) {
        key(url, intento) {
            AsyncImage(
                model = solicitud,
                contentDescription = descripcion,
                placeholder = ColorPainter(
                    Color(0xFF303039)
                ),
                error = ColorPainter(
                    Color(0xFF3C2525)
                ),
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                onLoading = {
                    cargando = true
                    fallo = false
                },
                onSuccess = {
                    cargando = false
                    fallo = false
                },
                onError = {
                    cargando = false
                    fallo = true
                }
            )
        }

        if (cargando && !fallo) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Naranja,
                strokeWidth = 3.dp
            )
        }

        if (fallo) {
            Column(
                modifier = Modifier.padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error al cargar",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )

                TextButton(
                    onClick = {
                        cargando = true
                        fallo = false
                        intento++
                    }
                ) {
                    Text(
                        text = "Reintentar",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun DetalleFoto(
    foto: Foto,
    onCerrar: () -> Unit
) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = Negro
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 18.dp,
                            end = 8.dp,
                            top = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = foto.titulo,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Naranja
                    )

                    TextButton(onClick = onCerrar) {
                        Text("Cerrar")
                    }
                }

                ImagenRemota(
                    url = foto.ampliada,
                    descripcion = "Vista ampliada de ${foto.titulo}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Vista ampliada",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Fotografía proporcionada por Lorem Picsum.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "La imagen conserva su proporción original.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}