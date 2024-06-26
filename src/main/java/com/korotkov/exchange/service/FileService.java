package com.korotkov.exchange.service;


import com.korotkov.exchange.util.ImageMetaData;
import com.korotkov.exchange.util.S3File;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FileService {

    private static final String BUCKET_NAME = "syantrywawe-exchange";
    private S3Client s3Client;

    public void uploadFile(MultipartFile file, String key) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public InputStreamResource getFile(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return new InputStreamResource(responseBytes.asInputStream());
    }

    public S3File getImageFromS3(String key)  {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);

        GetObjectResponse response = responseInputStream.response();
        if(response == null || !response.contentType().startsWith("image")) {
            //todo
        }
        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(responseInputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return S3File.builder()
                .inputStreamResource(new InputStreamResource(stream))
                .contentType(response.contentType())
                .build();
    }




//    public List<InputStreamResource> getAllImages(String prefix){
//        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
//                .bucket(BUCKET_NAME)
//                .prefix(prefix)
//                .build();
//
//        List<InputStreamResource> images = new ArrayList<>();
//        ListObjectsV2Response listObjectsResponse;
//        do {
//            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
//            for (S3Object object : listObjectsResponse.contents()) {
//                images.add(getImageFromS3(object.key()).getInputStreamResource());
//            }
//        } while (listObjectsResponse.isTruncated());
//
//        return images;
//    }
    public List<ImageMetaData> getAllImages(String prefix){
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .prefix(prefix)
                .build();

        List<ImageMetaData> images = new ArrayList<>();
        ListObjectsV2Response listObjectsResponse;
        do {
            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            for (S3Object object : listObjectsResponse.contents()) {
                images.add(ImageMetaData.builder()
                                .path(object.key())
                                .size(object.size())
                                .build());
            }
        } while (listObjectsResponse.isTruncated());
//        if(images.size() != 0){
//            images.remove(0);
//        }
        return images;
    }


    public List<S3Object> getObjects() {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .build();

        List<S3Object> objects = new ArrayList<>();

        ListObjectsV2Response listObjectsResponse;
        do {
            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            objects.addAll(listObjectsResponse.contents());
            listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .continuationToken(listObjectsResponse.nextContinuationToken())
                    .build();
        } while (listObjectsResponse.isTruncated());
        return objects;
    }


}
