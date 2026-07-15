package com.mas.co.service.impl;

import com.mas.co.service.StorageService;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Profile("dev")
public class LocalStorageService implements StorageService {

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        log.info("(Local) Simulando upload de '{}' a folder '{}'", file.getOriginalFilename(), folder);
        return "local://" + folder + "/" + file.getOriginalFilename();
    }

    @Override
    public String uploadFile(File file, String objectKey) {
        log.info("(Local) Simulando upload de File a key '{}'", objectKey);
        return "local://" + objectKey;
    }

    @Override
    public void deleteFile(String fileUrl) {
        log.info("(Local) Simulando eliminación de '{}'", fileUrl);
    }
}
