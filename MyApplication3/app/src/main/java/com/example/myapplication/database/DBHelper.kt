package com.example.myapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

/**
 * DatabasHelper to connect with the database
 */
class DbHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * function to create a database
     */
    override fun onCreate(db: SQLiteDatabase?) {
        // Die Methode bleibt leer, da wir eine bestehende Datenbank aus den assets verwende.
    }

    /**
     * function to upgrade a database
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        context.deleteDatabase(DATABASE_NAME)
        copyDatabaseFromAssets()
    }

    /**
     * function to only read in the database
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getReadableDatabase()
    }

    /**
     * function to write in the database
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getWritableDatabase()
    }

    /**
     * function to copy the database from the assets
     */
    private fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        if (!dbPath.exists()) {
            try {
                context.assets.open(DATABASE_NAME).use { inputStream ->
                    FileOutputStream(dbPath).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                // Debug-Log : Erfolg
                android.util.Log.d("DbHelper", "Database copied successfully to: ${dbPath.absolutePath}")
                // Debug-Log Größe der Datei prüfen
                android.util.Log.d("DbHelper", "Database size: ${dbPath.length()} bytes")

            } catch (e: Exception) {
                //Debug-Log: Fail
                android.util.Log.e("DbHelper", "Error copying database", e)
            }
        }
    }

    //Database we wanna connect to and the version of it
    companion object {
        const val DATABASE_NAME = "test.db"
        const val DATABASE_VERSION = 1
    }
}