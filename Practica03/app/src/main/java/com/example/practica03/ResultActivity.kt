package com.example.practica03

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practica03.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombre = intent.getStringExtra("EXTRA_NOMBRE") ?: "Sin nombre"
        val correo = intent.getStringExtra("EXTRA_CORREO") ?: "Sin correo"

        binding.tvNombreRecibido.text = "Nombre: $nombre"
        binding.tvCorreoRecibido.text = "Correo: $correo"

        binding.btnRegresar.setOnClickListener {
            finish()
        }
    }
}