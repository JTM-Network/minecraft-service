package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.model.FolderInfo
import com.jtm.minecraft.core.domain.model.ImageInfo
import com.jtm.minecraft.data.service.plugin.ImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/image")
class ImageController @Autowired constructor(private val imageService: ImageService) {

    @PostMapping("/upload/{id}", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(@PathVariable id: UUID, @RequestPart("file") image: FilePart): Mono<ImageInfo> {
        return imageService.uploadImage(id, image)
    }

    @GetMapping("/{id}/{name}")
    fun getImage(@PathVariable id: UUID, @PathVariable name: String): Mono<Resource> {
        return imageService.getImage(id, name)
    }

    @GetMapping("/all/{id}")
    fun getImages(@PathVariable id: UUID): Flux<FolderInfo> {
        return imageService.getImages(id)
    }

    @DeleteMapping("/{id}/{name}")
    fun deleteImage(@PathVariable id: UUID, @PathVariable name: String): Mono<String> {
        return imageService.deleteImage(id, name)
    }
}