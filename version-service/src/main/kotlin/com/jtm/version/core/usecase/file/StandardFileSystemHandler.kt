package com.jtm.version.core.usecase.file

import com.jtm.version.core.domain.dto.FileDTO
import com.jtm.version.core.domain.exceptions.filesystem.FileNotFound
import com.jtm.version.core.domain.exceptions.filesystem.FilesNotFound
import com.jtm.version.core.domain.exceptions.filesystem.FolderNotFound
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

@Component
@Qualifier("standard")
class StandardFileSystemHandler: FileSystemHandler {

    private val logger = LoggerFactory.getLogger(StandardFileSystemHandler::class.java)

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
    override fun save(path: String, filePart: FilePart, name: String): Mono<File> {
        val folder = File(disk + path)
        if (!folder.parentFile.exists() && folder.parentFile.mkdirs()) logger.info("Created parent directories: $disk")
        if (!folder.exists() && folder.mkdirs()) logger.info("Created directories: $path")
        val file = File(disk + path, "$name.jar")
        return filePart.transferTo(file).thenReturn(file)
    }

    /**
     * Update the name of the file, from the path.
     *
     * @param path          the path to the file
     * @param name          the new name for the file
     * @return              the file with the new name
     * @see                 File
     */
    override fun updateFileName(path: String, name: String): Mono<File> {
        val source = Paths.get(disk + path)
        val res = Files.move(source, source.resolveSibling(name))
        return Mono.just(res.toFile())
    }

    /**
     * Return the file found at the path specified.
     *
     * @param path          the path to the file
     * @return              the file found
     * @see                 File
     * @throws FileNotFound if the file does not exist
     */
    override fun fetch(path: String): Mono<File> {
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
    override fun delete(path: String): Mono<File> {
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
    override fun listFiles(path: String): Flux<FileDTO> {
        val folder = File(disk + path)
        if (!folder.exists()) return Flux.error(FolderNotFound())
        val files = folder.listFiles() ?: return Flux.error(FilesNotFound())
        return Flux.fromArray(files).map { FileDTO(it.name, it.path, it.length(), OffsetDateTime.ofInstant(Instant.ofEpochMilli(it.lastModified()), ZoneId.systemDefault())) }
    }
}