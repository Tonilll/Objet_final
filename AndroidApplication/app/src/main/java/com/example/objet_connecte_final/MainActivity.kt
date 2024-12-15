package com.example.objet_connecte_final

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyPairGenerator
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnConfirmer :Button = findViewById(R.id.btnConfirmation3)
        btnConfirmer.setOnClickListener {
            // obtient les inputs des deux edittext qui sont l'ip et le port
            val IP = findViewById<EditText>(R.id.etIP).text.toString()
            val Port = findViewById<EditText>(R.id.etPort).text.toString()
            // création de l'url dans le bon format
            val connexionURL = "http://" + IP + ":" + Port + "/"
            try {
                // Fait avec l'aide de Chatgpt

                // générer la clé
                //val clees = KeyPairGenerator.getInstance("RSA")

                //val clePrivee = clees.generateKeyPair().private

                //val cle = data[2]

                // Création d'une préférence pour partager l'url
                val sharedPrefs = getSharedPreferences("EchangePrefs", MODE_PRIVATE)

                val editor = sharedPrefs.edit()
                editor.putString("URL", connexionURL)

                editor.apply()

                //editor.putString("ClePublique", cle.toString())

                //editor.putString("ClePrive", clePrivee.toString())

                // Fait avec l'aide de Chatgpt //


                // Les données json que reçoit l'application a un problème
                // Ce code permetterait les notifications si les json serait correct

                // Regarde pour des changements chaque 5 secondes
                val thread = Thread {

                    while (true) {
                        val getData = getData()
                        var jsonData = getData.getData(connexionURL)
                        // Problème que } manquait des fois à la fin des json
                        // alors regarde s'il le manque et l'ajoute en cas ou
                        if (jsonData?.last() != '}'){
                            // https://stackoverflow.com/questions/44188240/kotlin-how-to-correctly-concatenate-a-string
                            val stringbuilder = StringBuilder()
                            stringbuilder.append(jsonData).append("}")
                            jsonData = stringbuilder.toString()
                        }

                        Log.i("i", jsonData.toString())

                        val obj = JSONObject(jsonData)
                        val Temps = obj.getInt("Temps") ?: 0
                        val Heure = obj.getInt("Heure") ?: 0

                        var nouvelle = false

                        while (!nouvelle) {
                            val getData2 = getData()
                            var jsonData2 = getData2.getData(connexionURL)
                            if (jsonData2?.last() != '}'){
                                // https://stackoverflow.com/questions/44188240/kotlin-how-to-correctly-concatenate-a-string
                                val stringbuilder = StringBuilder()
                                stringbuilder.append(jsonData2).append("}")
                                jsonData2 = stringbuilder.toString()
                            }
                            Log.i("i2", jsonData2.toString())

                            val obj2 = JSONObject(jsonData2)
                            val Temps2 = obj2.getInt("Temps") ?: 0
                            val Heure2 = obj2.getInt("Heure") ?: 0

                            Thread.sleep(5000)
                            if (Temps2 != Temps || Heure2 != Heure) {
                                // Create input data for the worker
                                val inputData = Data.Builder()
                                    .putString(
                                        "connexionURL",
                                        connexionURL
                                    ) // Pass connexionURL as a string
                                    .build()

                                // Now schedule the worker with the input data
                                val workRequest = OneTimeWorkRequestBuilder<myWorker>()
                                    .setInputData(inputData) // Pass the input data to the worker
                                    .build()

                                WorkManager.getInstance(applicationContext).enqueue(workRequest)

                                // s'il a une donnée différente, transforme en true
                                // ce qui brise cette loop
                                nouvelle = true
                                Thread.sleep(5000)
                            }
                        }
                        Thread.sleep(5000)
                    }
                }

                // Commence le thread
                thread.start()

                val intent = Intent(this, MenuActivity::class.java)

                startActivity(intent)
            }
            catch (e : Exception){
                findViewById<TextView>(R.id.tvErreur3).text = e.message
            }

        }
    }
}