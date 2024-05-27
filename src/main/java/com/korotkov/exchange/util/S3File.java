package com.korotkov.exchange.util;


import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.InputStreamResource;

@Data
@Builder
public class S3File {

    InputStreamResource inputStreamResource;
    String contentType;

}
