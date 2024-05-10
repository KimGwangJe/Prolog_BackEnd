package com.prolog.prologbackend.Member.Service.Other;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3Client amazonS3Client;
    @Value("${S3Bucket}")
    private String BUCKET;


    public String getImageUrl(String fileName) {
        return amazonS3Client.getUrl(BUCKET, fileName).toString();
    }
    public void deleteProfileImage(String fileName){
        amazonS3Client.deleteObject(BUCKET,fileName);
    }
    public String createProfileImage(MultipartFile multipartFile) {
        verifyFileType(multipartFile.getContentType());

        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(multipartFile.getSize());

        try {
            amazonS3Client.putObject(BUCKET, fileName, multipartFile.getInputStream(), objectMetaData);
            return fileName;
        } catch (IOException e){
            throw new BusinessLogicException(MemberExceptionType.IMAGE_BAD_REQUEST);
        }
    }

    private void verifyFileType(String type) {
        String[] typeList = {"image/jpeg","image/jpg","image/png"};
        List<String> strList = new ArrayList<>(Arrays.asList(typeList));
        if(!strList.contains(type))
            throw new BusinessLogicException(MemberExceptionType.IMAGE_BAD_REQUEST);
    }

    private String createFileName(String originalFileName){
        String type = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().concat(type);
        return fileName;
    }
}
