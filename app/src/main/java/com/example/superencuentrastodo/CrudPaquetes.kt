package com.example.superencuentrastodo

import ManejoBaseDeDatos
import android.content.ContentValues
import android.content.Intent
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
    private lateinit var btnCrudPaquetesConsultar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crud_paquetes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conexion = ManejoBaseDeDatos(this, null)
        baseDeDatos = conexion.writableDatabase ?: return

        editTextCrudPaquetesIDPaquete = findViewById(R.id.editTextCrudPaquetesIDPaquete)
        editTextCrudPaquetesIDProducto = findViewById(R.id.editTextCrudPaquetesIDProducto)
        editTextCrudPaquetesPorcentajeDesc = findViewById(R.id.editTextCrudPaquetesDescuento)
        editTextCrudPaquetesNoUnidades = findViewById(R.id.editTextCrudPaquetesNoUnidades)
        btnCrudPaquetesRecuperar = findViewById(R.id.btnCrudPaquetesRecuperar)
        btnCrudPaquetesLimpiar = findViewById(R.id.btnCrudPaquetesLimpiar)
        btnCrudPaquetesGrabar = findViewById(R.id.btnCrudPaquetesGrabar)
        btnCrudPaquetesBorrar = findViewById(R.id.btnCrudPaquetesBorrar)
        btnCrudPaquetesActualizar = findViewById(R.id.btnCrudPaquetesActualizar)
        btnCrudPaquetesConsultar = findViewById(R.id.btnCrudPaquetesConsultar)

        escuchadores()

    }

    override fun onClick(v: View?) {
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

            btnCrudPaquetesConsultar -> {
                consultar()
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
        btnCrudPaquetesConsultar.setOnClickListener(this)
    }

    private fun limpiar() {
        editTextCrudPaquetesIDPaquete.setText("")
        editTextCrudPaquetesIDProducto.setText("")
        editTextCrudPaquetesPorcentajeDesc.setText("")
        editTextCrudPaquetesNoUnidades.setText("")
        editTextCrudPaquetesIDPaquete.requestFocus()
    }

    private fun grabar() {
        val paqueteId: String = editTextCrudPaquetesIDPaquete.text.toString()
        val productoId: String = editTextCrudPaquetesIDProducto.text.toString()
        val descuento: String = editTextCrudPaquetesPorcentajeDesc.text.toString()
        val noUnidades: String = editTextCrudPaquetesNoUnidades.text.toString()
        if (paqueteId == "" || productoId == "" || noUnidades == "" || descuento == "") {
            Rutinas.mensajeToast(
                "No se ha podido grabar debido a que hace falta informacion",
                this
            );
            limpiar()
            return
        }

        if (descuento.toInt() > 80 || descuento.toInt() <= 0) {
            Rutinas.mensajeToast(
                "El descuento debe de estar entre el 1% y el 80%",
                this
            );
            limpiar()
            return
        }

        if (noUnidades.toInt() == 0) {
            Rutinas.mensajeToast(
                "El numero de unidades no puede ser 0",
                this
            );
            limpiar()
            return
        }

        var cursor: Cursor =
            baseDeDatos.rawQuery(
                "SELECT COUNT(*) FROM Productos WHERE ProductoID = $productoId AND ProductoEstatus = 'A'",
                null
            )
        if (cursor.moveToFirst() && cursor.getInt(0) <= 0) {
            Rutinas.mensajeToast("No hay productos con el id $productoId", this)
            limpiar()
            return
        }
        cursor.close()
        cursor = baseDeDatos.rawQuery(
            "SELECT COUNT(*) FROM Paquetes WHERE PaqueteID = $paqueteId AND ProductoID = $productoId",
            null
        )
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            Rutinas.mensajeToast(
                "Ya existe el registro con PaqueteID $paqueteId y ProductoID $productoId",
                this
            )
            limpiar()
            return
        }
        cursor.close()
        try {
            baseDeDatos.execSQL(
                """
            INSERT INTO Paquetes
            VALUES ($paqueteId, $productoId, $noUnidades, $descuento);
        """.trimIndent()
            )
            Rutinas.mensajeToast("Grabado exitoso", this)
        } catch (e: Exception) {
            Rutinas.mensajeToast("El grabado ha fallado", this)
        }
        limpiar()
    }

    private fun recuperar() {
        val paqueteId: String = editTextCrudPaquetesIDPaquete.text.toString()
        val productoId: String = editTextCrudPaquetesIDProducto.text.toString()
        if (paqueteId == "" || productoId == "") {
            Rutinas.mensajeToast(
                "Se necesitan PaqueteID y ProductoID para recuperar la informacion",
                this
            );
            limpiar()
            return
        }
        var cursor: Cursor = baseDeDatos.rawQuery(
            "SELECT PaqueteNoUnidades, PaquetePorcentajeDesc FROM Paquetes WHERE PaqueteID = $paqueteId AND ProductoID = $productoId",
            null
        )
        if (cursor.moveToFirst()) {
            editTextCrudPaquetesNoUnidades.setText("" + cursor.getInt(0))
            editTextCrudPaquetesPorcentajeDesc.setText("" + cursor.getInt(1))
        } else {
            Rutinas.mensajeToast("No se ha encontrado el registro", this)
            limpiar()
        }
        cursor.close()
    }

    private fun borrar() {
        val paqueteId: String = editTextCrudPaquetesIDPaquete.text.toString()
        if (paqueteId == "") {
            Rutinas.mensajeToast(
                "El PaqueteID debe de contener informacion para poder borrar",
                this
            );
            limpiar()
            return
        }
        var cursor: Cursor =
            baseDeDatos.rawQuery(
                "SELECT COUNT(*) FROM Ventas WHERE ProdOPaq = 1 AND ID = $paqueteId",
                null
            )
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            Rutinas.mensajeToast(
                "No se puede eliminar ya que la tabla Ventas está asociada con este paquete",
                this
            )
            limpiar()
            return
        }
        cursor.close()
        val productoId: String = editTextCrudPaquetesIDProducto.text.toString()
        val aux: Int
        if (productoId == "")
            aux = baseDeDatos.delete("Paquetes", "PaqueteID = $paqueteId", null)
        else
            aux = baseDeDatos.delete(
                "Paquetes",
                "PaqueteID = $paqueteId AND ProductoID = $productoId",
                null
            )
        if (aux > 0)
            Rutinas.mensajeToast("Borrado exitoso", this)
        else
            Rutinas.mensajeToast("El borrado ha fallado", this)
        limpiar()
    }

    private fun actualizar() {
        val paqueteId: String = editTextCrudPaquetesIDPaquete.text.toString()
        val productoId: String = editTextCrudPaquetesIDProducto.text.toString()
        val descuento: String = editTextCrudPaquetesPorcentajeDesc.text.toString()
        val noUnidades: String = editTextCrudPaquetesNoUnidades.text.toString()
        if (paqueteId == "" || productoId == "" || noUnidades == "" || descuento == "") {
            Rutinas.mensajeToast(
                "No se ha podido actualizar debido a que hace falta informacion",
                this
            );
            limpiar()
            return
        }

        if (descuento.toInt() > 80 || descuento.toInt() <= 0) {
            Rutinas.mensajeToast(
                "El descuento debe de estar entre el 1% y el 80%",
                this
            );
            limpiar()
            return
        }
        if (noUnidades.toInt() == 0) {
            Rutinas.mensajeToast(
                "El numero de unidades no puede ser 0",
                this
            );
            limpiar()
            return
        }
        var cursor: Cursor =
            baseDeDatos.rawQuery(
                "SELECT COUNT(*) FROM Ventas WHERE ProdOPaq = 1 AND ID = $paqueteId",
                null
            )
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            Rutinas.mensajeToast(
                "No se puede actualizar ya que la tabla Ventas está asociada con este paquete",
                this
            )
            limpiar()
            return
        }
        cursor.close()
        val valores = ContentValues().apply {
            put("PaqueteNoUnidades", noUnidades.toInt())
            put("PaquetePorcentajeDesc", descuento.toInt())
        }
        val aux = baseDeDatos.update(
            "Paquetes",
            valores,
            "PaqueteID = $paqueteId AND ProductoID = $productoId",
            null
        )
        if (aux > 0)
            Rutinas.mensajeToast("Actualización exitosa", this)
        else
            Rutinas.mensajeToast(
                "No se encontró el registro con el PaqueteID $paqueteId y ProductoID = $productoId",
                this
            )
        limpiar()
    }

    private fun consultar() {
        val intent = Intent(this, TablaConsultar::class.java)
        intent.putExtra(
            "consulta",
            "SELECT * FROM Paquetes ORDER BY PaqueteID ASC"
        )
        val encabezados = listOf("PaqueteID", "ProductoID", "No. Unidades", "Porcentaje Descuento")
        intent.putExtra("encabezados", ArrayList(encabezados))
        startActivity(intent)
    }
}