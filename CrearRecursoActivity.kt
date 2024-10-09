package com.udb.recursodeaprendizaje

import android.content.Intent
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

class CrearRecursoActivity : AppCompatActivity() {

    private lateinit var tituloEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var enlaceEditText: EditText
    private lateinit var imagenEditText: EditText
    private lateinit var crearButton: Button

    var auth_username = ""
    var auth_password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_recurso)

        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            auth_username = datos.getString("auth_username", "")
            auth_password = datos.getString("auth_password", "")
        }

        tituloEditText = findViewById(R.id.editTextTitulo)
        descripcionEditText = findViewById(R.id.editTextDescripcion)
        tipoEditText = findViewById(R.id.editTextTipo)
        enlaceEditText = findViewById(R.id.editTextEnlace)
        imagenEditText = findViewById(R.id.editTextImagen)
        crearButton = findViewById(R.id.btnGuardar)

        crearButton.setOnClickListener {
            val titulo = tituloEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val tipo = tipoEditText.text.toString()
            val enlace = enlaceEditText.text.toString()
            val imagen = imagenEditText.text.toString()

            if (titulo.isEmpty() || descripcion.isEmpty() || tipo.isEmpty() || enlace.isEmpty() || imagen.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recurso = Recurso(0, titulo, descripcion, tipo, enlace, imagen)
            Log.e("API", "auth_username: $auth_username")
            Log.e("API", "auth_password: $auth_password")

            val retrofit = Retrofit.Builder()
                .baseUrl("https://636adf4eb10125b78fe68af7.mockapi.io/api/example/Recursos")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(RecursoApi::class.java)

            api.crearRecurso(recurso).enqueue(object : Callback<Recurso> {
                override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CrearRecursoActivity, "Recurso creado exitosamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CrearRecursoActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error al crear recurso: $error")
                        Toast.makeText(this@CrearRecursoActivity, "Error al crear el recurso: $error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Recurso>, t: Throwable) {
                    Toast.makeText(this@CrearRecursoActivity, "Error de conexión al crear el recurso", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
