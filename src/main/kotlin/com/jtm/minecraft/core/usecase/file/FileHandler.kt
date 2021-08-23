package com.jtm.minecraft.core.usecase.file

import com.jtm.minecraft.core.domain.exceptions.file.FailedToDeleteDir
import com.jtm.minecraft.core.domain.exceptions.file.FileNotFound
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Component
class FileHandler {

    private val logger = LoggerFactory.getLogger(FileHandler::class.java)

    @Value("\${disk.path:/disk}")
    lateinit var disk: String

    fun save(path: String, filePart: FilePart, name: String): Mono<Void> {
        val folder = File(disk + path)
        if (!folder.exists()) if (folder.mkdirs()) logger.info("Created directories at: $path")
        val file = File(disk + path, name)
        return filePart.transferTo(file)
    }

    fun fetch(path: String): Mono<File> {
        val file = File(disk + path)
        if (!file.exists()) return Mono.error { FileNotFound() }
        return Mono.just(file)
    }

    fun delete(path: String): Mono<File> {
        val file = File(disk + path)
        if (!file.exists()) return Mono.error { FileNotFound() }
        return Mono.just(file)
            .flatMap {
                if (it.isDirectory) if (!Files.deleteIfExists(it.toPath())) return@flatMap Mono.error { FailedToDeleteDir() } else if (!it.delete()) return@flatMap Mono.error { FileNotFound() }
                Mono.just(it)
            }
    }

    fun listFiles(path: String): Flux<File> {
        val folder = File(disk + path)
        return Flux.fromArray(folder.listFiles())
    }
}