package com.udb.recursodeaprendizaje

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.udb.recursosdeaprendizaje.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecursoAdapter
    private lateinit var api: RecursoApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabAgregar: FloatingActionButton = findViewById(R.id.fab_agregar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://636adf4eb10125b78fe68af7.mockapi.io/api/example/Recursos")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(RecursoApi::class.java)

        cargarDatos()

        fabAgregar.setOnClickListener {
            val intent = Intent(this, CrearRecursoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        val call = api.obtenerRecursos()
        call.enqueue(object : Callback<List<Recurso>> {
            override fun onResponse(call: Call<List<Recurso>>, response: Response<List<Recurso>>) {
                if (response.isSuccessful) {
                    val recursos = response.body()
                    if (recursos != null) {
                        adapter = RecursoAdapter(recursos)
                        recyclerView.adapter = adapter

                        adapter.setOnItemClickListener(object : RecursoAdapter.OnItemClickListener {
                            override fun onItemClick(recurso: Recurso) {
                                val opciones = arrayOf("Modificar Recurso", "Eliminar Recurso")

                                AlertDialog.Builder(this@MainActivity)
                                    .setTitle(recurso.titulo)
                                    .setItems(opciones) { _, index ->
                                        when (index) {
                                            0 -> modificarRecurso(recurso)
                                            1 -> eliminarRecurso(recurso)
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        })
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al obtener recursos: $error")
                    Toast.makeText(
                        this@MainActivity,
                        "Error al obtener recursos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Recurso>>, t: Throwable) {
                Log.e("API", "Error al obtener recursos: ${t.message}")
                Toast.makeText(this@MainActivity, "Error al obtener recursos", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun modificarRecurso(recurso: Recurso) {
        val intent = Intent(this, ActualizarRecursoActivity::class.java).apply {
            putExtra("recurso_id", recurso.id)
            putExtra("titulo", recurso.titulo)
            putExtra("descripcion", recurso.descripcion)
            putExtra("tipo", recurso.tipo)
            putExtra("enlace", recurso.enlace)
            putExtra("imagen", recurso.imagen)
        }
        startActivity(intent)
    }

    private fun eliminarRecurso(recurso: Recurso) {
        val llamada = api.eliminarRecurso(recurso.id)
        llamada.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Recurso eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos()
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al eliminar recurso: $error")
                    Toast.makeText(
                        this@MainActivity,
                        "Error al eliminar recurso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar recurso: ${t.message}")
                Toast.makeText(this@MainActivity, "Error al eliminar recurso", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
