package com.jtm.plugin.core.usecase.file.impl

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.jtm.plugin.core.domain.exception.image.DirectoryNotFound
import com.jtm.plugin.core.domain.exception.image.FailedImageDeletion
import com.jtm.plugin.core.domain.exception.image.ImageNotFound
import com.jtm.plugin.core.usecase.file.ImageHandlerImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Paths

@Qualifier("cloud")
@Component
class CloudStorageImageHandler(path: String = "/storage",
                               @Value("\${gcp.project-id}") var projectId: String,
                               @Value("\${gcp.bucket-name}") var bucketName: String): ImageHandlerImpl(path) {

    private val logger = LoggerFactory.getLogger(CloudStorageImageHandler::class.java)

    override fun save(filePart: FilePart): Mono<File> {
        val folder = File("$path/temp")
        if (!folder.exists() && folder.mkdirs()) logger.info("Creating directories: ${folder.path}")
        val file = File("$path/temp", filePart.filename())
        return filePart.transferTo(file).thenReturn(file)
            .flatMap { f -> uploadFile(f)
                .then(deleteTemp(file.name))
            }
    }

    override fun fetch(name: String): Mono<File> {
        val file = File("$path/images", name)
        if (!file.exists()) {
            val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
            val blob = storage.get(BlobId.of(bucketName, name))
            if (!blob.exists()) return Mono.error(ImageNotFound())
            blob.downloadTo(Paths.get("$path/images/$name"))
        }
        return Mono.just(file)
    }

    override fun list(): Flux<String> {
        val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
        val blobs = storage.list(bucketName)
        return Flux.fromIterable(blobs.values).map { it.name }
    }

    override fun delete(name: String): Mono<File> {
        val file = File("$path/images", name)
        if (!file.delete()) return Mono.error(FailedImageDeletion())
        return Mono.just(file)
            .then(deleteCloud(name)
                .thenReturn(file))
    }

    private fun deleteTemp(name: String): Mono<File> {
        val file = File("$path/temp", name)
        if (!file.exists()) return Mono.error(ImageNotFound())
        if (!file.delete()) return Mono.error(FailedImageDeletion())
        return Mono.just(file)
    }

    private fun deleteCloud(name: String): Mono<Void> {
        val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
        val blob = storage.get(bucketName, name)
        if (blob == null) {
            logger.error("Blob $name does not exist")
            return Mono.error(ImageNotFound())
        }

        val precondition = Storage.BlobSourceOption.generationMatch(blob.generation)
        storage.delete(bucketName, name, precondition)
        return Mono.empty()
    }

    private fun uploadFile(file: File): Mono<File> {
        val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
        val blobId = BlobId.of(bucketName, file.name)
        val blobInfo = BlobInfo.newBuilder(blobId).build()

        var precondition: Storage.BlobWriteOption? = null
        if (storage.get(bucketName, file.name) == null) {
            precondition = Storage.BlobWriteOption.doesNotExist()
        } else {
            precondition = Storage.BlobWriteOption.generationMatch(storage.get(bucketName, file.name).generation)
        }
        val result = storage.createFrom(blobInfo, Paths.get(file.path), precondition)
        logger.info("Uploaded file: ${result.name}, ${result.size}")
        return Mono.just(file);
    }
}