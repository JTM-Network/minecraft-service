package com.jtm.version.core.usecase.file

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

interface FileSystemHandler {

    fun save(path: String, filePart: FilePart, name: String): Mono<File>

    fun updateFileName(path: String, name: String): Mono<File>

    fun fetch(path: String): Mono<File>

    fun delete(path: String): Mono<File>

    fun listFiles(path: String): Flux<File>
}