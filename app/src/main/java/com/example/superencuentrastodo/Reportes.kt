package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.app.DatePickerDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class Reportes : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnPaqueteMas3ProductosMas10Unidades: Button
    private lateinit var btnProductoNoPaqueteNoVenta: Button
    private lateinit var btnProductosIndPaq: Button
    private lateinit var btnRangoFechas: Button
    private lateinit var editTextDateInicio: EditText
    private lateinit var editTextDateFinal: EditText
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
        btnProductosIndPaq = findViewById(R.id.btnProductosIndPaq)
        btnRangoFechas = findViewById(R.id.btnRangoFechas)
        editTextDateInicio = findViewById(R.id.editTextDateInicio)
        editTextDateFinal = findViewById(R.id.editTextDateFinal)
        escuchadores()
    }

    private fun escuchadores() {
        btnProductoNoPaqueteNoVenta.setOnClickListener(this)
        btnPaqueteMas3ProductosMas10Unidades.setOnClickListener(this)
        btnProductosIndPaq.setOnClickListener(this)
        btnRangoFechas.setOnClickListener(this)
        editTextDateInicio.setOnClickListener(this)
        editTextDateFinal.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
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

            btnProductosIndPaq -> {
                val intent = Intent(this, TablaConsultar::class.java)
                intent.putExtra(
                    "consulta",
                    """
                    SELECT p.ProductoID, p.ProductoNombre,
                    SUM(CASE WHEN v.ProdOPaq = 0 THEN v.TotalVenta ELSE 0 END) AS TotalVentaProductos,
                    SUM(CASE WHEN v.ProdOPaq = 1 THEN p.ProductoPrecioUnidad * pq.PaqueteNoUnidades * v.UnidadesVendidas * (1-(pq.PaquetePorcentajeDesc/100)) ELSE 0 END) AS TotalVentaPaquete
                    FROM Productos p
                    LEFT JOIN Paquetes pq ON p.ProductoID = pq.ProductoID
                    LEFT JOIN Ventas v ON (v.ID = p.ProductoID AND v.ProdOPaq = 0) OR (v.ID = pq.PaqueteID AND v.ProdOPaq = 1)
                    GROUP BY p.ProductoID, p.ProductoNombre;
                    """.trimIndent()
                )
                val encabezados = listOf("ProductoID", "Nombre", "Individual", "En Paquete")
                intent.putExtra("encabezados", ArrayList(encabezados))
                startActivity(intent)
            }

            btnRangoFechas -> {
                val fechaInicio = editTextDateInicio.text.toString()
                val fechaFinal = editTextDateFinal.text.toString()

                if (fechaInicio == "" || fechaFinal == "") {
                    Rutinas.mensajeToast("Se necesitan ambas fechas", this)
                    return
                }
                val intent = Intent(this, TablaConsultar::class.java)

                intent.putExtra(
                    "consulta",
                    """
                    WITH RECURSIVE dates(date) AS (
                        SELECT DATE('$fechaInicio')
                        UNION ALL
                        SELECT DATE(date, '+1 day')
                        FROM dates
                        WHERE date <= DATE('$fechaFinal')
                    ),
                    daily_sales AS (
                        SELECT p.ProductoID, p.ProductoNombre, d.date AS FechaDeVenta,
                        COALESCE(SUM(CASE WHEN v.ProdOPaq = 0 THEN v.UnidadesVendidas ELSE 0 END), 0) AS Unidades,
                        COALESCE(SUM(CASE WHEN v.ProdOPaq = 0 THEN v.TotalVenta ELSE 0 END), 0) AS Importe
                        FROM dates d
                        CROSS JOIN Productos p
                        LEFT JOIN Ventas v ON p.ProductoID = v.ID AND v.FechaDeVenta = d.date AND v.ProdOPaq = 0
                        GROUP BY p.ProductoID, p.ProductoNombre, d.date
                    )
                    SELECT * FROM daily_sales
                    ORDER BY ProductoID, FechaDeVenta;
                    """.trimIndent()
                )
                val encabezados =
                    listOf("ProductoID", "Nombre Producto", "Fecha de Venta", "Unidades", "Importe")
                intent.putExtra("encabezados", ArrayList(encabezados))
                startActivity(intent)
            }

            editTextDateInicio -> {
                configurarFechas(editTextDateInicio)
            }

            editTextDateFinal -> {
                configurarFechas(editTextDateFinal)
            }
        }
    }

    private fun configurarFechas(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val monthStr = (selectedMonth + 1).toString().padStart(2, '0')
                    val dayStr = selectedDay.toString().padStart(2, '0')
                    val selectedDate = "$selectedYear-$monthStr-$dayStr"
                    editText.setText(selectedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }
}