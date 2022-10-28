package ru.stankin.dp.ext

import java.io.ByteArrayOutputStream
import java.io.InputStream

fun InputStream.toOutputStream(): ByteArrayOutputStream {
    val stream = ByteArrayOutputStream()
    this.transferTo(stream)

    return stream
}