package com.example.practica03

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica03.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnviar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val correo = binding.etCorreo.text.toString().trim()

            if (nombre.isEmpty() || correo.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("EXTRA_NOMBRE", nombre)
                putExtra("EXTRA_CORREO", correo)
            }

            startActivity(intent)
        }
    }
}