package com.example.blogservice.service.impl;

import com.example.blogservice.dto.LoadFile;
import com.example.blogservice.service.FileService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    @SneakyThrows
    @Override
    @Transactional
    public ObjectId uploadFile(MultipartFile file) {
        log.debug("Upload file with file name : {} , contentType: {}, size : {}",
                file.getOriginalFilename(),file.getContentType(),file.getSize());
        DBObject data = new BasicDBObject();
        data.put("filesize",file.getSize());

        ObjectId store = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(),
                file.getContentType(), data);
        return store;

    }

    @Override
    @SneakyThrows
    public LoadFile downloadFile(ObjectId id) {
        log.debug("Download file with objectId : {}",id);
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        return LoadFile.builder()
                .fileName(gridFSFile.getFilename())
                .fileSize(gridFSFile.getMetadata().get("filesize").toString())
                .fileType(gridFSFile.getMetadata().get("_contentType").toString())
                .file(IOUtils.toByteArray(gridFsOperations.getResource(gridFSFile).getInputStream()))
                .build();
    }

    @Override
    @Transactional
    public void deleteFile(ObjectId objectId) {
        log.debug("Delete file with object id : {}",objectId);
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(objectId)));
    }
}
