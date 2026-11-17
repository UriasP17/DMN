
package com.example.sem10_miniproyecto03

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val Naranja = Color(0xFFFF8C00)
private val NaranjaClaro = Color(0xFFFFB74D)
private val Fondo = Color(0xFF101014)
private val Tarjeta = Color(0xFF1D1D24)
private val Blanco = Color(0xFFF5F5F5)
private val Gris = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val modelo: PublicacionesViewModel = viewModel()

            val publicaciones by modelo.publicaciones.collectAsState()

            val colores = darkColorScheme(
                primary = Naranja,
                secondary = NaranjaClaro,
                background = Fondo,
                surface = Tarjeta,
                onPrimary = Color.Black,
                onBackground = Blanco,
                onSurface = Blanco
            )

            MaterialTheme(colorScheme = colores) {
                PantallaComunidad(
                    publicaciones = publicaciones,
                    guardar = { titulo, descripcion, foto ->
                        modelo.guardarPublicacion(
                            titulo,
                            descripcion,
                            foto
                        )
                    }
                )
            }
        }
    }
}

// Copia imágenes elegidas de galería a archivos privados.
private fun copiarImagen(context: Context, origen: Uri): String {
    val carpeta = File(context.filesDir, "images")
    carpeta.mkdirs()

    val archivo = File(
        carpeta,
        "${UUID.randomUUID()}.jpg"
    )

    context.contentResolver.openInputStream(origen).use { entrada ->
        requireNotNull(entrada) {
            "No se pudo leer la imagen seleccionada"
        }

        archivo.outputStream().use { salida ->
            entrada.copyTo(salida)
        }
    }

    return Uri.fromFile(archivo).toString()
}

// Prepara el archivo donde la cámara guardará la foto.
private fun nuevaFoto(context: Context): Uri {
    val carpeta = File(context.filesDir, "images")
    carpeta.mkdirs()

    val archivo = File(
        carpeta,
        "${UUID.randomUUID()}.jpg"
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}

private fun fechaTexto(fecha: Long): String {
    val formato = SimpleDateFormat(
        "dd/MM/yyyy - hh:mm a",
        Locale("es", "MX")
    )

    return formato.format(Date(fecha))
}

@Composable
fun PantallaComunidad(
    publicaciones: List<Publicacion>,
    guardar: (String, String, String) -> Unit
) {
    val context = LocalContext.current

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf("") }
    var fotoPendiente by rememberSaveable { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("Comparte algo con tu comunidad") }

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            fotoUri = fotoPendiente
            mensaje = "Fotografía lista para publicar"
        } else {
            mensaje = "Captura cancelada"
        }
        fotoPendiente = ""
    }

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                fotoUri = copiarImagen(context, uri)
                mensaje = "Imagen seleccionada correctamente"
            } catch (e: Exception) {
                mensaje = "No se pudo cargar la imagen"
            }
        }
    }

    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Naranja,
        unfocusedBorderColor = Gris,
        focusedLabelColor = Naranja,
        unfocusedLabelColor = Gris,
        cursorColor = Naranja,
        focusedTextColor = Blanco,
        unfocusedTextColor = Blanco
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            top = 30.dp,
            bottom = 30.dp
        )
    ) {

        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "COMUNIDAD UAS",
                    color = Naranja,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "RED SOCIAL LOCAL",
                    color = Blanco,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Comparte momentos de tu vida universitaria",
                    color = Gris,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Tarjeta
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "NUEVA PUBLICACIÓN",
                        color = Naranja,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    HorizontalDivider(
                        color = Naranja.copy(alpha = 0.35f)
                    )

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        placeholder = { Text("Escribe un título...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresCampo,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization =
                                KeyboardCapitalization.Sentences
                        )
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        placeholder = {
                            Text("¿Qué quieres compartir?")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 125.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresCampo,
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(
                            capitalization =
                                KeyboardCapitalization.Sentences
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        OutlinedButton(
                            onClick = {
                                try {
                                    val uri = nuevaFoto(context)
                                    fotoPendiente = uri.toString()
                                    camara.launch(uri)
                                } catch (e: Exception) {
                                    mensaje = "No se pudo abrir la cámara"
                                }
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Naranja),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Naranja
                            )
                        ) {
                            Text("CÁMARA")
                        }

                        OutlinedButton(
                            onClick = {
                                galeria.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts
                                            .PickVisualMedia
                                            .ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Naranja),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Naranja
                            )
                        ) {
                            Text("GALERÍA")
                        }
                    }

                    if (fotoUri.isNotEmpty()) {
                        AsyncImage(
                            model = Uri.parse(fotoUri),
                            contentDescription =
                                "Fotografía seleccionada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = "Fecha: ${
                            fechaTexto(System.currentTimeMillis())
                        }",
                        color = Gris,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Button(
                        onClick = {
                            when {
                                titulo.isBlank() -> {
                                    mensaje = "Escribe un título"
                                }

                                descripcion.isBlank() -> {
                                    mensaje = "Escribe una descripción"
                                }

                                fotoUri.isBlank() -> {
                                    mensaje = "Agrega una fotografía"
                                }

                                else -> {
                                    guardar(
                                        titulo.trim(),
                                        descripcion.trim(),
                                        fotoUri
                                    )

                                    titulo = ""
                                    descripcion = ""
                                    fotoUri = ""

                                    mensaje = "Publicación enviada para guardar"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Naranja,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "PUBLICAR",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = mensaje,
                        color = NaranjaClaro,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Text(
                text = "PUBLICACIONES RECIENTES",
                color = Naranja,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (publicaciones.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Tarjeta
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Todavía no hay publicaciones.\n¡Comparte la primera!",
                        color = Gris,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp)
                    )
                }
            }
        } else {
            items(
                items = publicaciones,
                key = { it.id }
            ) { publicacion ->
                TarjetaPublicacion(publicacion)
            }
        }
    }
}

@Composable
fun TarjetaPublicacion(publicacion: Publicacion) {

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Tarjeta
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Naranja
                ) {
                    Text(
                        text = "UAS",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Comunidad universitaria",
                        color = Blanco,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = fechaTexto(publicacion.fecha),
                        color = Gris,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            AsyncImage(
                model = Uri.parse(publicacion.fotoUri),
                contentDescription = publicacion.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 18.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = publicacion.titulo,
                    color = Blanco,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = publicacion.descripcion,
                    color = Gris,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(
                    color = Naranja.copy(alpha = 0.25f)
                )

                Text(
                    text = "Publicación #${publicacion.id}",
                    color = NaranjaClaro,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
