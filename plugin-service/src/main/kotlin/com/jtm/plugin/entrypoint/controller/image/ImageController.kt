package com.jtm.plugin.entrypoint.controller.image

import com.jtm.plugin.core.domain.model.ImageInfo
import com.jtm.plugin.data.service.image.ImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/images")
class ImageController @Autowired constructor(private val imageService: ImageService) {

    @PostMapping("/upload", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun postImage(@RequestPart("file") part: FilePart): Mono<ImageInfo> = imageService.insertImage(part)

    @GetMapping("/{name}")
    fun getImage(@PathVariable name: String): Mono<Resource> = imageService.getImage(name)

    @GetMapping("/all")
    fun getImages(): Flux<String> = imageService.getImages()

    @DeleteMapping("/{id}/{name}")
    fun deleteImage(@PathVariable name: String): Mono<Void> = imageService.removeImage(name)
}