import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.Serial
import java.io.Serializable

class ManejoBaseDeDatos(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, NOMBRE_DB, factory, VERSION) {

    companion object {
        private const val VERSION = 10
        private const val NOMBRE_DB = "VentasDB"
    }

    private val tablaProductos: String = """
        CREATE TABLE Productos(
            ProductoID INTEGER PRIMARY KEY AUTOINCREMENT,
            ProductoNombre TEXT NOT NULL,
            ProductoPrecioUnidad REAL NOT NULL,
            ProductoNoUnidades INTEGER NOT NULL,
            ProductoEstatus TEXT NOT NULL
            CHECK (ProductoPrecioUnidad > 0 AND ProductoEstatus IN('A', 'B'))
        )
    """.trimIndent()

    private val tablaPaquetes: String = """
        CREATE TABLE Paquetes (
            PaqueteID INTEGER,
            ProductoID INTEGER,
            PaqueteNoUnidades INTEGER NOT NULL,
            PaquetePorcentajeDesc INTEGER NOT NULL,
            PRIMARY KEY (PaqueteID, ProductoID),
            FOREIGN KEY (ProductoID) REFERENCES Productos(ProductoID), 
            CHECK (PaquetePorcentajeDesc < 80 AND PaqueteNoUnidades > 0 AND PaquetePorcentajeDesc > 0)
        )
    """.trimIndent()

    //Nota: en ProdOPaq  0 es producto y 1 es paquete
    private val tablaVentas: String = """
        CREATE TABLE Ventas(
            Folio INTEGER,
            ID INTEGER,
            ProdOPaq INTEGER,
            UnidadesVendidas INTEGER NOT NULL,
            TotalVenta REAL NOT NULL,
            FechaDeVenta TEXT NOT NULL,
            PRIMARY KEY (Folio, ID, ProdOPaq),
            CHECK (UnidadesVendidas > 0 AND ProdOPaq IN(0,1))
        )
    """.trimIndent()


    override fun onCreate(db: SQLiteDatabase?) {
        println("En el onCreate")
        db?.execSQL(tablaProductos)
        db?.execSQL(tablaPaquetes)
        db?.execSQL(tablaVentas)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("En el onUpgrade")
        db?.execSQL("DROP TABLE IF EXISTS Productos")
        db?.execSQL("DROP TABLE IF EXISTS Paquetes")
        db?.execSQL("DROP TABLE IF EXISTS Ventas")
        onCreate(db)
    }
}