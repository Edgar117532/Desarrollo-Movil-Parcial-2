
package com.example.notasapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val mensaje = findViewById<TextView>(R.id.tvMensaje)

        btnLogin.setOnClickListener {
            val correo = email.text.toString().trim()
            val clave = password.text.toString()

            if (correo.isEmpty() || clave.isEmpty()) {
                mensaje.text = "Ingresa tu correo y contraseña"
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            mensaje.text = "Conectando con el servidor..."

            RetrofitClient.api.iniciarSesion(
                LoginRequest(correo, clave)
            ).enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    btnLogin.isEnabled = true

                    if (response.isSuccessful) {
                        val resultado = response.body()

                        if (resultado != null) {
                            mensaje.text =
                                "¡Bienvenido, ${resultado.user.nombre}!"
                            val intent = android.content.Intent(
                                this@MainActivity,
                                NotasActivity::class.java
                            )

                            intent.putExtra("token", resultado.token)
                            startActivity(intent)

                            Toast.makeText(
                                this@MainActivity,
                                "Inicio de sesión correcto",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mensaje.text = "El servidor no devolvió datos"
                        }
                    } else {
                        mensaje.text = when (response.code()) {
                            401, 400 -> "Correo o contraseña incorrectos"
                            else -> "Error del servidor: ${response.code()}"
                        }
                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>,
                    t: Throwable
                ) {
                    btnLogin.isEnabled = true
                    mensaje.text = "No se pudo conectar: ${t.message}"
                }
            })
        }
    }
}


