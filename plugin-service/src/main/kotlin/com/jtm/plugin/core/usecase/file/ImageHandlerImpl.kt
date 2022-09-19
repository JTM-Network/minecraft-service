package com.jtm.plugin.core.usecase.file

import com.jtm.plugin.core.domain.exception.image.DirectoryNotFound
import com.jtm.plugin.core.domain.exception.image.FailedImageDeletion
import com.jtm.plugin.core.domain.exception.image.ImageNotFound
import org.slf4j.LoggerFactory
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.util.*

abstract class ImageHandlerImpl(var path: String): ImageHandler {

    private val logger = LoggerFactory.getLogger(ImageHandlerImpl::class.java)

    init {
        val folder = File(path)
        if (!folder.exists() && folder.mkdirs()) logger.info("Created directories: $path")
    }

    override fun save(filePart: FilePart): Mono<File> {
        val folder = File(path)
        if (!folder.exists() && folder.mkdirs()) logger.info("Creating directories: ${folder.path}")
        val file = File(path, filePart.filename())
        return filePart.transferTo(file).thenReturn(file)
    }

    override fun fetch(name: String): Mono<File> {
        val file = File(path, name)
        if (!file.exists()) return Mono.error(ImageNotFound())
        return Mono.just(file)
    }

    override fun list(): Flux<File> {
        val folder = File(path)
        val files = folder.listFiles() ?: return Flux.error(DirectoryNotFound())
        return Flux.fromArray(files)
    }

    override fun delete(name: String): Mono<File> {
        val file = File(path, name)
        if (!file.exists()) return Mono.error(ImageNotFound())
        if (!file.delete()) return Mono.error(FailedImageDeletion())
        return Mono.just(file)
    }
}