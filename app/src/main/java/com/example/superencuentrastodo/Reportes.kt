package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Reportes : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnPaqueteMas3ProductosMas10Unidades: Button
    private lateinit var btnProductoNoPaqueteNoVenta: Button
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase
    private lateinit var layoutResultados: LinearLayout

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
        layoutResultados = findViewById(R.id.layoutResultados)

        escuchadores()
    }

    private fun escuchadores() {
        btnProductoNoPaqueteNoVenta.setOnClickListener(this)
        btnPaqueteMas3ProductosMas10Unidades.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        layoutResultados.removeAllViews()

        when (v) {
            btnProductoNoPaqueteNoVenta -> {
                val intent = Intent(this, TablaConsultar::class.java)
                intent.putExtra(
                    "consulta",
                    """
                    SELECT ProductoID, ProductoNombre
                    FROM Productos
                    WHERE ProductoID NOT IN (
                        SELECT ProductoID 
                        FROM Paquetes
                    )
                    AND ProductoID NOT IN (
                        SELECT ID 
                        FROM Ventas 
                        WHERE ProdOPaq = 0
                    )
                    AND ProductoEstatus = 'A'
                    """.trimIndent()
                )
                val encabezados = listOf("ProductoID", "Paquetes")
                intent.putExtra("encabezados", ArrayList(encabezados))
                startActivity(intent)
            }

            btnPaqueteMas3ProductosMas10Unidades -> {
                val intent = Intent(this, TablaConsultar::class.java)
                intent.putExtra(
                    "consulta",
                    """
                    SELECT PaqueteID, COUNT(ProductoID), SUM(PaqueteNoUnidades)
                    FROM Paquetes
                    GROUP BY PaqueteID
                    HAVING COUNT(ProductoID) > 3 
                    AND SUM(PaqueteNoUnidades) > 10
                    """.trimIndent()
                )
                val encabezados = listOf("PaqueteID", "Num. Productos", "Total Unidades")
                intent.putExtra("encabezados", ArrayList(encabezados))
                startActivity(intent)
            }
        }
    }
}