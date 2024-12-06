package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CrudProductos : AppCompatActivity(), View.OnClickListener {
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase
    private lateinit var editTextCrudProductosID: EditText
    private lateinit var editTextCrudProductosNombre: EditText
    private lateinit var editTextCrudProductosPrecio: EditText
    private lateinit var editTextCrudProductosNoUnidades: EditText
    private lateinit var btnCrudProductosRecuperar: Button
    private lateinit var btnCrudProductosLimpiar: Button
    private lateinit var btnCrudProductosGrabar: Button
    private lateinit var btnCrudProductosBorrar: Button
    private lateinit var btnCrudProductosActualizar: Button
    private lateinit var btnCrudProductosRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crud_productos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return

        editTextCrudProductosID = findViewById(R.id.editTextCrudPaquetesIDPaquete)
        editTextCrudProductosNombre = findViewById(R.id.editTextCrudPaquetesIDProducto)
        editTextCrudProductosPrecio = findViewById(R.id.editTextCrudPaquetesPorcentajeDesc)
        editTextCrudProductosNoUnidades = findViewById(R.id.editTextCrudPaquetesNoUnidades)
        btnCrudProductosRecuperar = findViewById(R.id.btnCrudProductosRecuperar)
        btnCrudProductosLimpiar = findViewById(R.id.btnCrudProductosLimpiar)
        btnCrudProductosGrabar = findViewById(R.id.btnCrudProductosGrabar)
        btnCrudProductosBorrar = findViewById(R.id.btnCrudProductosBorrar)
        btnCrudProductosActualizar = findViewById(R.id.btnCrudProductosActualizar)
        btnCrudProductosRegresar = findViewById(R.id.btnCrudProductosRegresar)

        escuchadores()

    }

    override fun onClick(v: View?) {
        println("En el onclick")
        when (v) {
            btnCrudProductosRecuperar -> {
                recuperar()
                return
            }

            btnCrudProductosLimpiar -> {
                limpiar()
                return
            }

            btnCrudProductosGrabar -> {
                grabar()
                return
            }

            btnCrudProductosBorrar -> {
                borrar()
                return
            }

            btnCrudProductosActualizar -> {
                actualizar()
                return
            }

            btnCrudProductosRegresar -> {
                //TODO
                return
            }
        }
    }

    private fun escuchadores() {
        btnCrudProductosRecuperar.setOnClickListener(this)
        btnCrudProductosLimpiar.setOnClickListener(this)
        btnCrudProductosGrabar.setOnClickListener(this)
        btnCrudProductosBorrar.setOnClickListener(this)
        btnCrudProductosActualizar.setOnClickListener(this)
        btnCrudProductosRegresar.setOnClickListener(this)
    }

    private fun limpiar() {
        editTextCrudProductosID.setText("")
        editTextCrudProductosNombre.setText("")
        editTextCrudProductosPrecio.setText("")
        editTextCrudProductosNoUnidades.setText("")
        editTextCrudProductosID.requestFocus()
    }

    private fun grabar() {
        if (editTextCrudProductosID.text.toString() != "") {
            Rutinas.mensajeToast(
                "No se ha podido grabar debido a que el ID del producto contiene informacion",
                this
            );
            limpiar()
            return
        }
        var nombre: String = editTextCrudProductosNombre.text.toString()
        var precio: String = editTextCrudProductosPrecio.text.toString()
        var noUnidades: String = editTextCrudProductosNoUnidades.text.toString()
        if (nombre == "" || precio == "" || noUnidades == "") {
            Rutinas.mensajeToast(
                "No se ha podido grabar debido a que hace falta informacion",
                this
            );
            limpiar()
            return
        }
        baseDeDatos.execSQL(
            """
            INSERT INTO Productos (ProductoNombre, ProductoPrecioUnidad, ProductoNoUnidades, ProductoEstatus)
            VALUES ('$nombre', $precio, $noUnidades, 'A');
        """.trimIndent()
        )
        Rutinas.mensajeToast("Grabado exitoso", this)
        limpiar()
    }

    private fun recuperar() {
        if (editTextCrudProductosID.text.toString() == "") {
            Rutinas.mensajeToast(
                "El id debe de contener informacion para poder realizar una consulta",
                this
            );
            limpiar()
            return
        }
        var id: String = editTextCrudProductosID.text.toString()
        var cursor: Cursor =
            baseDeDatos.rawQuery(
                "SELECT ProductoNombre, ProductoPrecioUnidad, ProductoNoUnidades FROM Productos WHERE ProductoID = $id AND ProductoEstatus = 'A'",
                null
            )

        if (cursor.moveToFirst()) {
            editTextCrudProductosNombre.setText(cursor.getString(0))
            editTextCrudProductosPrecio.setText(cursor.getDouble(1).toString())
            editTextCrudProductosNoUnidades.setText(cursor.getInt(2).toString())
        } else {
            Rutinas.mensajeToast("No hay productos con el id $id", this)
            limpiar()
        }
        cursor.close()
    }

    private fun borrar() {
        if (editTextCrudProductosID.text.toString() == "") {
            Rutinas.mensajeToast(
                "El id debe de contener informacion para poder borrar",
                this
            );
            limpiar()
            return
        }
        var id: String = editTextCrudProductosID.text.toString()

        baseDeDatos.execSQL(
            """
            UPDATE Productos 
            SET ProductoEstatus = 'B' 
            WHERE ProductoID = $id;
        """.trimIndent()
        )
        Rutinas.mensajeToast("Borrado exitoso", this)
        var aux: Int = baseDeDatos.delete("Productos", "ProductoID = $id", null)
        limpiar()
    }

    private fun actualizar() {
        var id: String = editTextCrudProductosID.text.toString()
        var nombre: String = editTextCrudProductosNombre.text.toString()
        var precio: String = editTextCrudProductosPrecio.text.toString()
        var noUnidades: String = editTextCrudProductosNoUnidades.text.toString()
        if (id == "" || nombre == "" || precio == "" || noUnidades == "") {
            Rutinas.mensajeToast(
                "No se ha podido actualizar debido a que hace falta informacion",
                this
            );
            limpiar()
            return
        }
        var valores = ContentValues().apply {
            put("ProductoNombre", nombre)
            put("ProductoPrecioUnidad", precio.toDouble())
            put("ProductoNoUnidades", noUnidades.toInt())
        }
        var aux: Int = baseDeDatos.update(
            "Productos",
            valores,
            "ProductoID = $id AND ProductoEstatus = 'A'",
            null
        )
        if (aux > 0)
            Rutinas.mensajeToast("Actualización exitosa", this)
        else
            Rutinas.mensajeToast("No se encontró el producto con el id $id", this)
        limpiar()
    }
}