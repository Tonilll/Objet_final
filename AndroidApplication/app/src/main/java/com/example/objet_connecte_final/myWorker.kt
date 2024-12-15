package com.example.objet_connecte_final

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import org.json.JSONObject

class myWorker (context : Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    val CHANNEL_ID = "Channel1"
    // Provient de figure121b par Maryse Mongeau
    private fun creerCanal() {
        val channelName = "Channel1"
        val channelDescription = "Canal de notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    // Provient de figure123 de Maryse Mongeau
    private fun afficherNotification(id: Int, titre: String, texte: String) {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle(titre)
            .setContentText(texte)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ){
                notify(id, builder.build())
            }
        }
    }

    // Envoie des logs des données temps et heure
    override fun doWork(): Result {

        val connexionURL = inputData.getString("connexionURL")

        val getData = getData()
        var jsonData = getData.getData(connexionURL.toString())
        // Problème que } manquait des fois à la fin des json
        // alors regarde s'il le manque et l'ajoute en cas ou
        if (jsonData?.last() != '}'){
            // https://stackoverflow.com/questions/44188240/kotlin-how-to-correctly-concatenate-a-string
            val stringbuilder = StringBuilder()
            stringbuilder.append(jsonData).append("}")
            jsonData = stringbuilder.toString()
        }

        var Heure = 0
        var Temps = 0
        Log.i("i", jsonData.toString())
        if (jsonData != null) {
            val obj = JSONObject(jsonData)
            Temps = obj.getInt("Temps") ?: 0
            Heure = obj.getInt("Heure") ?: 0
        }

        val texte = """
            Temps : $Temps
            Heure : $Heure
        """.trimIndent()

        creerCanal()
        afficherNotification(1, "Nouvelle données", texte)

        return Result.success()

    }

}