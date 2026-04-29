package com.moassam.shared.adapter.filestorage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    String upload(MultipartFile file, String directory);

    void delete(String fileUrl);
}
