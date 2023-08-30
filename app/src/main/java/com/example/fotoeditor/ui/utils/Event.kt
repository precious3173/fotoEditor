package com.example.fotoeditor.ui.utils

interface Event
interface EventHandler {
    fun onEvent(event: Event)
}