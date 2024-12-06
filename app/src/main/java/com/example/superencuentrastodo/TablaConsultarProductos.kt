package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class TablaConsultarProductos : AppCompatActivity() {
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase
    private lateinit var tabla: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tabla_consultar_productos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return
        tabla = findViewById(R.id.TableLayoutConsultarProductos)
        procesar()
    }

    private fun procesar(consulta: String = "SELECT ProductoID, ProductoNombre, ProductoPrecioUnidad, ProductoNoUnidades FROM Productos WHERE ProductoEstatus = 'A' ORDER BY ProductoID ASC") {
        var cursor: Cursor = baseDeDatos.rawQuery(
            consulta,
            null
        )
        if (cursor.count <= 0) {
            Rutinas.mensajeToast("Todavia no hay registros", this)
            return
        }
        hazEncabezados()
        while (cursor.moveToNext()) {
            val fila = TableRow(this)
            val productoID = cursor.getInt(0)
            val nombre = cursor.getString(1)
            val precio = cursor.getDouble(2)
            val noUnidades = cursor.getInt(3)
            val atributos = listOf(
                productoID.toString(),
                nombre,
                precio.toString(),
                noUnidades.toString()
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
        val encabezados = listOf("ProductoID", "Nombre", "Precio Unidad", "No. Unidades")
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