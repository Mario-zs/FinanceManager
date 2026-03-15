package clases;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final String URL = "jdbc:sqlite:data/bd_fm.db";
    private static Connection cn;

    public static Connection conectar() {

        try {
            
            File carpeta = new File("data");
            if(!carpeta.exists()){
                carpeta.mkdir();
            }
            
            //Conectar con SQLite
            cn = DriverManager.getConnection(URL);
            
            //Activar Foreing keys
            try (Statement st = cn.createStatement()){
                st.execute("PRAGMA foreign_keys = ON");
            } catch (Exception e) {
                System.err.println("No se pudieron activar las foreign keys: " + e.getMessage());
            }

            //Crear tablas si no existen
            crearTablas();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
        return cn;
    }

    private static void crearTablas() {

        //Usuarios
        String tablaUsuarios = """
        CREATE TABLE IF NOT EXISTS usuarios (
            id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            pass_hash TEXT NOT NULL,
            fecha_creacion DATE NOT NULL
        );
        """;

        //Movimientos
        String tablaMovimientos = """
        CREATE TABLE IF NOT EXISTS movimientos (
            id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER NOT NULL,
            tipo TEXT NOT NULL,
            monto REAL NOT NULL,
            fecha DATE NOT NULL,
            comentarios TEXT,
            FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        );
        """;
        
        try (Statement st = cn.createStatement()){
            
            st.execute(tablaUsuarios);
            st.execute(tablaMovimientos);
            
        } catch (SQLException e) {
            System.err.println("Error al generar tablas. " + e);
        }
    }
}
