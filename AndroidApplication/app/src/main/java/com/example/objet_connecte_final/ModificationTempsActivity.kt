package com.example.objet_connecte_final

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.round

class ModificationTempsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_temps)

        val sharedPerfs = getSharedPreferences("EchangePrefs", MODE_PRIVATE)

        val connexionURL = sharedPerfs.getString("URL", "")

        val sendPost = sendPost()

        val btnConfirmation : Button = findViewById(R.id.btnConfirmation2)
        btnConfirmation.setOnClickListener {
            val ButtonId = findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId

            val text = findViewById<RadioButton>(ButtonId).text

            var temps = ""
            // Transforme les entrées en forme de minute 1h = 60 ect
            if (text == "30 Min") {
                temps = "30"
            }
            else if (text == "1 H") {
                temps = "60"
            }
            else if (text == "1 H 30") {
                temps = "90"
            }

            // Obtient les 2 autres input
            var dans = findViewById<EditText>(R.id.etDans).text.toString()
            var chaque = findViewById<EditText>(R.id.etChaque).text.toString()

            // Transforme les 2 inputs en float
            var dansFloat : Float? = dans.toFloatOrNull()
            var chaqueFloat : Float? = chaque.toFloatOrNull()
            Log.i("DANS", "$dansFloat")
            Log.i("CHAQUE", "$chaqueFloat")
            // Vérifie qu'ils peuvent être en float
            if (dansFloat == null || chaqueFloat == null) {
                findViewById<TextView>(R.id.tvErreur2).text = "Attention, les deux options dans et chaque doivent être des nombres"
            }
            else {
                findViewById<TextView>(R.id.tvErreur2).text = ""
                dansFloat = round(dansFloat)
                chaqueFloat = round(chaqueFloat)

                // Construction de la string en forme json
                val JsonString = """
                    {
                        "Temps": $temps,
                        "Heure": -1,
                        "Dans": $dansFloat,
                        "Chaque": $chaqueFloat
                    }
                """.trimIndent()
                val handler = Handler(Looper.getMainLooper())
                val thread = Thread {
                    sendPost.sendPost(connexionURL.toString(), JsonString)
                    handler.post {
                        findViewById<TextView>(R.id.tvErreur2).text = "Nouvelles données envoyées"
                    }
                }
                thread.start()
            }


        }


        val btnRetour : Button = findViewById(R.id.btnRetour3)
        btnRetour.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)

            startActivity(intent)

            finish()
        }
    }
}