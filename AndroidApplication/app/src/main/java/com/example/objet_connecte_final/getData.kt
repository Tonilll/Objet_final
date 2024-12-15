package com.example.objet_connecte_final

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

// Provient de figure111 de Maryse Mongeau
class getData {
    fun getData(stUrl: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(stUrl)
            .build()
        return try {
            client.newCall(request).execute().use { response: Response ->
                if (!response.isSuccessful) {
                    Log.e("ERREUR", "Erreur de connexion : ${response.code}")
                    null
                } else {
                    var reponse = response.body?.string()

                    reponse = reponse?.trim()

                    Log.i("DATAREPONSE", reponse.toString())
                    return reponse
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ERREUR", e.toString())
            null
        }

    }
}