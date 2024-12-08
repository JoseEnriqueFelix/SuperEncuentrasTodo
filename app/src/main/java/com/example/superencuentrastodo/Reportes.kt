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
    private lateinit var btnProductosIndPaq: Button
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
        btnProductosIndPaq = findViewById(R.id.btnProductosIndPaq)
        layoutResultados = findViewById(R.id.layoutResultados)

        escuchadores()
    }

    private fun escuchadores() {
        btnProductoNoPaqueteNoVenta.setOnClickListener(this)
        btnPaqueteMas3ProductosMas10Unidades.setOnClickListener(this)
        btnProductosIndPaq.setOnClickListener(this)
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
                    LEFT JOIN Ventas v ON (v.ID = p.ProductoID AND v.ProdOPaq = 0) 
                    OR (v.ID = pq.PaqueteID AND v.ProdOPaq = 1)
                    GROUP BY p.ProductoID, p.ProductoNombre;
                    """.trimIndent()
                )
                val encabezados = listOf("ProductoID", "Nombre", "Individual", "En Paquete")
                intent.putExtra("encabezados", ArrayList(encabezados))
                startActivity(intent)
            }
        }
    }
}