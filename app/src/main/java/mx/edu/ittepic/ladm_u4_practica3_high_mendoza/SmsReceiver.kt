package mx.edu.ittepic.ladm_u4_practica3_high_mendoza

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast

class SmsReceiver : BroadcastReceiver(){
    var numeroOrigen = ""
    var mensaje = "Error de sintaxis -\nLa sintaxis correcta es: CALIFICACION No.Control U#"
    var unidad = ""
    var control = ""

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if (extras != null){
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){

                var formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //verificar que version de SMS manejo en mi celular
                    SmsMessage.createFromPdu(sms[indice] as  ByteArray,formato)

                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                numeroOrigen = celularOrigen.toString()

                //verificar sintaxis
                var arreglo = contenidoSMS.split(" ")

                if(arreglo.size>2) {
                    if (arreglo[0] == "CALIFICACION") {
                        control  = arreglo[1]
                        unidad = arreglo[2]

                        //Recuperar info de db y mandar mensaje
                        try{
                            var cursor = BaseDatos(context,"calificaciones",null,1)
                                .readableDatabase
                                .rawQuery("SELECT ${unidad} FROM CALIFICACIONES WHERE NO_CONTROL = $control",null)

                            var ultimo = ""

                            if(cursor.moveToFirst()){
                                do{
                                    ultimo = "Calificación "+unidad+": "+ cursor.getString(0)
                                    mensaje = ultimo
                                }while(cursor.moveToNext())

                            }else{
                                ultimo = "Error de sintaxis \nLa sintaxis correcta es: CALIFICACION No.Control U#"
                                mensaje = ultimo
                            }

                        }catch (err: SQLiteException){
                            Toast.makeText(context,err.message, Toast.LENGTH_LONG)
                                .show()
                        }

                    }else{
                        mensaje = "Error de sintaxis \nLa sintaxis correcta es: CALIFICACION No.Control U#"
                    }

                }else{
                    mensaje = "Error de sintaxis \nLa sintaxis correcta es: CALIFICACION No.Control U#"
                }

                //Guardar mensajes en bd
                try{
                    var baseDatos = BaseDatos(context,"entrantes",null,1)
                    var insertar  = baseDatos.writableDatabase
                    var SQL = "INSERT INTO ENTRANTES VALUES ('${celularOrigen}','${contenidoSMS}')"
                    insertar.execSQL(SQL)
                    baseDatos.close()

                }catch (err: SQLiteException){
                    Toast.makeText(context,err.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
            SmsManager.getDefault().sendTextMessage(numeroOrigen,null,
                mensaje,null,null)
        }

    }

}