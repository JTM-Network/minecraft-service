package com.jtm.plugin.data.service.image

import com.jtm.plugin.core.domain.model.ImageInfo
import com.jtm.plugin.core.usecase.file.ImageHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class ImageService @Autowired constructor(private val imageHandler: ImageHandler) {

    fun insertImage(filePart: FilePart): Mono<ImageInfo> {
        return imageHandler.save(filePart).map { ImageInfo("https://api.jtm-network.com/images/${it.name}") }
    }

    fun getImage(name: String): Mono<Resource> = imageHandler.fetch(name).map { FileSystemResource(it) }

    fun getImages(): Flux<String> = imageHandler.list().map { it.name }

    fun removeImage(name: String): Mono<Void> = imageHandler.delete(name).then()
}