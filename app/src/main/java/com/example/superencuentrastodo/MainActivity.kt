package com.example.superencuentrastodo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnCrudProductos: Button
    private lateinit var btnCrudPaquetes: Button
    private lateinit var btnProcesarVentas: Button
    private lateinit var btnEmisionReportes : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        btnCrudProductos = findViewById(R.id.btnCrudProductos)
        btnCrudPaquetes = findViewById(R.id.btnCrudPaquetes)
        btnProcesarVentas = findViewById(R.id.btnProcesarVentas)
        btnEmisionReportes = findViewById(R.id.btnEmisionReportes)

        btnCrudProductos.setOnClickListener(this)
        btnCrudPaquetes.setOnClickListener(this)
        btnProcesarVentas.setOnClickListener(this)
        btnEmisionReportes.setOnClickListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onClick(v: View?) {
        println("En el onClick xD")
        when(v){
            btnCrudProductos -> {
                val intent = Intent(this, CrudProductos::class.java)
                startActivity(intent)
            }
        }
    }
}