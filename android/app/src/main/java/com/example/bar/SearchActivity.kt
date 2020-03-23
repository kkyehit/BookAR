package com.example.bar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bar.AR.activity.ARActivity
import com.example.bar.adapter.BookListAdapter
import com.example.bar.adapter.BookListItem
import com.example.bar.config.NetworkConfig
import kotlinx.android.synthetic.main.search_activity.*
import org.json.simple.parser.JSONParser
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {
    var searchOpt: String? = ""
    var searchInput: String = ""
    val adapter: BookListAdapter = BookListAdapter()
    val networkConfig = NetworkConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)

        searchOpt = networkConfig.getListByName

        initSpinner()
        initButton()
        initListView()
    }

    public fun initListView(): Unit{
        bookListView.setOnItemClickListener {
                parent, view, position, id ->
            var book = adapter.getItem(position) as BookListItem
            Toast.makeText(this@SearchActivity, book.name+"을 선택하였습니다.", Toast.LENGTH_LONG).show();
            var intent = Intent(this, ARActivity::class.java)
            intent.putExtra("bookId", book.id)
            startActivity(intent)
        }
    }

    public fun initSpinner(){
        var list : ArrayList<String> = ArrayList()

        list.add("제목")
        list.add("작가")

        var adapter = ArrayAdapter(this@SearchActivity, R.layout.support_simple_spinner_dropdown_item, list)
        searchSpinner.adapter = adapter
        searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> searchOpt = networkConfig.getListByName
                    1 -> searchOpt = networkConfig.getListByAuthor
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    public fun initButton(){
        searchButton.setOnClickListener {
            searchInput = searchEditText.text.toString()
            var thread = searchThread()
            thread.start()
            thread.join()

            bookListView.adapter = adapter
        }
    }

    inner class searchThread:Thread() {
        override fun run() {
            try{
                Log.d("log", "search Url : "+ networkConfig.host + networkConfig.books + searchOpt + "/"+ searchInput);
                var url = URL(networkConfig.host + networkConfig.books + searchOpt + "/"+ searchInput)
                with(url.openConnection() as HttpURLConnection){
                    adapter.listClear()

                    val jsonParser = JSONParser()
                    val jsonObjectList = jsonParser.parse(
                        InputStreamReader(inputStream)
                    ) as List<org.json.simple.JSONObject>

                    for (jsonObject in jsonObjectList){
                        //Log.d("log", "0: ")
                        var id = jsonObject.get("id") as Long
                        //Log.d("log", "1: "+id)
                        var bookName = jsonObject.get("bookName") as String
                        //Log.d("log", "2: "+bookName)
                        var authorName =  jsonObject.get("authorName") as String
                       // Log.d("log", "3: "+authorName)
                        var tableName = jsonObject.get("tableName") as String
                        //Log.d("log", "4: "+tableName)
                        var state = jsonObject.get("state") as String

                        adapter.addItem(id, bookName, authorName, tableName, state)
                    }
                }

            }catch (e: Exception){
                Log.d("log", e.toString());
            }

        }
    }
}