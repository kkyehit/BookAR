package com.example.bar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bar.config.NetworkConfig
import kotlinx.android.synthetic.main.manager_activity.*
import org.json.JSONObject
import org.json.simple.parser.JSONParser
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ManagerActivity: AppCompatActivity() {

    private var networkConfig = NetworkConfig()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manager_activity)

        addNewTableListener()
        addNewBookListener()
    }

    public fun addNewTableListener():Unit{
        newTableButton.setOnClickListener {
            var lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            /*permission check한 후 현재 위치 location에 저장*/
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@setOnClickListener
            }
            var location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            //Toast.makeText(this@MainActivity, "위도" + location.longitude + "경도"+ location.latitude + "고도"+location.altitude, Toast.LENGTH_SHORT ).show()


            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.new_table_name_input_layout, null)
            val dialogEditText = dialogView.findViewById<EditText>(R.id.newTBNameET)
            val dialogEditText2 = dialogView.findViewById<EditText>(R.id.newTBFloorET)

            builder.setView(dialogView)
                .setPositiveButton("확인") { DialogInterface, i ->

                    var newName = dialogEditText.text.toString()
                    var newFloor = dialogEditText2.text.toString()
                    class netThread : Thread() {
                        override fun run() {
                            try {
                                var jsonObject = JSONObject()
                                Log.d("log","isonObject is created")
                                jsonObject.accumulate("name", newName)
                                jsonObject.accumulate("x", ""+location.longitude)
                                jsonObject.accumulate("y", ""+location.latitude)
                                jsonObject.accumulate("z", ""+location.altitude)
                                jsonObject.accumulate("floor", newFloor)

                                var json = jsonObject.toString()
                                Log.d("log","ison is created")

                                var urlCon = URL(networkConfig.host + networkConfig.tb + networkConfig.add)
                                Log.d("log","urlCon is created")
                                with(urlCon.openConnection() as HttpURLConnection){
                                    setRequestProperty("Content-type", "application/json");
                                    setRequestProperty("Accept", "application/json")
                                    requestMethod = "POST"
                                    outputStream.write(json.toByteArray())
                                    outputStream.flush()

                                    Log.d("log", "" + responseCode)
                                    Log.d("log", "" + responseMessage)
                                }
                            } catch (e: Exception) {
                                Log.d("log", "" + e.toString())
                                throw e
                            }
                        }

                    }

                    var thread = netThread()
                    try {
                        thread.start()
                    }catch (e : java.lang.Exception){
                        Toast.makeText(this@ManagerActivity, e.toString(), Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("취소"){ DialogInterface, i ->

                }.show()
        }
    }

    public fun addNewBookListener(){
        newBookButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.new_book_input_layout, null)
            val dialogBookName = dialogView.findViewById<EditText>(R.id.newBookName)
            val dialogBookAuthor = dialogView.findViewById<EditText>(R.id.newBookAuthor)
            val dialogBookFloor = dialogView.findViewById<EditText>(R.id.newBookFloor)
            val dialogBookTableSpinner = dialogView.findViewById<Spinner>(R.id.newBookTableSpinner)
            var tableList: ArrayList<String> = ArrayList()
            var tableName: String? = ""

            /**서버에서 table list를 json list로 받아와 파싱하는 Thread**/
            class getTableThread:Thread(){
                override fun run() {
                    try {
                        var url = URL(networkConfig.host + networkConfig.tb + networkConfig.getAll)
                        var json: String
                        with(url.openConnection() as HttpURLConnection) {
                            val jsonParser = JSONParser()
                            val jsonObjectList = jsonParser.parse(
                                InputStreamReader(inputStream)
                            ) as List<org.json.simple.JSONObject>
                            for (jsonObject in jsonObjectList){
                                tableList.add(jsonObject.get("name") as String)
                            }
                        }
                    }catch (e: java.lang.Exception){
                        Log.d("log", e.toString());
                        throw e
                    }
                }
            }
            /**새로운 책을 서버에 입력하는 Thread**/
            class addBookThread:Thread(){
                override fun run() {
                    try {
                        var jsonObject = JSONObject()
                        Log.d("log","books JSONObject is created")
                        jsonObject.accumulate("bookName", dialogBookName.text.toString())
                        jsonObject.accumulate("authorName", dialogBookAuthor.text.toString())
                        jsonObject.accumulate("tableName", tableName)
                        jsonObject.accumulate("floor", dialogBookFloor.text.toString())

                        var json = jsonObject.toString()
                        Log.d("log","books JSON is created")

                        var url = URL(networkConfig.host + networkConfig.books + networkConfig.add)
                        with(url.openConnection() as HttpURLConnection) {
                            setRequestProperty("Content-type", "application/json");
                            setRequestProperty("Accept", "application/json")
                            requestMethod = "POST"
                            outputStream.write(json.toByteArray())
                            outputStream.flush()

                            Log.d("log", "" + responseCode)
                            Log.d("log", "" + responseMessage)
                        }
                    }catch (e: java.lang.Exception){
                        Log.d("log", e.toString());
                        throw e
                    }
                }
            }

            /**Spinner에 Adapter와 Listener 설정**/

            var gettableThread = getTableThread()
            try {
                gettableThread.start()
                gettableThread.join()
                var arrayAdapter = ArrayAdapter<String>(this@ManagerActivity, R.layout.support_simple_spinner_dropdown_item, tableList)
                dialogBookTableSpinner.setAdapter(arrayAdapter)
                dialogBookTableSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        tableName = dialogBookTableSpinner.getItemAtPosition(position) as String
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }

            }catch (e: java.lang.Exception){
                Toast.makeText(this@ManagerActivity, e.toString(), Toast.LENGTH_LONG).show()
                Log.d("log", e.toString());
            }
            /****/


            builder.setView(dialogView)
                .setPositiveButton("확인") { DialogInterface, i ->
                    try {
                        var addbookThread = addBookThread()
                        addbookThread.start()
                    }catch (e : java.lang.Exception){
                        Log.d("log", e.toString());
                    }
                }
                .setNegativeButton("취소"){ DialogInterface, i ->

                }.show()
        }
    }
}