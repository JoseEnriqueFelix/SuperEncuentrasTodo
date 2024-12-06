package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TablaConsultarPaquetes : AppCompatActivity() {
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase
    private lateinit var tabla: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tabla_consultar_paquetes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return
        tabla = findViewById(R.id.TableLayoutConsultarPaquetes)
        procesar()
    }

    private fun procesar() {
        var cursor: Cursor =
            baseDeDatos.rawQuery("SELECT * FROM Paquetes ORDER BY PaqueteID ASC", null)
        if (cursor.count <= 0) {
            Rutinas.mensajeToast("Todavia no hay registros", this)
            return
        }
        hazEncabezados()
        while (cursor.moveToNext()) {
            val fila = TableRow(this)
            val paqueteID = cursor.getInt(0)
            val productoID = cursor.getInt(1)
            val noUnidades = cursor.getInt(2)
            val descuento = cursor.getInt(3)
            val atributos = listOf(
                paqueteID.toString(),
                productoID.toString(),
                noUnidades.toString(),
                descuento.toString()
            )

            for (atributo in atributos) {
                val textView = TextView(this)
                textView.text = atributo
                textView.gravity = android.view.Gravity.CENTER
                textView.setPadding(8, 8, 8, 8)
                val params = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
                textView.layoutParams = params
                fila.addView(textView)
            }
            tabla.addView(fila)
        }
        cursor.close()
    }

    private fun hazEncabezados() {
        val encabezados = listOf("PaqueteID", "ProductoID", "No. Unidades", "Porcentaje Descuento")
        val filaEncabezado = TableRow(this)
        for (encabezado in encabezados) {
            val textView = TextView(this)
            textView.text = encabezado
            textView.gravity = android.view.Gravity.CENTER
            textView.setPadding(8, 8, 8, 8)
            val params = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            textView.layoutParams = params
            filaEncabezado.addView(textView)

        }
        tabla.addView(filaEncabezado)
    }

}