package mx.edu.ittepic.ladm_u4_practica3_high_mendoza

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    class MainActivity : AppCompatActivity() {

        val siPermiso = 1
        val siPermisoReciver = 2
        var siPermisoLectura = 3

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)



            btnPermisos.setOnClickListener(){
                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.RECEIVE_SMS), siPermisoReciver
                        )
                    }
                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.SEND_SMS), siPermiso
                    )
                }

                Toast.makeText(this,"Permisos otorgados", Toast.LENGTH_SHORT).show()
            }

            leerAlumnos()
            insertarAlumnos()
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if(requestCode == siPermisoReciver){
                Toast.makeText(this,"Permiso para recibir mensajes otorgado", Toast.LENGTH_SHORT).show()
            }

            if(requestCode == siPermisoLectura){
                Toast.makeText(this,"Permiso de lectura otorgado", Toast.LENGTH_SHORT).show()
                leerAlumnos()
            }

        }

        private fun insertarAlumnos(){
            btnInsertar.setOnClickListener(){
                if(txtControl.text.toString() != "" || txtU1.text.toString() != "" && txtU2.text.toString() != "" ||
                    txtU3.text.toString() != "" || txtU4.text.toString() != "" || txtU5.text.toString() != ""){
                    try{

                        var baseDatos = BaseDatos(this,"calificaciones",null,1)
                        var insertar  = baseDatos.writableDatabase
                        var SQL = "INSERT INTO CALIFICACIONES VALUES ('${txtControl.text}','${txtU1.text}','${txtU2.text}','${txtU3.text}','${txtU4.text}','${txtU5.text})"

                        insertar.execSQL(SQL)
                        baseDatos.close()
                        Toast.makeText(this,"Datos Insertados", Toast.LENGTH_SHORT).show()

                        vaciar()

                    }catch (err: SQLiteException){
                        Toast.makeText(this,err.message, Toast.LENGTH_LONG)
                            .show()
                    }
                    leerAlumnos()
                }else{
                    Toast.makeText(this,"Faltan datos a insertar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun leerAlumnos(){
            try{
                var cursor = BaseDatos(this,"calificaciones",null,1)
                    .readableDatabase
                    .rawQuery("SELECT * FROM CALIFICACIONES",null)

                var ultimo = ""
                if(cursor.moveToFirst()){
                    do{
                        ultimo += "CALIFICACIONES:\n"+
                                "No_Control: "+ cursor.getString(0)+
                                "\nUnidad 1: "+cursor.getString(1)+
                                "\nUnidad 2: "+cursor.getString(2)+
                                "\nUnidad 3: "+cursor.getString(3)+
                                "\nUnidad 4: "+cursor.getString(4)+
                                "\nUnidad 5: "+cursor.getString(5)+
                                "\n--------------------------\n"
                    }while(cursor.moveToNext())
                }else{
                    ultimo = "Sin calificaciones registradas aún, Tabla vacía"
                }
                txtCal.setText(ultimo)
            }catch (err: SQLiteException){
                Toast.makeText(this,err.message, Toast.LENGTH_LONG)
                    .show()
            }
        }

        private fun envioSMS(){
            SmsManager.getDefault().sendTextMessage(txtControl.text.toString(),null,
                txtCal.text.toString(),null,null)
            Toast.makeText(this,"Se envió el sms", Toast.LENGTH_LONG)
                .show()
        }

        fun vaciar(){
            txtControl.setText("")
            txtU1.setText("")
            txtU2.setText("")
            txtU3.setText("")
            txtU4.setText("")
            txtU5.setText("")
        }
    }

}
