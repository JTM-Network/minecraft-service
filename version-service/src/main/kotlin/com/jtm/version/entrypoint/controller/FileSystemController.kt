package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.model.FileInfo
import com.jtm.version.core.domain.model.FolderInfo
import com.jtm.version.data.service.FileSystemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/filesystem")
class FileSystemController @Autowired constructor(private val fileSystemService: FileSystemService) {

    @GetMapping("/{id}")
    fun getVersions(@PathVariable id: UUID): Flux<FileInfo> = fileSystemService.getVersions(id)

    @GetMapping("/files")
    fun getFiles(@RequestParam("path") path: String): Flux<FileInfo> = fileSystemService.getFiles(path)

    @GetMapping("/folders")
    fun getFolders(@RequestParam("path") path: String): Flux<FolderInfo> = fileSystemService.getFolders(path)

    @DeleteMapping("/file")
    fun deleteFile(@RequestParam("path") path: String): Mono<FileInfo> = fileSystemService.removeFile(path)

    @DeleteMapping("/folder")
    fun deleteFolder(@RequestParam("path") path: String): Mono<FolderInfo> = fileSystemService.removeFolder(path)
}