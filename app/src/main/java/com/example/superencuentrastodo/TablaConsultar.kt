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

class TablaConsultar : AppCompatActivity() {
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase
    private lateinit var tabla: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tabla_consultar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val consulta = intent.getStringExtra("consulta")
        val encabezados: ArrayList<String>? = intent.getStringArrayListExtra("encabezados")

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return
        tabla = findViewById(R.id.TableLayoutConsultar)
        procesar(consulta, encabezados)
    }

    private fun procesar(consulta: String?, encabezados: ArrayList<String>?) {
        var cursor: Cursor = baseDeDatos.rawQuery(
            consulta,
            null
        )
        if (cursor.count <= 0) {
            Rutinas.mensajeToast("Todavia no hay registros", this)
            return
        }
        hazEncabezados(encabezados)
        while (cursor.moveToNext()) {
            val fila = TableRow(this)
            val atributos = mutableListOf<String>()
            encabezados?.forEachIndexed { index, _ ->
                atributos.add(cursor.getString(index))
            }

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

    private fun hazEncabezados(encabezados: ArrayList<String>?) {
        val filaEncabezado = TableRow(this)
        if (encabezados != null) {
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
        }
        tabla.addView(filaEncabezado)
    }
}