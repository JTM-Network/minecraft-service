package com.jtm.plugin.core.usecase.file

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.util.*

interface ImageHandler {

    fun save(filePart: FilePart): Mono<File>

    fun fetch(name: String): Mono<File>

    fun list(): Flux<File>

    fun delete(name: String): Mono<File>
}