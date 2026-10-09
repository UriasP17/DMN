package com.example.practica06

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica06.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var alumnoAdapter: AlumnoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
    }

    private fun configurarRecyclerView() {
        val alumnos = listOf(
            Alumno(
                "16000668",
                "Urias Valenzuela Paul",
                "Ingeniería de Software",
                "Vespertino"
            ),
            Alumno(
                "16000669",
                "Ana López García",
                "Ingeniería en Computación",
                "Matutino"
            ),
            Alumno(
                "16000670",
                "Carlos Hernández Ruiz",
                "Tecnologías de la Información",
                "Vespertino"
            ),
            Alumno(
                "16000671",
                "Mariana Torres Vega",
                "Ingeniería de Software",
                "Matutino"
            ),
            Alumno(
                "16000672",
                "Diego Martínez Castro",
                "Ingeniería en Computación",
                "Vespertino"
            ),
            Alumno(
                "16000673",
                "Fernanda Sánchez Díaz",
                "Tecnologías de la Información",
                "Matutino"
            ),
            Alumno(
                "16000674",
                "Jorge Ramírez Soto",
                "Ingeniería de Software",
                "Vespertino"
            ),
            Alumno(
                "16000675",
                "Valeria Morales Cruz",
                "Ingeniería en Computación",
                "Matutino"
            )
        )

        alumnoAdapter = AlumnoAdapter(alumnos) { alumno ->
            Toast.makeText(
                this,
                "Seleccionaste a ${alumno.nombre}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvAlumnos.layoutManager = LinearLayoutManager(this)
        binding.rvAlumnos.adapter = alumnoAdapter
        binding.rvAlumnos.setHasFixedSize(true)
    }
}