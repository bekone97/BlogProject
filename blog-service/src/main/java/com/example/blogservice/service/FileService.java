package com.example.blogservice.service;

import com.example.blogservice.dto.LoadFile;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    ObjectId uploadFile(MultipartFile multipartFile);

    LoadFile downloadFile(ObjectId id);

    void deleteFile(ObjectId file);
}
