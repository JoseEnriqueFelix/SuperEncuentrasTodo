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

class CrudPaquetes : AppCompatActivity(), View.OnClickListener {
    private lateinit var conexion: ManejoBaseDeDatos
    private lateinit var baseDeDatos: SQLiteDatabase
    private lateinit var editTextCrudPaquetesIDPaquete: EditText
    private lateinit var editTextCrudPaquetesIDProducto: EditText
    private lateinit var editTextCrudPaquetesPorcentajeDesc: EditText
    private lateinit var editTextCrudPaquetesNoUnidades: EditText
    private lateinit var btnCrudPaquetesRecuperar: Button
    private lateinit var btnCrudPaquetesLimpiar: Button
    private lateinit var btnCrudPaquetesGrabar: Button
    private lateinit var btnCrudPaquetesBorrar: Button
    private lateinit var btnCrudPaquetesActualizar: Button
    private lateinit var btnCrudPaquetesRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crud_paquetes)
        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return

        editTextCrudPaquetesIDPaquete = findViewById(R.id.editTextCrudPaquetesIDPaquete)
        editTextCrudPaquetesIDProducto = findViewById(R.id.editTextCrudPaquetesIDProducto)
        editTextCrudPaquetesPorcentajeDesc = findViewById(R.id.editTextCrudPaquetesPorcentajeDesc)
        editTextCrudPaquetesNoUnidades = findViewById(R.id.editTextCrudPaquetesNoUnidades)
        btnCrudPaquetesRecuperar = findViewById(R.id.btnCrudPaquetesRecuperar)
        btnCrudPaquetesLimpiar = findViewById(R.id.btnCrudPaquetesLimpiar)
        btnCrudPaquetesGrabar = findViewById(R.id.btnCrudPaquetesGrabar)
        btnCrudPaquetesBorrar = findViewById(R.id.btnCrudPaquetesBorrar)
        btnCrudPaquetesActualizar = findViewById(R.id.btnCrudPaquetesActualizar)
        btnCrudPaquetesRegresar = findViewById(R.id.btnCrudPaquetesRegresar)

        escuchadores()

    }

    override fun onClick(v: View?) {
        println("En el onclick")
        when (v) {
            btnCrudPaquetesRecuperar -> {
                recuperar()
                return
            }

            btnCrudPaquetesLimpiar -> {
                limpiar()
                return
            }

            btnCrudPaquetesGrabar -> {
                grabar()
                return
            }

            btnCrudPaquetesBorrar -> {
                borrar()
                return
            }

            btnCrudPaquetesActualizar -> {
                actualizar()
                return
            }

            btnCrudPaquetesRegresar -> {
                //TODO
                return
            }
        }
    }

    private fun escuchadores() {
        btnCrudPaquetesRecuperar.setOnClickListener(this)
        btnCrudPaquetesLimpiar.setOnClickListener(this)
        btnCrudPaquetesGrabar.setOnClickListener(this)
        btnCrudPaquetesBorrar.setOnClickListener(this)
        btnCrudPaquetesActualizar.setOnClickListener(this)
        btnCrudPaquetesRegresar.setOnClickListener(this)
    }

    private fun limpiar() {
        editTextCrudPaquetesIDPaquete.setText("")
        editTextCrudPaquetesIDProducto.setText("")
        editTextCrudPaquetesPorcentajeDesc.setText("")
        editTextCrudPaquetesNoUnidades.setText("")
        editTextCrudPaquetesIDPaquete.requestFocus()
    }

    private fun grabar() {
        /*
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
         */
    }

    private fun recuperar() {
        /*
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

        if (cursor?.moveToFirst() == true) {
            editTextCrudProductosNombre.setText(cursor.getString(0))
            editTextCrudProductosPrecio.setText(cursor.getDouble(1).toString())
            editTextCrudProductosNoUnidades.setText(cursor.getInt(2).toString())
        } else {
            Rutinas.mensajeToast("No hay productos con el id $id", this)
            limpiar()
        }
        cursor.close()
        */
    }

    private fun borrar() {
        /*
        if (editTextCrudProductosID.text.toString() == "") {
            Rutinas.mensajeToast(
                "El id debe de contener informacion para poder borrar",
                this
            );
            limpiar()
            return
        }
        var id: String = editTextCrudProductosID.text.toString()
        var aux: Int = baseDeDatos.delete("Productos", "ProductoID = $id", null)
        if (aux > 0)
            Rutinas.mensajeToast("Borrado exitoso", this)
        else
            Rutinas.mensajeToast("No se encontró el producto con el id $id", this)
        limpiar()
        */
    }

    private fun actualizar() {
        /*
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
        var cursor: Cursor =
            baseDeDatos.rawQuery(
                "SELECT * FROM Productos WHERE ProductoID = $id AND ProductoEstatus = 'A'",
                null
            )
        if (!cursor.moveToFirst()) {
            Rutinas.mensajeToast("No hay productos con el id $id", this)
            limpiar()
            return
        }
        cursor.close()
        var valores = ContentValues().apply {
            put("ProductoNombre", nombre)
            put("ProductoPrecioUnidad", precio.toDouble())
            put("ProductoNoUnidades", noUnidades.toInt())
        }
        var aux: Int = baseDeDatos.update("Productos", valores, "ProductoID = $id", null)
        if (aux > 0)
            Rutinas.mensajeToast("Actualización exitosa", this)
        else
            Rutinas.mensajeToast("No se encontró el producto con el id $id", this)
        limpiar()
        */
    }
}