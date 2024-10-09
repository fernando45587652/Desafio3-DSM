package com.udb.recursodeaprendizaje

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udb.recursosdeaprendizaje.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActualizarRecursoActivity : AppCompatActivity() {
    private lateinit var api: RecursoApi
    private var recursoId: Int = -1

    private lateinit var editTextTitulo: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextEnlace: EditText
    private lateinit var editTextImagen: EditText
    private lateinit var editTextTipo: EditText
    private lateinit var btnActualizar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_recurso)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://636adf4eb10125b78fe68af7.mockapi.io/api/example/Recursos")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(RecursoApi::class.java)

        recursoId = intent.getIntExtra("recurso_id", -1)
        Log.d("ActualizarRecursoActivity", "ID del recurso recibido: $recursoId")

        editTextTitulo = findViewById(R.id.editTextTitulo)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextEnlace = findViewById(R.id.editTextEnlace)
        editTextImagen = findViewById(R.id.editTextImagen)
        editTextTipo = findViewById(R.id.editTextTipo)
        btnActualizar = findViewById(R.id.btnActualizar)

        if (recursoId != -1) {
            cargarRecurso(recursoId)
        } else {
            Log.e("ActualizarRecursoActivity", "El ID del recurso es inválido")
            Toast.makeText(this, "ID de recurso no válido", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnActualizar.setOnClickListener {
            Log.d(
                "ActualizarRecursoActivity",
                "ID del recurso antes de la actualización: $recursoId"
            )
            actualizarRecurso()
        }
    }

    private fun cargarRecurso(id: Int) {
        val llamada = api.obtenerRecurso(id.toString())
        llamada.enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    val recurso = response.body()
                    if (recurso != null) {
                        Log.d("ActualizarRecursoActivity", "Recurso cargado: $recurso")
                        editTextTitulo.setText(recurso.titulo)
                        editTextDescripcion.setText(recurso.descripcion)
                        editTextEnlace.setText(recurso.enlace)
                        editTextImagen.setText(recurso.imagen)
                        editTextTipo.setText(recurso.tipo)
                    } else {
                        Log.e("ActualizarRecursoActivity", "El recurso es nulo")
                    }
                } else {
                    Log.e(
                        "ActualizarRecursoActivity",
                        "Error al cargar recurso: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                Log.e("ActualizarRecursoActivity", "Error de conexión: ${t.message}")
            }
        })
    }

    private fun actualizarRecurso() {
        val recursoActualizado = Recurso(
            id = recursoId,
            titulo = editTextTitulo.text.toString(),
            descripcion = editTextDescripcion.text.toString(),
            enlace = editTextEnlace.text.toString(),
            imagen = editTextImagen.text.toString(),
            tipo = editTextTipo.text.toString()
        )

        val llamada = api.actualizarRecurso(recursoId, recursoActualizado)
        llamada.enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ActualizarRecursoActivity,
                        "Recurso actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Log.e(
                        "ActualizarRecursoActivity",
                        "Error al actualizar recurso: ${response.errorBody()?.string()}"
                    )
                    Toast.makeText(
                        this@ActualizarRecursoActivity,
                        "Error al actualizar recurso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                Toast.makeText(
                    this@ActualizarRecursoActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}