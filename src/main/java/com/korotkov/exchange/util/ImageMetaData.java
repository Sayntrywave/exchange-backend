package com.korotkov.exchange.util;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageMetaData {
    String path;
    Long size;
}
