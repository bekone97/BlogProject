package com.example.blogproject.dto;

import org.springframework.web.multipart.MultipartFile;

public class AudioDto extends FileDto{
    public AudioDto(String title, MultipartFile multipartFile) {
        super(title, multipartFile,FileType.AUDIO);
    }
}
