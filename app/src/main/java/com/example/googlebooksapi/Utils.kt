package com.example.googlebooksapi

data class Book(
        val id:String? = null,
        val author: String? = null,
        val title: String? = null,
        val thumbnail: String? = null,
        val publisher: String? = null,
        val publisherDate: String? = null,
        val pageCount:Int? = null,
        val language:String?=null
)
