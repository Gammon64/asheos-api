package br.com.gammonsistemas.asheos.core.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Faz upload de um arquivo para o storage.
     * 
     * @param file      arquivo recebido via formulário
     * @param objectKey o caminho/nome único do arquivo no bucket
     * @return URL pública do objeto
     */
    String uploadFile(MultipartFile file, String objectKey);

    /**
     * Faz o download de um arquivo do storage.
     * @param objectKey o caminho/nome único do arquivo no bucket
     * @return transmissão de bytes do objeto
     */
    byte[] downloadFile(String objectKey);

    /**
     * Apaga um arquivo do storage.
     * @param objectKey o caminho/nome único do arquivo no bucket
     */
    void deleteFile(String objectKey);
}
