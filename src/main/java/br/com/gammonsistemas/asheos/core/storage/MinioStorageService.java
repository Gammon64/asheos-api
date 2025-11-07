package br.com.gammonsistemas.asheos.core.storage;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String objectKey) {
        try {
            // 1. Cria a requisição de upload
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey) // O caminho/nome do arquivo no bucket
                    .contentType(file.getContentType())
                    .build();

            // 2. Prepara o corpo da requisição (os bytes do arquivo)
            RequestBody requestBody = RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()
            );

            // 3. Envia o objeto para o Min.io (S3)
            s3Client.putObject(putRequest, requestBody);

            // 4. Retorna a chave do objeto (o caminho) para salvar no banco
            return objectKey;

        } catch (IOException | S3Exception e) {
            // (Em produção, trate exceções de forma mais granular)
            throw new RuntimeException("Falha ao fazer upload do arquivo para o Min.io: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadFile(String objectKey) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getRequest)) {
            // Lê todos os bytes do arquivo
            return response.readAllBytes();
        } catch (IOException | S3Exception e) {
            throw new RuntimeException("Falha ao baixar o arquivo: " + objectKey, e);
        }
    }

    @Override
    public void deleteFile(String objectKey) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        try {
            s3Client.deleteObject(deleteRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Falha ao deletar o arquivo: " + objectKey, e);
        }
    }

}
