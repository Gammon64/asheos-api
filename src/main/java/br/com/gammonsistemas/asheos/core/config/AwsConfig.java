package br.com.gammonsistemas.asheos.core.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Value("${aws.url}")
    private String awsUrl;

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    @Profile("docker") // Configuração para o ambiente de teste local docker
    S3Client s3ClientDocker() {
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));
        return S3Client.builder()
                .endpointOverride(URI.create(awsUrl))
                .region(Region.of(awsRegion))
                .credentialsProvider(credentialsProvider)
                .forcePathStyle(true) // Essencial para LocalStack/MinIO
                .build();
    }

    @Bean
    @Profile("!docker")
    S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}
