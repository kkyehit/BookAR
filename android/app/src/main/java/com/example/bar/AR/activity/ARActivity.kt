package com.example.bar.AR.activity

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bar.R
import com.google.ar.core.Config
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.example.bar.config.NetworkConfig
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

class ARActivity : AppCompatActivity() {
    private val TAG = ARActivity::class.simpleName
    private val MIN_OPENGL_VERSION = 3.0

    private var arFragment: ArFragment? = null
    private var andyRenderable : ModelRenderable? = null
    private var session : Session? = null

    private var directionRenderable: ModelRenderable? = null
    private var infoRenderable: ViewRenderable? = null
    private var flags= 0

    private var id :Long = 0L
    private var idString: String = ""

    private var networkConfig = NetworkConfig()
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0
    private var lm : LocationManager ?= null
    private var location : Location ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!checkIsSupportedDeviceOrFinish(this)){
            return
        }
        Toast.makeText(this@ARActivity, "땅을 클릭하면 그 곳을 기준으로 안내 표시가 생성됩니다.", Toast.LENGTH_SHORT). show()
        id = intent.getExtras().getLong("bookId")
        idString = id.toString()
        setContentView(R.layout.ar_activity)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        ModelRenderable.builder()
            .setSource(this, R.raw.andy)
            .build()
            .thenAccept(Consumer { renderable: ModelRenderable -> andyRenderable = renderable
            })
            .exceptionally(
                Function<Throwable, Void?> { throwable: Throwable? ->
                    val toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }
            )

        ViewRenderable.builder()
            .setView(this@ARActivity, R.layout.ar_cardview)
            .build()
            .thenAccept(
                Consumer { renderable: ViewRenderable ->
                    infoRenderable = renderable
                    val textView = renderable.view as TextView
                    textView.setText("book id : "+ idString)
                }
            )
            .exceptionally(
                Function<Throwable, Void> { throwable: Throwable? ->
                    val toast = Toast.makeText(this, "Unable to load view renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    throw AssertionError("Could not load plane card view.", throwable)
                }
            )

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            if (andyRenderable == null) {
                return@setOnTapArPlaneListener
            }
            if(flags != 1) {
                setLocation()
                Log.d("log", "x : "+x.toString()+" y : "+y.toString()+" z : "+z.toString())
                // Create the Anchor.
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment!!.arSceneView.scene)
                // Create the transformable andy and add it to the anchor.
                // Create the transformable andy and add it to the anchor.
                val andy = TransformableNode(arFragment!!.transformationSystem)
                val pose = anchor.pose
                andy.worldPosition = Vector3(x.toFloat(), z.toFloat(), y.toFloat())
                andy.setParent(anchorNode)
                andy.renderable = andyRenderable
                andy.select()

                val infoCard = TransformableNode(arFragment!!.transformationSystem)
                infoCard.setParent(anchorNode)
                infoCard.renderable = infoRenderable
                infoCard.isEnabled = true
                infoCard.localPosition = Vector3(x.toFloat(), z.toFloat() + 0.5F, y.toFloat())

                flags += 1;
            }
         }
    }

    override fun onResume() {
        super.onResume()
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

    fun setLocation(){
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
        location = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        var decide = decideXYZ()
        decide.start()
        decide.join()
    }

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
                var tbX = 0.0
                var tbY = 0.0
                var tbZ = 0.0
                with(url.openConnection() as HttpURLConnection) {
                    val jsonParser = JSONParser()
                    val jsonObject = jsonParser.parse(
                        InputStreamReader(inputStream)
                    ) as org.json.simple.JSONObject
                    tbX = (jsonObject.get("x") as String).toDouble()
                    tbY = (jsonObject.get("y") as String).toDouble()
                    tbZ = (jsonObject.get("z") as String).toDouble()
                }
                x = location?.longitude!! - tbX
                y = location?.latitude!! - tbY
                z = location?.altitude!! - tbZ
                Log.d("log", ""+x+"="+location?.longitude?.toFloat()!!+ " - " +tbX+"("+location?.longitude+")");
                Log.d("log", ""+y+"="+location?.latitude?.toFloat()!!+ " - " +tbY+"("+location?.latitude+")");
                Log.d("log", ""+z+"="+location?.altitude?.toFloat()!!+ " - " +tbZ+"("+location?.altitude+")");
            }catch (e: java.lang.Exception){
                Log.d("log", e.toString());
            }
        }
    }
}