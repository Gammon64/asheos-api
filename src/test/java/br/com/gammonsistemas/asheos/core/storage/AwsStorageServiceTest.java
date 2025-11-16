package br.com.gammonsistemas.asheos.core.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
public class AwsStorageServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private AwsStorageService storageService;

    @BeforeEach
    void setUp() {
        // Injetando o nome do bucket manualmente para o teste
        org.springframework.test.util.ReflectionTestUtils.setField(
                storageService, "bucketName", "test-bucket");
    }

    @Test
    @DisplayName("Deve fazer o upload de um arquivo para o Min.io")
    void testUploadFile() throws IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "Test file content".getBytes());
        String objectKey = "path/to/test.jpg";

        // When
        String resultKey = storageService.uploadFile(file, objectKey);

        // Then
        assertEquals(resultKey, objectKey);

        // Verifica se o s3Client.putObject foi chamado
        verify(s3Client).putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class));
    }

    @Test
    @DisplayName("Deve excluir um arquivo do Min.io")
    void testDeleteFile() {
        // Given
        String objectKey = "path/to/test.jpg";

        // When
        storageService.deleteFile(objectKey);

        // Then
        // Verifica se o s3Client.deleteObject foi chamado
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}
