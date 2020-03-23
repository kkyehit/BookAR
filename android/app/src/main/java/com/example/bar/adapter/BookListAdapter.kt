package com.example.bar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.books_listview_item.view.*
import java.util.ArrayList

class BookListAdapter: BaseAdapter() {
    private var bookList = ArrayList<BookListItem>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val context = parent?.context

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(com.example.bar.R.layout.books_listview_item, parent, false)
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val iconImageView = view!!.findViewById(com.example.bar.R.id.listviewBooksImage) as ImageView
        val nameTextView = view.findViewById(com.example.bar.R.id.booksListViewName) as TextView
        val authorTextView = view.findViewById(com.example.bar.R.id.booksListViewAuthor) as TextView
        val stateTextView = view.findViewById(com.example.bar.R.id.booksListViewState) as TextView

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val bookItem = bookList[position]

        // 아이템 내 각 위젯에 데이터 반영
        nameTextView.setText(bookItem.name)
        authorTextView.setText(bookItem.author)
        stateTextView.setText(bookItem.state)

        return view
    }

    override fun getItem(position: Int): Any {
        return bookList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return bookList.size
    }

    fun addItem(id: Long, name: String, author: String, table: String, state: String) {
        var bookItem =BookListItem();
        bookItem.name = name
        bookItem.author = author
        bookItem.stateNum = state
        bookItem.id = id
        bookItem.table = table

        when(state){
            "0" -> bookItem.state = "대출가능"
            else -> bookItem.state = "대출중"
        }
        bookList.add(bookItem)
    }

    fun listClear(){
        bookList.clear()
    }
}