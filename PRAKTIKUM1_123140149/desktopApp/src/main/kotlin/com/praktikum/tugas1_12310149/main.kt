package com.praktikum.tugas1_12310149

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PRAKTIKUM1_123140149",
    ) {
        App()
    }
}