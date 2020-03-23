package com.example.bar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bar.config.NetworkConfig
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.json.simple.parser.JSONParser
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addSearchListener()
        addManagerListener()
    }


    public fun addSearchListener():Unit{
        bookSearchButton.setOnClickListener {
            var intent = Intent(this, SearchActivity::class.java )
            startActivity(intent)
        }
    }

    public fun addManagerListener():Unit{
        manageModeButton.setOnClickListener {
            var intent = Intent(this, ManagerActivity::class.java)
            startActivity(intent)
        }
    }
}
