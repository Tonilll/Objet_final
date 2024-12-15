package com.example.objet_connecte_final

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NotificationActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (!result) {
            Toast.makeText(this, "La permission n'a pas été accordée", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Provient de figure121a de Maryse Mongeau
        fun demanderPermissionNotification() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        val btnNotif : Button = findViewById(R.id.btnNotif)
        btnNotif.setOnClickListener {
            demanderPermissionNotification()
        }

        val btnRetour : Button = findViewById(R.id.btnRetour4)
        btnRetour.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)

            startActivity(intent)

            finish()
        }

    }
}