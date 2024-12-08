package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.RadioButton
import java.util.Calendar

class ProcesarVentas : AppCompatActivity(), View.OnClickListener {
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase
    private lateinit var editTextVentaFolio: EditText
    private lateinit var editTextVentaID: EditText
    private lateinit var editTextVentaUnidades: EditText
    private lateinit var editTextDate: EditText
    private lateinit var textViewVentasPrecio: TextView
    private lateinit var btnVentasProcesar: Button
    private lateinit var btnVentasLimpiar: Button
    private lateinit var rbProducto: RadioButton
    private lateinit var rbPaquete: RadioButton
    private lateinit var btnProcesarVentasConsultar: Button
    private var prodOPaq: Int? = null //0 => producto y 1 => paquete

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_procesar_ventas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return

        editTextVentaFolio = findViewById(R.id.editTextVentaFolio)
        editTextVentaID = findViewById(R.id.editTextVentaID)
        editTextVentaUnidades = findViewById(R.id.editTextVentaUnidades)
        editTextDate = findViewById(R.id.editTextDate)
        textViewVentasPrecio = findViewById(R.id.textViewVentasPrecio)
        btnVentasProcesar = findViewById(R.id.btnVentasProcesar)
        btnVentasLimpiar = findViewById(R.id.btnVentasLimpiar)
        rbProducto = findViewById(R.id.rbProducto)
        rbPaquete = findViewById(R.id.rbPaquete)
        btnProcesarVentasConsultar = findViewById(R.id.btnProcesarVentasConsultar)
        escuchadores()
    }

    private fun escuchadores() {
        btnVentasProcesar.setOnClickListener(this)
        btnVentasLimpiar.setOnClickListener(this)
        btnProcesarVentasConsultar.setOnClickListener(this)
        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    editTextDate.setText(selectedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btnVentasLimpiar -> {
                limpiar()
            }

            btnVentasProcesar -> {
                procesarVenta()
            }

            btnProcesarVentasConsultar -> {
                val intent = Intent(this, TablaConsultar::class.java)
                intent.putExtra(
                    "consulta",
                    "SELECT * FROM Ventas ORDER BY Folio ASC"
                )
                val encabezados = listOf(
                    "Folio",
                    "ID",
                    "ProdOPaq",
                    "UnidadesVendidas",
                    "TotalVenta",
                    "FechaDeVenta"
                )
                intent.putExtra("encabezados", ArrayList(encabezados))
                startActivity(intent)
            }
        }
    }

    private fun limpiar() {
        editTextVentaFolio.setText("")
        editTextVentaID.setText("")
        editTextVentaUnidades.setText("")
        editTextDate.setText("")
        textViewVentasPrecio.text = ""
        rbPaquete.isChecked = false
        rbProducto.isChecked = false
        prodOPaq = null
        editTextVentaFolio.requestFocus()
    }

    private fun procesarVenta() {
        if (rbProducto.isChecked)
            prodOPaq = 0
        if (rbPaquete.isChecked)
            prodOPaq = 1
        val folio = editTextVentaFolio.text.toString()
        val id = editTextVentaID.text.toString()
        val unidades = editTextVentaUnidades.text.toString()
        val fecha = editTextDate.text.toString()
        if (prodOPaq == null || folio == "" || id == "" || unidades == "" || fecha == "") {
            Rutinas.mensajeToast(
                "No se ha podido procesar la venta debido a que hace falta informacion",
                this
            )
            limpiar()
            return
        }
        if (unidades.toInt() == 0) {
            Rutinas.mensajeToast(
                "Las cantidad de unidades no puede ser 0",
                this
            )
            limpiar()
            return
        }
        if (prodOPaq == 0)
            trabajaProductos(prodOPaq, folio, id, unidades, fecha)
        else
            trabajaPaquetes(prodOPaq, folio, id, unidades, fecha)
    }

    private fun trabajaProductos(
        prodOPaq: Int?,
        folio: String,
        id: String,
        unidades: String,
        fecha: String
    ) {
        var cursor: Cursor =
            baseDeDatos.rawQuery(
                "SELECT COUNT(*) FROM Ventas WHERE ProdOPaq = $prodOPaq AND Folio = $folio AND ID = $id",
                null
            )
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            Rutinas.mensajeToast("Venta previamente registrada", this)
            limpiar()
            return
        }
        cursor.close()
        cursor = baseDeDatos.rawQuery(
            "SELECT COUNT(*) FROM Productos WHERE ProductoID = $id AND ProductoEstatus = 'A'",
            null
        )
        if (cursor.moveToFirst() && cursor.getInt(0) <= 0) {
            Rutinas.mensajeToast("No hay productos con el id $id", this)
            limpiar()
            return
        }
        cursor.close()
        cursor = baseDeDatos.rawQuery(
            "SELECT ProductoNoUnidades FROM Productos WHERE ProductoID = $id AND ProductoEstatus = 'A'",
            null
        )
        cursor.moveToFirst()
        var unidadesTablaProductos: Int = cursor.getInt(0)
        cursor.close()
        unidadesTablaProductos -= unidades.toInt()
        if (unidadesTablaProductos < 0) {
            Rutinas.mensajeToast(
                "No existen suficientes unidades de producto para realizar la venta",
                this
            )
            limpiar()
            return
        }
        var cadena: String = """
            UPDATE Productos
            SET ProductoNoUnidades = $unidadesTablaProductos
            WHERE ProductoID = $id; 
        """.trimIndent()
        baseDeDatos.execSQL(cadena)
        cursor = baseDeDatos.rawQuery(
            "SELECT ProductoPrecioUnidad FROM Productos WHERE ProductoID = $id AND ProductoEstatus = 'A'",
            null
        )
        cursor.moveToFirst()
        val precio: Double = cursor.getDouble(0) * unidades.toInt()
        cursor.close()
        cadena = """
            INSERT INTO Ventas (Folio, ID, ProdOPaq, UnidadesVendidas, TotalVenta, FechaDeVenta)
            VALUES ($folio, $id, $prodOPaq, $unidades, $precio, '$fecha')
        """.trimIndent()
        baseDeDatos.execSQL(cadena)

        Rutinas.mensajeToast("Se ha procesado la venta con el folio $folio del producto $id", this)
        textViewVentasPrecio.setText("" + precio)
    }

    private fun trabajaPaquetes(
        prodOPaq: Int?,
        folio: String,
        id: String,
        unidades: String,
        fecha: String
    ) {
        var cursor: Cursor =
            baseDeDatos.rawQuery(
                "SELECT COUNT(*) FROM Ventas WHERE ProdOPaq = $prodOPaq AND Folio = $folio AND ID = $id",
                null
            )
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            Rutinas.mensajeToast("Venta previamente registrada", this)
            limpiar()
            return
        }
        cursor.close()
        cursor = baseDeDatos.rawQuery(
            "SELECT $unidades * PaqueteNoUnidades, ProductoID FROM Paquetes WHERE PaqueteID = $id",
            null
        )
        var mapTotalUnidadesProducto = mutableListOf<Pair<Int, Int>>()
        while (cursor.moveToNext()) {
            val unidadesTotales = cursor.getInt(0)
            val productoID = cursor.getInt(1)
            mapTotalUnidadesProducto.add(Pair(unidadesTotales, productoID))
        }
        cursor.close()
        var mapNuevoValUnidadesProducto = mutableListOf<Pair<Int, Int>>()
        mapTotalUnidadesProducto.forEach { par ->
            cursor = baseDeDatos.rawQuery(
                "SELECT COUNT(*) FROM Productos WHERE ProductoID = ${par.second} AND ProductoEstatus = 'A'",
                null
            )
            if (cursor.moveToFirst() && cursor.getInt(0) <= 0) {
                Rutinas.mensajeToast("No hay productos con el id $id", this)
                limpiar()
                return
            }
            cursor.close()
            cursor = baseDeDatos.rawQuery(
                "SELECT COUNT(*), ProductoNoUnidades-${par.first} FROM Productos WHERE ProductoID = ${par.second} AND ProductoNoUnidades >= ${par.first}",
                null
            )
            cursor.moveToFirst()
            if (cursor.getInt(0) <= 0) {
                Rutinas.mensajeToast(
                    "No existen suficientes unidades de producto para realizar la venta",
                    this
                )
                limpiar()
                return
            }
            val nuevoValorExistencias: Int = cursor.getInt(1)
            cursor.close()
            mapNuevoValUnidadesProducto.add(Pair(nuevoValorExistencias, par.second))
        }
        mapNuevoValUnidadesProducto.forEach { par ->
            var cadena: String = """
            UPDATE Productos
            SET ProductoNoUnidades = ${par.first}
            WHERE ProductoID = ${par.second}; 
        """.trimIndent()
            baseDeDatos.execSQL(cadena)
        }
        var precioTotal: Double = 0.0
        mapTotalUnidadesProducto.forEach { par ->
            cursor = baseDeDatos.rawQuery(
                "SELECT SUM(ProductoPrecioUnidad * ${par.first}) FROM Productos WHERE ProductoID = ${par.second}",
                null
            )
            cursor.moveToFirst()
            var aux: Double = cursor.getDouble(0)
            cursor.close()
            cursor = baseDeDatos.rawQuery(
                "SELECT PaquetePorcentajeDesc FROM Paquetes WHERE PaqueteID = $id AND ProductoID = ${par.second}",
                null
            )
            cursor.moveToFirst()
            var descuento: Double = cursor.getInt(0).toDouble()
            descuento = (1 - (descuento / 100))
            println("Descuento => " + descuento)
            cursor.close()
            aux *= descuento
            precioTotal += aux

        }
        val cadena = """
            INSERT INTO Ventas (Folio, ID, ProdOPaq, UnidadesVendidas, TotalVenta, FechaDeVenta)
            VALUES ($folio, $id, $prodOPaq, $unidades, $precioTotal, '$fecha')
        """.trimIndent()
        baseDeDatos.execSQL(cadena)
        Rutinas.mensajeToast("Se ha procesado la venta con el folio $folio del paquete $id", this)
        textViewVentasPrecio.setText("" + precioTotal)
    }
}