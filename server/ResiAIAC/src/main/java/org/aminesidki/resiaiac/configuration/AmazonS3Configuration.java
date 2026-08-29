package org.aminesidki.resiaiac.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class AmazonS3Configuration {
  @Value("${seaweedfs.s3.access-key}")
  private String accessKey;

  @Value("${seaweedfs.s3.secret-key}")
  private String secretKey;

  @Bean
  public AwsCredentialsProvider awsCredentialsProvider() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }
  ;

  @Bean
  public S3Configuration s3Configuration() {
    return S3Configuration.builder().pathStyleAccessEnabled(true).build();
  }
}
