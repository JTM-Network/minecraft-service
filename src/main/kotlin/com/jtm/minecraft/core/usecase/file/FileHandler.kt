package com.jtm.minecraft.core.usecase.file

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
@Component
class FileHandler {

    val logger = LoggerFactory.getLogger(FileHandler::class.java)

    @Value("\${disk.path:path}")
    lateinit var disk: String

    fun save(path: String, filePart: FilePart): Mono<Void> {
        val folder = File(path)
        if (!folder.exists()) if (folder.mkdirs()) logger.info("Created directories at: $path")
        val file = File(path, filePart.filename())
        return filePart.transferTo(file)
    }

    fun fetch(path: String): Mono<File> {
        val file = File(path)
        return Mono.just(file)
    }

    fun delete(path: String): Mono<File> {
        val file = File(path)
        return Mono.just(file)
            .map {
                it.delete()
                it
            }
    }

    fun listFiles(path: String): Flux<File> {
        val folder = File(path)
        return Flux.fromArray(folder.listFiles())
    }
}