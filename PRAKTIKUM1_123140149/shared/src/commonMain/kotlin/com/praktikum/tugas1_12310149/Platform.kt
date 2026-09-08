package com.praktikum.tugas1_12310149

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform