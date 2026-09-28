package com.example.miniproyecto01

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miniproyecto01.screens.ConfirmacionScreen
import com.example.miniproyecto01.screens.RegistroScreen
import android.net.Uri

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "registro"
    ) {
        composable("registro") {
            RegistroScreen { matricula, nombre, carrera, turno, activo ->
                val nombreCodificado = Uri.encode(nombre)
                val carreraCodificada = Uri.encode(carrera)

                navController.navigate(
                    "confirmacion/$matricula/$nombreCodificado/" +
                            "$carreraCodificada/$turno/$activo"
                )
            }
        }

        composable(
            route = "confirmacion/{matricula}/{nombre}/{carrera}/{turno}/{activo}",
            arguments = listOf(
                navArgument("matricula") {
                    type = NavType.StringType
                },
                navArgument("nombre") {
                    type = NavType.StringType
                },
                navArgument("carrera") {
                    type = NavType.StringType
                },
                navArgument("turno") {
                    type = NavType.StringType
                },
                navArgument("activo") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val carrera = backStackEntry.arguments?.getString("carrera") ?: ""
            val turno = backStackEntry.arguments?.getString("turno") ?: ""
            val activo = backStackEntry.arguments?.getBoolean("activo") ?: false

            ConfirmacionScreen(
                matricula = matricula,
                nombre = nombre,
                carrera = carrera,
                turno = turno,
                activo = activo,
                onRegresar = {
                    navController.popBackStack()
                }
            )
        }
    }
}