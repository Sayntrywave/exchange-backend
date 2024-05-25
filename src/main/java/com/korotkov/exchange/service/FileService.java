package com.korotkov.exchange.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    private final AmazonS3 amazonS3;

    public FileService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void uploadFile(String bucketName, String key) {
        String filePath = "src/main/resources/Untitle123d.png";
        File file1 = new File(filePath);
        amazonS3.putObject(new PutObjectRequest("syantrywawe-exchange", "yo", file1));
    }

}
