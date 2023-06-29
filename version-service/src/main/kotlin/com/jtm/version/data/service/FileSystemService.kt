package com.jtm.version.data.service

import com.jtm.version.core.domain.dto.FileDTO
import com.jtm.version.core.domain.exceptions.filesystem.FileNotFound
import com.jtm.version.core.domain.exceptions.filesystem.FolderNotFound
import com.jtm.version.core.domain.model.FileInfo
import com.jtm.version.core.domain.model.FolderInfo
import com.jtm.version.core.usecase.file.FileSystemHandler
import com.jtm.version.core.usecase.file.StandardFileSystemHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.util.*

@Service
class FileSystemService @Autowired constructor(@Qualifier("cloud") private val fileSystemHandler: FileSystemHandler) {

    /**
     * This will get the plugin folder with the name of the identifier.
     *
     * @param id        the identifier of the plugin folder
     * @return          the list of file versions in the plugin folder
     * @see             FileInfo
     */
    fun getVersions(id: UUID): Flux<FileInfo> {
        return fileSystemHandler.listFiles("/${id}")
            .map {
                val calc: Double = (it.size.toDouble() / (1024 * 1024).toDouble())
                val size = "%.2f".format(calc).toDouble()
                FileInfo(it.name, size, it.extension)
            }
    }

    /**
     * This will get the list of files inside the path.
     *
     * @param path      the file path
     * @return          the list of files in the folder
     * @see             FileInfo
     */
    fun getFiles(path: String): Flux<FileInfo> {
        return fileSystemHandler.listFiles(path)
            .filter(FileDTO::isFile)
            .map {
                val calc: Double = (it.size.toDouble() / (1024 * 1024).toDouble())
                val size = "%.2f".format(calc).toDouble()
                FileInfo(it.name, size, it.extension)
            }
    }

    /**
     * This will get the folders inside the path.
     *
     * @param path      the file path
     * @return          the list of folders inside the path
     * @see             FolderInfo
     */
    fun getFolders(path: String): Flux<FolderInfo> {
        return fileSystemHandler.listFiles(path)
            .filter(FileDTO::isDirectory)
            .map {
                val files = fileSystemHandler.listFiles(it.path) ?: Flux.empty()
                FolderInfo(it.name, files.count().block() ?: 0L)
            }
    }

    /**
     * This will delete the file found from the path.
     *
     * @param path      the file path
     * @return          the file removed
     * @see             FileInfo
     * @throws FileNotFound if the path is not a file.
     */
    fun removeFile(path: String): Mono<FileInfo> {
        return fileSystemHandler.fetch(path)
            .filter(File::isFile)
            .switchIfEmpty(Mono.defer { Mono.error(FileNotFound()) })
            .flatMap { fileSystemHandler.delete(path)
                .map {
                    val calc: Double = (it.length().toDouble() / (1024 * 1024).toDouble())
                    val size = "%.2f".format(calc).toDouble()
                    FileInfo(it.name, size, it.extension)
                }
            }
    }

    /**
     * This will delete the folder and all sub folders & files inside.
     *
     * @param path      the file path
     * @return          the folder removed
     * @see             FolderInfo
     * @throws FolderNotFound if the path is not a directory.
     */
    fun removeFolder(path: String): Mono<FolderInfo> {
        return fileSystemHandler.fetch(path)
            .filter(File::isDirectory)
            .switchIfEmpty(Mono.defer { Mono.error(FolderNotFound()) })
            .flatMap { fileSystemHandler.delete(path)
                .map {
                    val files = fileSystemHandler.listFiles(it.path) ?: Flux.empty()
                    FolderInfo(it.name, files.count().block() ?: 0L)
                }
            }
    }
}