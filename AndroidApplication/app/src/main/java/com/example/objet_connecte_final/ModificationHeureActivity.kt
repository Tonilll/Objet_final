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
import androidx.work.OneTimeWorkRequest
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
import java.util.concurrent.TimeUnit
import kotlin.math.round

class ModificationHeureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_heure)

        // Obtient les préférences
        val sharedPerfs = getSharedPreferences("EchangePrefs", MODE_PRIVATE)

        // Obtient l'url de connexion
        val connexionURL = sharedPerfs.getString("URL", "")

        val sendPost = sendPost()


        val btnConfirmation : Button = findViewById(R.id.btnConfirmation)
        btnConfirmation.setOnClickListener {
            // Obtient input
            var ModifHeure = findViewById<EditText>(R.id.etModifHeure).text.toString()

            Log.i("MODIF", "$ModifHeure")

            // Transforme l'input en float
            var ModifHeureFloat : Float? = ModifHeure.toFloatOrNull()

            Log.i("MODIF", "$ModifHeureFloat")

            // Si l'input ne peut pas être en float = null
            if (ModifHeureFloat == null) {
                findViewById<TextView>(R.id.tvErreur).text = "L'entré doit être un nombre"
            }
            // Vérifie que l'input est entre 0 et 24
            else if (round(ModifHeureFloat) < 0 || round(ModifHeureFloat) > 24) {
                findViewById<TextView>(R.id.tvErreur).text = "L'entré doit être entre 0 et 24"
            }
            else {
                ModifHeureFloat = round(ModifHeureFloat)

                // Construction du string en forme json

                val JsonString = """
                    {
                        "Heure": $ModifHeureFloat,
                        "Temps": -1,
                        "Dans": -1,
                        "Chaque": -1
                    }
                """.trimIndent()
                val handler = Handler(Looper.getMainLooper())
                val thread = Thread {
                    sendPost.sendPost(connexionURL.toString(), JsonString)
                    handler.post {
                        findViewById<TextView>(R.id.tvErreur).text = "Nouvelles données envoyées"
                    }
                }
                thread.start()
            }
        }


        val btnRetour :Button = findViewById(R.id.btnRetour2)
        btnRetour.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)

            startActivity(intent)

            finish()
        }



    }

}