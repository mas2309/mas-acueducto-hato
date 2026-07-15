package com.mas.co.service;

import java.io.File;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, String folder);

    String uploadFile(File file, String objectKey);

    void deleteFile(String fileUrl);
}
