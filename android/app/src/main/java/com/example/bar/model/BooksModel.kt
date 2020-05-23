package com.example.bar.model

class BooksModel(
    id: Long,
    bookName : String ,
    authorName: String,
    tableName: String,
    state: String,
    floor: String
) {
    var id: Long? = id
    var bookName: String? = bookName
    var authorName: String? = authorName
    var tableName: String? = tableName
    var state: String? = state
    var f: String? = floor
}