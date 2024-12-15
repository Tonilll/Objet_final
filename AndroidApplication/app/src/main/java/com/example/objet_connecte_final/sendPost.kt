package com.example.objet_connecte_final

import android.util.Log
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class sendPost {
    fun sendPost(stUrl : String, jsonMsg : String) {
        try {
            val url = URL(stUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.doInput = true
            DataOutputStream(conn.outputStream).use { os ->
                os.writeBytes(jsonMsg)
                os.flush()
            }


            Log.d("STATUS", conn.responseCode.toString())
            Log.d("MSG", conn.responseMessage)
            conn.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ERREUR", e.message ?: "Erreur inconnue")
        }
    }
}