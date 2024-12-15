package com.example.objet_connecte_final

import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val getData = getData()
    val sendPost = sendPost()

    @Test
    fun ChangerTemps() {
        val JsonString = """
                    {
                        "Heure": -1,
                        "Temps": 10,
                        "Dans": 0,
                        "Chaque": 0
                    }
                """.trimIndent()

        // Changer url pour autre objet
        sendPost.sendPost("http//10.4.129.35:8080", JsonString)

        val getData = getData()
        val jsonData = getData.getData("http//10.4.129.35:8080")
        var Heure = 0
        var Temps = 0
        if (jsonData != null) {
            val obj = JSONObject(jsonData)
            Temps = obj.getInt("Temps") ?: 0
            Heure = obj.getInt("Heure") ?: 0
        }

        assertEquals(Heure, 10)
    }

    @Test
    fun ChangerHeure() {
        val JsonString = """
                    {
                        "Heure": 10,
                        "Temps": -1,
                        "Dans": -1,
                        "Chaque": -1
                    }
                """.trimIndent()

        // Changer url pour autre objet
        sendPost.sendPost("http//10.4.129.35:8080", JsonString)

        val getData = getData()
        val jsonData = getData.getData("http//10.4.129.35:8080")
        var Heure = 0
        var Temps = 0
        if (jsonData != null) {
            val obj = JSONObject(jsonData)
            Temps = obj.getInt("Temps") ?: 0
            Heure = obj.getInt("Heure") ?: 0
        }

        assertEquals(Temps, 10)
    }
}