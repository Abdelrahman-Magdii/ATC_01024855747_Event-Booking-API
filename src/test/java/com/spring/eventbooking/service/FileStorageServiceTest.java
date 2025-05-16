package com.spring.eventbooking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    private final String testUploadDir = "test-uploads";
    @Mock
    private MultipartFile mockFile;
    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", testUploadDir);
        cleanUpTestDirectory();
    }

    @AfterEach
    void tearDown() throws IOException {
        cleanUpTestDirectory();
    }

    private void cleanUpTestDirectory() throws IOException {
        Path uploadPath = Paths.get(testUploadDir);
        if (Files.exists(uploadPath)) {
            // Delete all files in the directory first
            try (Stream<Path> paths = Files.walk(uploadPath)) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
            // Then delete the directory
            Files.deleteIfExists(uploadPath);
        }
    }

    @Test
    void constructor_ShouldCreateUploadsDirectory() throws IOException {
        // Arrange
        Path expectedPath = Paths.get("uploads").toAbsolutePath().normalize();
        cleanUpDirectory(expectedPath); // Clean up if exists from previous tests

        try {
            // Act
            new FileStorageService();

            // Assert
            assertTrue(Files.exists(expectedPath));
        } finally {
            // Clean up
            cleanUpDirectory(expectedPath);
        }
    }

    private void cleanUpDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            // Delete all files in the directory first
            try (Stream<Path> paths = Files.walk(path)) {
                paths.filter(Files::isRegularFile)
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
            // Then delete the directory
            Files.deleteIfExists(path);
        }
    }

    @Test
    void saveFile_ShouldCreateDirectoryIfNotExists() throws IOException {
        String originalFilename = "test.txt";
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("test content".getBytes()));

        fileStorageService.saveFile(mockFile);

        assertTrue(Files.exists(Paths.get(testUploadDir)));
    }

    @Test
    void saveFile_ShouldGenerateUniqueFilename() throws IOException {
        String originalFilename = "test.txt";
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("test content".getBytes()));

        String result1 = fileStorageService.saveFile(mockFile);
        String result2 = fileStorageService.saveFile(mockFile);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1, result2);
        assertTrue(result1.contains(originalFilename));
        assertTrue(result2.contains(originalFilename));
    }

    @Test
    void saveFile_ShouldHandleIOException() throws IOException {
        String originalFilename = "test.txt";
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO Exception"));

        assertThrows(IOException.class, () -> fileStorageService.saveFile(mockFile));
    }

    @Test
    void deleteFile_ShouldDeleteExistingFile() throws IOException {
        String testContent = "test content";
        String fileName = "test-delete.txt";
        Path filePath = Paths.get(testUploadDir).resolve(fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, testContent.getBytes());

        fileStorageService.deleteFile(fileName);

        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_ShouldNotThrowWhenFileNotExists() {
        String nonExistentFile = "non-existent.txt";

        assertDoesNotThrow(() -> fileStorageService.deleteFile(nonExistentFile));
    }

    @Test
    void loadFile_ShouldReturnFileContent() throws IOException {
        String testContent = "test content";
        String fileName = "test-load.txt";
        Path filePath = Paths.get(testUploadDir).resolve(fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, testContent.getBytes());

        byte[] result = fileStorageService.loadFile(fileName);

        assertNotNull(result);
        assertEquals(testContent, new String(result));
    }

    @Test
    void loadFile_ShouldReturnNullWhenFileNotExists() throws IOException {
        String nonExistentFile = "non-existent.txt";

        byte[] result = fileStorageService.loadFile(nonExistentFile);

        assertNull(result);
    }

}