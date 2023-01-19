package com.example.blogproject.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class VideoDto extends FileDto{
    public VideoDto(String title, MultipartFile multipartFile) {
        super(title, multipartFile, FileType.VIDEO);
    }
}
