package com.example.miniproyecto01.screens
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miniproyecto01.data.PreferencesManager
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
@Composable
fun RegistroScreen(
    onConfirmar: (
        matricula: String,
        nombre: String,
        carrera: String,
        turno: String,
        activo: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember {
        PreferencesManager(context)
    }

    var matricula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("Selecciona una carrera") }
    var menuExpandido by remember { mutableStateOf(false) }
    var turno by remember { mutableStateOf("Matutino") }
    var activo by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    val carreras = listOf(
        "Ingeniería de Software",
        "Ingeniería en Computación",
        "Tecnologías de la Información",
        "Otra"

    )
    val azulRey = Color(0xFF0D47A1)
    val azulClaro = Color(0xFFE3F2FD)
    val formaCuadrada = RoundedCornerShape(6.dp)

    LaunchedEffect(Unit) {
        matricula = preferencesManager.obtenerMatricula()
        activo = preferencesManager.obtenerEstatusActivo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sistema de registro de estudiantes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = azulRey
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = matricula,
            onValueChange = {
                matricula = it
                mensajeError = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Matrícula") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulRey,
                focusedLabelColor = azulRey,
                cursorColor = azulRey
            ),
            shape = formaCuadrada,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mensajeError = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre completo") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulRey,
                focusedLabelColor = azulRey,
                cursorColor = azulRey
            ),
            shape = formaCuadrada,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                menuExpandido = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = formaCuadrada,
            colors = ButtonDefaults.buttonColors(
                containerColor = azulRey,
                contentColor = Color.White
            )
        ) {
            Text("Carrera: $carrera")
        }

        DropdownMenu(
            expanded = menuExpandido,
            onDismissRequest = {
                menuExpandido = false
            }
        ) {
            carreras.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(opcion)
                    },
                    onClick = {
                        carrera = opcion
                        menuExpandido = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Turno",
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = turno == "Matutino",
                onClick = {
                    turno = "Matutino"
                }
            )

            Text(
                text = "Matutino",
                modifier = Modifier
                    .selectable(
                        selected = turno == "Matutino",
                        onClick = {
                            turno = "Matutino"
                        },
                        role = Role.RadioButton
                    )
                    .padding(end = 16.dp)
            )

            RadioButton(
                selected = turno == "Vespertino",
                onClick = {
                    turno = "Vespertino"
                }
            )

            Text(
                text = "Vespertino",
                modifier = Modifier
                    .selectable(
                        selected = turno == "Vespertino",
                        onClick = {
                            turno = "Vespertino"
                        },
                        role = Role.RadioButton
                    )
                    .padding(end = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (activo) "Estatus: Activo" else "Estatus: Inactivo",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = activo,
                onCheckedChange = {
                    activo = it
                }
            )
        }

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (
                    matricula.isBlank() ||
                    nombre.isBlank() ||
                    carrera == "Selecciona una carrera"
                ) {
                    mensajeError =
                        "Completa matrícula, nombre y carrera antes de continuar."
                    return@Button
                }

                preferencesManager.guardarMatricula(matricula)
                preferencesManager.guardarEstatusActivo(activo)

                onConfirmar(
                    matricula,
                    nombre,
                    carrera,
                    turno,
                    activo
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = formaCuadrada,
            colors = ButtonDefaults.buttonColors(
                containerColor = azulRey,
                contentColor = Color.White
            )
        ) {
            Text("Continuar a confirmación")
        }
    }
}