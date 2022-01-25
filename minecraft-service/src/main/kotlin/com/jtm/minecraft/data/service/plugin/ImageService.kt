package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.model.FolderInfo
import com.jtm.minecraft.core.domain.model.ImageInfo
import com.jtm.minecraft.core.usecase.file.FileHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ImageService @Autowired constructor(private val fileHandler: FileHandler) {

    /**
     * Save the uploaded image under a folder pertaining to the id.
     *
     * @param id - the plugin id folder
     * @param filePart - the file to save
     * @return the url of the image.
     */
    fun uploadImage(id: UUID, filePart: FilePart): Mono<ImageInfo> {
        return fileHandler.save("/images/${id}", filePart, filePart.filename())
            .thenReturn(ImageInfo("https://api.jtm-network.com/mc/image/${id}/\${filePart.filename()"))
    }

    /**
     * Return an image saved on disk
     *
     * @param id - the plugin id folder
     * @param name - the file name
     * @return the image resource
     */
    fun getImage(id: UUID, name: String): Mono<Resource> {
        return fileHandler.fetch("/images/${id}/${name}")
            .map { FileSystemResource(it) }
    }

    /**
     * Return the list of images found under plugin id folder
     *
     * @param id - the plugin identifier
     * @return the file names found
     */
    fun getImages(id: UUID): Flux<FolderInfo> {
        return fileHandler.listFiles("/images/${id}")
            .map { FolderInfo(it.name) }
    }

    /**
     * Delete the image found.
     *
     * @param id - the plugin identifier
     * @param name - the file name
     * @return the file name
     */
    fun deleteImage(id: UUID, name: String): Mono<String> {
        return fileHandler.delete("/images/${id}/${name}")
            .map { it.name }
    }
}