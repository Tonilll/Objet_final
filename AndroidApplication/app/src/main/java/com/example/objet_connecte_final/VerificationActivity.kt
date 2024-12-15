package com.example.objet_connecte_final

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import org.json.JSONObject
import javax.crypto.Cipher

class VerificationActivity : AppCompatActivity() {
    //val sharedPerfs = getSharedPreferences("EchangePrefs", MODE_PRIVATE)

    //val connexionURL = sharedPerfs.getString("URL", "")
    //val clePublique = sharedPerfs.getString("ClePublique", "")
    //val getData = getData()
    //val data = Gson().fromJson(getData.getData(connexionURL.toString()), List::class.java)

    //val cipher = Cipher.getInstance("RSA")
    //cipher.init(Cipher.DECRYPT_MODE, clePublique)

    //val Temps = data[0]
    //val Heure = data[1]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        val sharedPerfs = getSharedPreferences("EchangePrefs", MODE_PRIVATE)

        val connexionURL = sharedPerfs.getString("URL", "")
        Log.i("URL", "$connexionURL")
        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val Selection = parent?.getItemAtPosition(position).toString()
                DataSelect(connexionURL.toString(), Selection)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                DataSelect(connexionURL.toString(), "Temps")
            }
        }

        val btnRetour : Button = findViewById(R.id.btnRetour)
        btnRetour.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)

            startActivity(intent)

            finish()
        }
    }

    /*

    Problème lorsque changement entre temps et heure

    si change et même affaire quand temps et heure ne sont pas la même affaire

    causé par problème de Json qui ne met pas } a la fin

     */

    // Fonction qui obtient et affiche les données temps et heure dépendament de la selection
    private fun DataSelect(connexionURL: String, selection: String) {
        val handler = Handler(Looper.getMainLooper())
        val thread = Thread {
            try {
                val getData = getData()
                var jsonData = getData.getData(connexionURL)
                if (jsonData?.last() != '}'){
                    // https://stackoverflow.com/questions/44188240/kotlin-how-to-correctly-concatenate-a-string
                    val stringbuilder = StringBuilder()
                    stringbuilder.append(jsonData).append("}")
                    jsonData = stringbuilder.toString()
                }
                Log.i("JSONDATAURL", connexionURL)
                Log.i("JSONDATA", jsonData.toString())

                val obj = JSONObject(jsonData)

                val Temps = obj.getInt("Temps")
                val Heure = obj.getInt("Heure")


                handler.post {
                    if (selection == "Temps"){
                        findViewById<TextView>(R.id.tvTimeHeure).text = Temps.toString()
                    }
                    else if (selection == "Heure"){
                        findViewById<TextView>(R.id.tvTimeHeure).text = Heure.toString()
                    }
                }
            }
            catch (e: Exception) {
                Log.e("DATA", "Erreur dans l'extraction des données")
            }
        }
        thread.start()
    }
}