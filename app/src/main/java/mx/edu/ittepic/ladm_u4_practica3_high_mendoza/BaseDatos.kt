package mx.edu.ittepic.ladm_u4_practica3_high_mendoza

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ENTRANTES(CELULAR VARCHAR(100),MENSAJE VARCHAR(2000))")
        db.execSQL("CREATE TABLE CALIFICACIONES(NO_CONTROL VARCHAR(100),U1 VARCHAR(10),U2 VARCHAR(10),U3 VARCHAR(10),U4 VARCHAR(10),U5 VARCHAR(10))")
    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}