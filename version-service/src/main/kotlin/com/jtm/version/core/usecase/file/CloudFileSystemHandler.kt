package com.jtm.version.core.usecase.file

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.jtm.version.core.domain.dto.FileDTO
import com.jtm.version.core.domain.exceptions.filesystem.FileNotFound
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Paths

@Component
@Qualifier("cloud")
class CloudFileSystemHandler(@Value("\${gcp.project-id}") var projectId: String, @Value("\${gcp.bucket-name}") var bucketName: String): FileSystemHandler {

    private val logger = LoggerFactory.getLogger(CloudFileSystemHandler::class.java)
    private val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
    private val path = "/storage"


    override fun save(path: String, filePart: FilePart, name: String): Mono<File> {
        val folder = File("${this.path}/temp")
        if (!folder.exists() && folder.mkdirs()) logger.info("Creating directories: ${folder.path}")
        val file = File("${this.path}/temp", filePart.filename())
        return filePart.transferTo(file)
            .thenReturn(file)
            .flatMap { f -> uploadFile(path, f)
                .then(deleteTemp(file.name))
            }
    }

    override fun updateFileName(path: String, name: String): Mono<File> {
        return Mono.empty()
    }

    override fun fetch(path: String): Mono<File> {
        val blob = storage.get(BlobId.of(bucketName, path))
        if (!blob.exists()) return Mono.error(FileNotFound())
        blob.downloadTo(Paths.get("${this.path}/temp/${blob.name}"))
        val file = File("${this.path}/temp/${blob.name}")
        return Mono.just(file)
    }

    override fun delete(path: String): Mono<File> {
        return fetch(path)
            .switchIfEmpty(Mono.error(FileNotFound()))
            .flatMap { f -> deleteFile(path, f.name).thenReturn(f) }
    }

    override fun listFiles(path: String): Flux<FileDTO> {
        val blobs = storage.list(bucketName, Storage.BlobListOption.prefix(path))
        return Flux.fromIterable(blobs.values).map {
            val name = it.name.substringAfterLast("/")
            val blob_path = it.name.replace(name, "")
            val extension = name.substringAfterLast(".")
            FileDTO(name, blob_path, it.size, it.updateTimeOffsetDateTime, extension, !it.isDirectory, it.isDirectory)
        }
    }

    private fun deleteTemp(name: String): Mono<File> {
        val file = File("${this.path}/temp", name)
        if (!file.exists()) {
            logger.info("File ${file.name} does not exist")
            return Mono.empty()
        }

        return Mono.just(file)
            .doOnNext { f -> f.delete() }
    }

    private fun deleteFile(path: String, name: String): Mono<Void> {
        val blob = storage.get(bucketName, "$path/$name")
        if (blob == null) {
            logger.info("Blob $name does not exist")
            // TODO: Add exception..
            return Mono.empty()
        }

        val precondition = Storage.BlobSourceOption.generationMatch(blob.generation)
        storage.delete(bucketName, name, precondition)
        return Mono.empty()
    }

    private fun uploadFile(path: String, file: File): Mono<File> {
        val blobId = BlobId.of(bucketName,  "$path/${file.name}");
        val blobInfo = BlobInfo.newBuilder(blobId).build();
        var precondition: Storage.BlobWriteOption? = null
        precondition = if (storage.get(bucketName, file.name) == null) {
            Storage.BlobWriteOption.doesNotExist()
        } else {
            Storage.BlobWriteOption.generationMatch(storage.get(bucketName, file.name).generation)
        }

        val result = storage.createFrom(blobInfo, Paths.get(file.path), precondition)
        logger.info("Uploaded file: ${result.name}, ${result.size}")
        return Mono.just(file)
    }
}