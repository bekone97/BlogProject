package com.example.blogservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoadFile {
    private String fileName;
    private String fileType;
    private String fileSize;
    private byte[] file;
}
