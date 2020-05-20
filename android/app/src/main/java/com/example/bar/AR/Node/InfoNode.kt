package com.example.bar.AR.Node

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.example.bar.R
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import java.util.function.Consumer
import java.util.function.Function

class InfoNode() : Node() {
    private var infoCard : Node ?= null
    //private var andy : Node ?= null
    //private var andyRenderable : ModelRenderable? = null
    private var directrion : Node ?= null
    //private var directionRenderable : ModelRenderable? = null
    private var context : Context ?= null
    private var bookId : String ?= null
    constructor(context: Context, bookId : String) : this(){
        this.context = context
        this.bookId = bookId
    }

    override fun onActivate() {
        if (infoCard == null) {
            infoCard = Node()
            infoCard!!.setParent(this)
            infoCard!!.isEnabled = true
            infoCard!!.localPosition = Vector3(0.0f, 0.5f, 0.0f)
            ViewRenderable.builder()
                .setView(context, com.example.bar.R.layout.ar_cardview)
                .build()
                .thenAccept(
                    Consumer { renderable: ViewRenderable ->
                        infoCard!!.renderable = renderable
                        val textView = renderable.view as TextView
                        textView.text = "book id : " + bookId + "입니다."
                    }
                )
                .exceptionally(
                    Function<Throwable, Void> { throwable: Throwable? ->
                        throw AssertionError(
                            "Could not load plane card view.",
                            throwable
                        )
                    }
                )
        }
        /*if (andy == null) {
            andy = Node()
            andy!!.setParent(this)
            ModelRenderable.builder()
                .setSource(context, R.raw.andy)
                .build()
                .thenAccept(Consumer { renderable: ModelRenderable ->

                    andy!!.renderable= renderable
                })
                .exceptionally(
                    Function<Throwable, Void?> { throwable: Throwable? ->
                        val toast = Toast.makeText(
                            context,
                            "Unable to load andy renderable",
                            Toast.LENGTH_LONG
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        null
                    }
                )
            andy!!.localPosition = Vector3(0.0f, 0.0f, 0.0f)
        }*/

        if (directrion == null) {
            directrion = Node()
            directrion!!.setParent(this)
            ModelRenderable.builder()
                .setSource(context, R.raw.direction)
                .build()
                .thenAccept(Consumer { renderable: ModelRenderable ->

                    directrion!!.renderable= renderable
                })
                .exceptionally(
                    Function<Throwable, Void?> { throwable: Throwable? ->
                        val toast = Toast.makeText(
                            context,
                            "Unable to load direction renderable",
                            Toast.LENGTH_LONG
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        null
                    }
                )
            directrion!!.localPosition = Vector3(0.0f, 0.0f, 0.0f)
        }
    }
}