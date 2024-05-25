package com.korotkov.exchange.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.korotkov.exchange.config.AwsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Autowired
    private AwsProperties awsProperties;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(
                awsProperties.getAccessKey(),
                awsProperties.getSecretKey()
        );

        AmazonS3ClientBuilder clientBuilder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds));

        if (awsProperties.getEndpoint() != null) {
            clientBuilder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                    awsProperties.getEndpoint(),
                    awsProperties.getRegion()
            ));
        }

        return clientBuilder.build();
    }

}
