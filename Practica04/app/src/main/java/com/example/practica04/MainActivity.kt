package com.example.practica04

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica04.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var carreraSeleccionada = ""
    private var modalidadSeleccionada = "Presencial"
    private var nivelExperiencia = 1
    private var notificacionesActivas = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinner()
        configurarRadioGroup()
        configurarCheckBoxes()
        configurarSwitch()
        configurarSeekBar()
        configurarBoton()
    }

    private fun configurarSpinner() {
        val carreras = arrayOf(
            "Selecciona una carrera",
            "Ingeniería de Software",
            "Ingeniería en Computación",
            "Tecnologías de la Información",
            "Otra"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            carreras
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerCarrera.adapter = adapter

        binding.spinnerCarrera.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    carreraSeleccionada = carreras[position]

                    if (position > 0) {
                        Toast.makeText(
                            this@MainActivity,
                            "Carrera seleccionada: $carreraSeleccionada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    carreraSeleccionada = ""
                }
            }
    }

    private fun configurarRadioGroup() {
        binding.radioGroupModalidad.setOnCheckedChangeListener { _, checkedId ->
            modalidadSeleccionada = when (checkedId) {
                R.id.rbPresencial -> "Presencial"
                R.id.rbVirtual -> "Virtual"
                R.id.rbHibrida -> "Híbrida"
                else -> "No seleccionada"
            }
        }
    }

    private fun configurarCheckBoxes() {
        binding.cbAndroid.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Interés: Android", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cbWeb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Interés: Desarrollo Web", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cbRedes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Interés: Redes y servidores", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarSwitch() {
        binding.switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            notificacionesActivas = isChecked

            val mensaje = if (isChecked) {
                "Notificaciones activadas"
            } else {
                "Notificaciones desactivadas"
            }

            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarSeekBar() {
        binding.seekBarNivel.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    nivelExperiencia = progress + 1

                    binding.tvNivel.text =
                        "Nivel de experiencia: $nivelExperiencia"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Nivel seleccionado: $nivelExperiencia",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun configurarBoton() {
        binding.btnProcesar.setOnClickListener {
            if (carreraSeleccionada.isEmpty() ||
                carreraSeleccionada == "Selecciona una carrera"
            ) {
                Toast.makeText(
                    this,
                    "Selecciona una carrera antes de procesar",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intereses = obtenerIntereses()

            val resumen = """
                RESUMEN DEL FORMULARIO
                
                Carrera: $carreraSeleccionada
                Modalidad: $modalidadSeleccionada
                Intereses: $intereses
                Notificaciones: ${if (notificacionesActivas) "Activadas" else "Desactivadas"}
                Nivel de experiencia: $nivelExperiencia / 10
            """.trimIndent()

            binding.tvResumen.text = resumen
        }
    }

    private fun obtenerIntereses(): String {
        val intereses = mutableListOf<String>()

        if (binding.cbAndroid.isChecked) {
            intereses.add("Desarrollo Android")
        }

        if (binding.cbWeb.isChecked) {
            intereses.add("Desarrollo Web")
        }

        if (binding.cbRedes.isChecked) {
            intereses.add("Redes y servidores")
        }

        return if (intereses.isEmpty()) {
            "Ninguno seleccionado"
        } else {
            intereses.joinToString(", ")
        }
    }
}