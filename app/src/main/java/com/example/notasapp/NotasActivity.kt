
package com.example.notasapp

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotasActivity : ComponentActivity() {

    private lateinit var token: String
    private lateinit var titulo: EditText
    private lateinit var contenido: EditText
    private lateinit var estado: TextView
    private lateinit var contenedor: LinearLayout
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        token = "Bearer ${intent.getStringExtra("token").orEmpty()}"

        titulo = findViewById(R.id.etTitulo)
        contenido = findViewById(R.id.etContenido)
        estado = findViewById(R.id.tvEstadoNotas)
        contenedor = findViewById(R.id.contenedorNotas)
        btnGuardar = findViewById(R.id.btnGuardarNota)

        findViewById<Button>(R.id.btnActualizarNotas)
            .setOnClickListener { cargarNotas() }

        btnGuardar.setOnClickListener { guardarNota() }

        cargarNotas()
    }

    private fun cargarNotas() {
        estado.text = "Cargando notas..."

        RetrofitClient.api.obtenerNotas(token)
            .enqueue(object : Callback<List<Nota>> {

                override fun onResponse(
                    call: Call<List<Nota>>,
                    response: Response<List<Nota>>
                ) {
                    if (response.isSuccessful) {
                        contenedor.removeAllViews()
                        val notas = response.body().orEmpty()

                        if (notas.isEmpty()) {
                            estado.text = "Todavía no tienes notas."
                        } else {
                            estado.text = "Mis notas (${notas.size})"

                            notas.forEach { nota ->
                                val texto = TextView(this@NotasActivity)
                                texto.text =
                                    "${nota.titulo}\n${nota.contenido}\n"
                                texto.textSize = 18f
                                texto.setPadding(0, 12, 0, 12)

                                contenedor.addView(
                                    texto,
                                    ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                )
                            }
                        }
                    } else {
                        estado.text =
                            "No se pudieron cargar las notas: ${response.code()}"
                    }
                }

                override fun onFailure(
                    call: Call<List<Nota>>,
                    t: Throwable
                ) {
                    estado.text = "Error de conexión: ${t.message}"
                }
            })
    }

    private fun guardarNota() {
        val tituloTexto = titulo.text.toString().trim()
        val contenidoTexto = contenido.text.toString().trim()

        if (tituloTexto.isEmpty() || contenidoTexto.isEmpty()) {
            estado.text = "Escribe el título y el contenido."
            return
        }

        btnGuardar.isEnabled = false
        estado.text = "Guardando nota..."

        RetrofitClient.api.crearNota(
            token,
            CrearNotaRequest(tituloTexto, contenidoTexto)
        ).enqueue(object : Callback<Nota> {

            override fun onResponse(
                call: Call<Nota>,
                response: Response<Nota>
            ) {
                btnGuardar.isEnabled = true

                if (response.isSuccessful) {
                    titulo.text.clear()
                    contenido.text.clear()
                    estado.text = "Nota guardada correctamente."
                    cargarNotas()
                } else {
                    estado.text =
                        "No se pudo guardar la nota: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Nota>, t: Throwable) {
                btnGuardar.isEnabled = true
                estado.text = "Error de conexión: ${t.message}"
            }
        })
    }
}


