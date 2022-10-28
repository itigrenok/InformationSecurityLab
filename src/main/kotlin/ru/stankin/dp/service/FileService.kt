package ru.stankin.dp.service

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.stankin.dp.enum.FileType

@Service
class FileService(
    private val cryptoService: CryptoService
) {

    companion object {
        private const val CIPHER_FILE_NAME = "users.json"
        private const val FILE_NAME = "_users.json"

        private val logger = LoggerFactory.getLogger(FileService::class.java)
    }

    fun createTriggerFile() {
        val file = File(FileService::class.java.classLoader.getResource(CIPHER_FILE_NAME)?.path?.replace(CIPHER_FILE_NAME, "") + "trigger.txt")
        file.createNewFile()
    }

    fun existTriggerFile(): Boolean {
        return FileService::class.java.classLoader.getResource("trigger.txt") != null
    }

    fun findByType(type: FileType): File? {
        return when(type) {
            FileType.DATA -> FileService::class.java.classLoader.getResource(FILE_NAME)?.let { File(it.toURI()) }
            FileType.CRYPTO_DATA -> FileService::class.java.classLoader.getResource(CIPHER_FILE_NAME)?.let { File(it.toURI()) }
        }
    }

    fun saveFile(source: ByteArray, file: File?) {
        if (file == null) {
            logger.warn("Невозможно сохранить file. File == null")
            return
        }

        FileOutputStream(file).use {
            it.write(source)
        }
    }

    fun openDataFile(): InputStream? {
        try {
            if (FileService::class.java.classLoader.getResourceAsStream(FILE_NAME) != null) {
                return FileService::class.java.classLoader.getResourceAsStream(FILE_NAME)
            }

            createSimpleFile()
            return FileService::class.java.classLoader.getResourceAsStream(FILE_NAME)
        } catch (e: Exception) {
            return null
        }
    }

    fun openFile(): File = File(FileService::class.java.classLoader.getResource(FILE_NAME)!!.toURI())

    private fun createSimpleFile() {
        val cipherFile = FileService::class.java.classLoader.getResource(CIPHER_FILE_NAME)
            ?: throw Exception("БД не найдена!")

        val simpleText = cryptoService.fileDecryption(cipherFile.readBytes())

        val file = File(cipherFile.path.replace(CIPHER_FILE_NAME, "") + FILE_NAME)
        file.writeText(simpleText)
        file.createNewFile()
    }
}