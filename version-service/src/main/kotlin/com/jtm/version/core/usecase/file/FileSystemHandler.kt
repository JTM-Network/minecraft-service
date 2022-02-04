package com.jtm.version.core.usecase.file

import com.jtm.version.core.domain.exceptions.FileNotFound
import com.jtm.version.core.domain.exceptions.FilesNotFound
import com.jtm.version.core.domain.exceptions.FolderNotFound
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

@Component
class FileSystemHandler {

    private val logger = LoggerFactory.getLogger(FileSystemHandler::class.java)

    @Value("\${storage.disk:/disk}")
    lateinit var disk: String

    /**
     * Save a file to disk using the {@link FilePart}
     *
     * @param path          the path to save the file to
     * @param filePart      the file to save
     * @param name          the name for the file
     * @return              the file saved
     * @see                 File
     */
    fun save(path: String, filePart: FilePart, name: String): Mono<File> {
        val folder = File(disk + path)
        if (!folder.exists() && folder.mkdirs()) logger.info("Created directories: $path")
        val file = File(disk + path, name)
        return filePart.transferTo(file).thenReturn(file)
    }

    /**
     * Return the file found at the path specified.
     *
     * @param path          the path to the file
     * @return              the file found
     * @see                 File
     * @throws FileNotFound if the file does not exist
     */
    fun fetch(path: String): Mono<File> {
        val file = File(disk + path)
        if (!file.exists()) return Mono.error { FileNotFound() }
        return Mono.just(file)
    }

    /**
     * Delete file at the path specified.
     *
     * @param path          the path to the file
     * @return              the deleted file/folder
     * @see                 File
     * @throws FileNotFound if the file/folder does not exist or the deletion of the file fails.
     */
    fun delete(path: String): Mono<File> {
        val file = File(disk + path)
        if (!file.exists()) return Mono.error { FileNotFound() }
        return Mono.just(file)
            .flatMap {
                if (it.isDirectory) FileUtils.deleteDirectory(it) else if (!it.delete()) return@flatMap Mono.error { FileNotFound() }
                Mono.just(it)
            }
    }

    /**
     * Return the list of files/folders from the path
     *
     * @param path          the path
     * @return              the list of files & folders
     * @see                 File
     * @throws FolderNotFound if the folder does not exist
     * @throws FilesNotFound if listing the files returns null
     */
    fun listFiles(path: String): Flux<File> {
        val folder = File(disk + path)
        if (!folder.exists()) return Flux.error(FolderNotFound())
        val files = folder.listFiles() ?: return Flux.error(FilesNotFound())
        return Flux.fromArray(files)
    }
}