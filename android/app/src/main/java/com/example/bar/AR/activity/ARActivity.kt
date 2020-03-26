package com.example.bar.AR.activity

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bar.AR.Node.InfoNode
import com.example.bar.R
import com.google.ar.sceneform.AnchorNode
import com.example.bar.config.NetworkConfig
import com.google.ar.core.*
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import org.json.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.function.Consumer
import java.util.function.Function

class ARActivity : AppCompatActivity(){
    private val TAG = ARActivity::class.simpleName
    private val MIN_OPENGL_VERSION = 3.0

    private var arFragment: ArFragment? = null
    private var session : Session? = null

    private var id :Long = 0L
    private var idString: String = ""

    private var networkConfig = NetworkConfig()
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0
    private var tbX = 0.0
    private var tbY = 0.0
    private var tbZ = 0.0
    private var lm : LocationManager ?= null
    private var location : Location ?= null
    private var mAnchorNode : AnchorNode ?= null

    private var sm : SensorManager ? = null
    private var sl : sensorEventListener ?= null
    private var sensor : Sensor ? = null
    private var accelerometer : Sensor ? = null
    private var magnetometer : Sensor ? = null

    @RequiresApi(VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!checkIsSupportedDeviceOrFinish(this)){
            return
        }
        Toast.makeText(this@ARActivity, "땅을 클릭하면 그 곳을 기준으로 안내 표시가 생성됩니다.", Toast.LENGTH_SHORT). show()
        id = intent.extras!!.getLong("bookId")
        idString = id.toString()
        setContentView(R.layout.ar_activity)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        
        /**trackable을 터치하는 경우**/
        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->

        }
        
        /**방위각 구하기**/
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sm!!.getDefaultSensor(Sensor.TYPE_ORIENTATION) as Sensor
        accelerometer = sm!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sm!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sl = sensorEventListener(accelerometer,magnetometer)
        setLocation()

        /**session 설정후 할당 및 시작**/
        val session = Session(this@ARActivity)
        val config = Config(session)
        config.cloudAnchorMode = Config.CloudAnchorMode.ENABLED
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        session.configure(config);
        arFragment!!.arSceneView.setupSession(session)
        arFragment!!.arSceneView.session.resume()
        var node : InfoNode? = null
        
        /**화면에 고정 시키기**/
        arFragment!!.arSceneView.scene.addOnUpdateListener {
            if (arFragment!!.arSceneView.arFrame == null) {
                Log.d(TAG, "onUpdate: No frame available");
// No frame available
            }

            else if (arFragment!!.arSceneView.arFrame.camera.trackingState != TrackingState.TRACKING) {
                Log.d(TAG, "onUpdate: Tracking not started yet");
                // Tracking not started yet
            }
            else{
                val cameraPos = arFragment!!.arSceneView.scene.camera.worldPosition;
                val cameraForward = arFragment!!.arSceneView.scene.camera.forward;
                val position = Vector3.add(cameraPos, cameraForward.scaled(1.0f));

                // Create an ARCore Anchor at the position.
                val pose = Pose.makeTranslation(position.x, position.y, position.z);
                val anchor = arFragment!!.arSceneView.session.createAnchor(pose);
                if(mAnchorNode != null) {
                    mAnchorNode!!.anchor.detach()
                    mAnchorNode!!.anchor = anchor
                }else {
                    mAnchorNode = AnchorNode(anchor);
                    mAnchorNode!!.setParent(arFragment!!.arSceneView.scene);
                }
                if(node == null) node = InfoNode(this, idString)
                node!!.localRotation = Quaternion(x.toFloat(), z.toFloat(), y.toFloat(), 1.0F)
                node!!.setParent(mAnchorNode);

            }
        }
    }

    override fun onResume() {
        super.onResume()
        sm?.registerListener(sl, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sm?.registerListener(sl, magnetometer, SensorManager.SENSOR_DELAY_GAME)
        if(session == null){
            session = Session(this@ARActivity)
            var config = Config(session)
            config.cloudAnchorMode = Config.CloudAnchorMode.ENABLED
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            session!!.configure(config);
        }
        arFragment!!.arSceneView.setupSession(session)
    }

    fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later")
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        sm?.unregisterListener(sl, accelerometer)
        sm?.unregisterListener(sl, magnetometer)
    }
    
    /**Location Manager를 이용한 위치 계산**/
    @RequiresApi(VERSION_CODES.P)
    fun setLocation(){
        try {
            lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                return
            }
            location = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            var decide = decideXYZ()
            decide.start()
            decide.join()
        }catch (e : Exception){
            Log.d("log", e.toString())
        }

        /**서버로부터 위치 정보 받아와서 차이 계산**/
        val listener = object : LocationListener {
            override fun onLocationChanged(l: Location?) {
                location = l
                var decide = decideXYZ()
                decide.start()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        lm?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 1.0F, listener)
    }

    /**서버로부터 위치 정보 받아와서 차이 계산**/
    inner class decideXYZ: Thread() {
        override fun run() {
            try {
                var url = URL(networkConfig.host + networkConfig.books + networkConfig.getListById +"/"+ idString)
                var tableName =""
                var json = ""
                with(url.openConnection() as HttpURLConnection) {
                    var reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"));
                    var line = reader.readLine()
                    Log.d("log", "url : " + url);
                    while(line != null){
                        json += line
                        line = reader.readLine()
                    }
                    var jsonObject = JSONObject(json)
                    tableName = jsonObject.get("tableName") as String
                }
                url = URL(networkConfig.host + networkConfig.tb + networkConfig.getListByName +"/"+ tableName)
                tbX = 0.0
                tbY = 0.0
                tbZ = 0.0
                with(url.openConnection() as HttpURLConnection) {
                    val jsonParser = JSONParser()
                    val jsonObject = jsonParser.parse(
                        InputStreamReader(inputStream)
                    ) as org.json.simple.JSONObject
                    tbX = (jsonObject.get("x") as String).toDouble()
                    tbY = (jsonObject.get("y") as String).toDouble()
                    tbZ = (jsonObject.get("z") as String).toDouble()
                }
                x = ( ( tbX * 100000000 ).toLong() - (location?.longitude!! * 100000000).toLong() ).toDouble() / 1000
                y = ( ( tbY * 100000000 ).toLong() - (location?.latitude!! * 100000000).toLong() ).toDouble() / 1000
                z =  tbZ - location?.altitude!!
                Log.d("log", ""+x+"="+location?.longitude!!+ " - " +tbX+"("+location?.longitude+")");
                Log.d("log", ""+y+"="+location?.latitude!!+ " - " +tbY+"("+location?.latitude+")");
                Log.d("log", ""+z+"="+location?.altitude!!+ " - " +tbZ+"("+location?.altitude+")");
            }catch (e: java.lang.Exception){
                Log.d("log", e.toString());
            }
        }
    }

}
class sensorEventListener :  SensorEventListener {
    private var lastaccelerometer = FloatArray(3)
    private var lastmagnetometer  = FloatArray(3)
    private var lastorientation  = FloatArray(3)
    private var rotationMatrix  = FloatArray(9)
    private var lastaccelerometerset = false
    private var lastmagnetometerset = false
    private var accelerometer : Sensor ? = null
    private var magnetometer : Sensor ? = null
    private var azimute = 0.0F

    constructor(accelerometer : Sensor ? = null, magnetometer : Sensor ? = null){
        this.accelerometer = accelerometer
        this.magnetometer = magnetometer
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor == accelerometer){
            System.arraycopy(event.values, 0, lastaccelerometer, 0, event.values.size)
            lastaccelerometerset = true;
        }else if(event!!.sensor == magnetometer){
            System.arraycopy(event.values, 0, lastmagnetometer, 0, event.values.size)
            lastmagnetometerset = true;
        }
        if(lastaccelerometerset && lastmagnetometerset){
            SensorManager.getRotationMatrix(rotationMatrix, null, lastaccelerometer, lastmagnetometer)
            azimute =  ( ( Math.toDegrees(SensorManager.getOrientation(rotationMatrix, lastorientation)[0].toDouble()) + 360 ).toInt() % 360 ).toFloat()
            Log.d("log", "azimute : $azimute")
        }
    }
}

