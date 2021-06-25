package com.jtm.minecraft.core.usecase

import com.jtm.minecraft.core.usecase.file.FileHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File

@RunWith(SpringRunner::class)
class FileHandlerTest {

    private val path = "test"
    private val handler = FileHandler()

    @Before fun setup() {
        val folder = File(path)
        folder.mkdirs()

        val file = File(path, "test.txt")
        file.createNewFile()
    }

    @After fun tearDown() {
        val file = File(path)
        file.deleteRecursively()
    }

    @Test fun saveTest() {
        val filePart = mock(FilePart::class.java)

        `when`(filePart.filename()).thenReturn("test")
        `when`(filePart.transferTo(any(File::class.java))).thenReturn(Mono.empty())

        val returned = handler.save(path, filePart)

        verify(filePart, times(1)).filename()
        verify(filePart, times(1)).transferTo(any(File::class.java))
        verifyNoMoreInteractions(filePart)

        StepVerifier.create(returned)
            .verifyComplete()
    }

    @Test fun fetchTest() {
        val returned = handler.fetch("$path/test.txt")

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test.txt")
            }
            .verifyComplete()
    }

    @Test fun deleteTest() {
        val returned = handler.delete("$path/test.txt")

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test.txt")
            }
            .verifyComplete()
    }

    @Test fun listFilesTest() {
        val returned = handler.listFiles(path)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test.txt")
            }
            .verifyComplete()
    }
}