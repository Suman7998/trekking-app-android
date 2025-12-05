package com.example.trekking.data

enum class Category { TREKKING, PARK, WATERFALL }

data class Place(
    val id: Int = 0,
    val name: String,
    val location: String,
    val category: Category
)
