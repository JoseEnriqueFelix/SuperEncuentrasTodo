package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Reportes : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnPaqueteMas3ProductosMas10Unidades: Button
    private lateinit var btnProductoNoPaqueteNoVenta: Button
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reportes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return

        btnProductoNoPaqueteNoVenta = findViewById(R.id.btnProductoNoPaqueteNoVenta)
        btnPaqueteMas3ProductosMas10Unidades =
            findViewById(R.id.btnPaqueteMas3ProductosMas10Unidades)

        escuchadores()
    }

    private fun escuchadores() {

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}