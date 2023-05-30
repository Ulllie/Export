package com.example.exportgradle.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.io.inputstream.ZipInputStream
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

@RestController
@RequestMapping("/export")
class ExportAndImportController {

    @GetMapping
    fun getFile(): ResponseEntity<ByteArray> {

        val user = User("Kolya", "MALE")

        check(user)

        val jsonMapper = ObjectMapper()

        val jsonByte: ByteArray = jsonMapper.writeValueAsBytes(user)

        val jsonFile = File.createTempFile("example", ".json")

        val fos = FileOutputStream(jsonFile)
        fos.write(jsonByte)
        fos.close()

        val zip = ZipFile("C:\\Users\\R2D2\\IdeaProjects\\ExportGradle\\src\\main\\resources\\name.zip")

        zip.addFile(jsonFile)

        jsonFile.delete()

        val zipBytesArray = Files.readAllBytes(zip.file.toPath())

        zip.file.delete()

        val httpHeaders = HttpHeaders()
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"hui.zip\"")

        return ResponseEntity(zipBytesArray, httpHeaders, HttpStatus.OK)
    }

    @PostMapping
    fun uploadFile(@RequestBody file: ByteArray): String? {

        val zos = ZipInputStream(ByteArrayInputStream(file))

        val zipEntry = zos.nextEntry

        val offsetJson = zipEntry.offsetStartOfData

        val sizeJson = zipEntry.uncompressedSize

        val jsonByte = ByteArray(sizeJson.toInt())

        zos.read(jsonByte, offsetJson.toInt(), sizeJson.toInt())

        zos.close()

        val jsonString = String(jsonByte)

        val objectMapper = jacksonObjectMapper()/*.registerModule(KotlinModule.Builder().build())*/

        val user = objectMapper.readValue<User>(jsonString)

        return user.sex

        /*val zipFile = ZipFile("zip4j.zip")

        zipFile.extractAll("/resources")*/

        /*val zipBytes = file.bytes

        val fos = FileOutputStream(File("zip4j.zip"))

        fos.write(zipBytes)
        fos.close()


        val zipFile = ZipFile()*/

    }

    fun <T> check(t: T) {
        val a = (t as Any).javaClass
    }

}