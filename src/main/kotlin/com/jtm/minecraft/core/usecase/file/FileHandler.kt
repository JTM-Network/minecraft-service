package com.jtm.minecraft.core.usecase.file

import com.jtm.minecraft.core.domain.exceptions.file.FileNotFound
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

@Component
class FileHandler {

    private val logger = LoggerFactory.getLogger(FileHandler::class.java)

    @Value("\${disk.path:/disk}")
    lateinit var disk: String

    /**
     * Save a file to disk using the {@link FilePart}
     *
     * @param path - the path to save the file to
     * @param filePart - the file to save
     * @param name - the name for the file
     * @return empty publisher
     */
    fun save(path: String, filePart: FilePart, name: String): Mono<Void> {
        val folder = File(disk + path)
        if (!folder.exists() && folder.mkdirs()) logger.info("Created directories at: $path")
        val file = File(disk + path, name)
        return filePart.transferTo(file)
    }

    /**
     * Return the file found at the path specified.
     *
     * @param path - the path to the file
     * @throws FileNotFound - if the file does not exist
     * @return the file found.
     */
    fun fetch(path: String): Mono<File> {
        val file = File(disk + path)
        if (!file.exists()) return Mono.error { FileNotFound() }
        return Mono.just(file)
    }

    /**
     * Delete file at the path specified.
     *
     * @param path - the path to the file
     * @throws FileNotFound - if the file/folder does not exist
     * @return the deleted file/folder
     */
    fun delete(path: String): Mono<File> {
        val file = File(disk + path)
        if (!file.exists()) return Mono.error { FileNotFound() }
        return Mono.just(file)
            .flatMap {
                if (it.isDirectory) {
                    FileUtils.deleteDirectory(it)
                } else {
                    if (!it.delete()) return@flatMap Mono.error { FileNotFound() }
                }
                Mono.just(it)
            }
    }

    /**
     * Return the list of files/folders from the path
     *
     * @param path - the path
     * @return the list of files & folders
     */
    fun listFiles(path: String): Flux<File> {
        val folder = File(disk + path)
        return Flux.fromArray(folder.listFiles())
    }
}