package com.bookprism.bookprism.ui.home

data class BookModel (val name: String,
                      val author: String,
                      val rating: String,
                      val comment: String,
                      val cost: String,
                      val imgUrl:String){
    constructor() : this("","","","","","")
}