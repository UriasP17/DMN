package com.example.practica06

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practica06.databinding.ItemAlumnoBinding

class AlumnoAdapter(
    private val listaAlumnos: List<Alumno>,
    private val onClickAlumno: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    inner class AlumnoViewHolder(
        private val binding: ItemAlumnoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alumno: Alumno) {
            val inicial = alumno.nombre.firstOrNull()?.uppercase() ?: "?"

            binding.tvInicial.text = inicial
            binding.tvNombre.text = alumno.nombre
            binding.tvCarrera.text = alumno.carrera
            binding.tvMatricula.text = "Mat. ${alumno.matricula}"
            binding.tvTurno.text = alumno.turno

            binding.root.setOnClickListener {
                onClickAlumno(alumno)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemAlumnoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        holder.bind(listaAlumnos[position])
    }

    override fun getItemCount(): Int = listaAlumnos.size
}