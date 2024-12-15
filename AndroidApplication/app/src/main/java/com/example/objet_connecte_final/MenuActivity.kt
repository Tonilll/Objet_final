package com.example.objet_connecte_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Bouton vers l'horaire
        val btnHoraire : Button = findViewById(R.id.btnHoraire)
        btnHoraire.setOnClickListener {
            val intent = Intent(this, VerificationActivity::class.java)

            startActivity(intent)

            finish()
        }

        // Bouton vers la modification du temps
        val btnTemps : Button = findViewById(R.id.btnModifTemps)
        btnTemps.setOnClickListener {
            val intent = Intent(this, ModificationTempsActivity::class.java)

            startActivity(intent)

            finish()
        }

        // Bouton vers la modification de l'heure
        val btnHeure : Button = findViewById(R.id.btnModifHeure)
        btnHeure.setOnClickListener {
            val intent = Intent(this, ModificationHeureActivity::class.java)

            startActivity(intent)

            finish()
        }

        // Bouton vers la modification des notifications
        val btnNotification : Button = findViewById(R.id.btnNotification)
        btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)

            startActivity(intent)

            finish()
        }

    }
}