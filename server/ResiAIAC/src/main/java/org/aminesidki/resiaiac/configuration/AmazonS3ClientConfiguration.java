package org.aminesidki.resiaiac.configuration;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonS3ClientConfiguration {
  @Value("${seaweedfs.s3.internal-endpoint}")
  private String endpoint;

  @Value("${seaweedfs.s3.region}")
  private String region;

  private final AwsCredentialsProvider awsCredentialsProvider;
  private final S3Configuration s3Configuration;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .endpointOverride(URI.create(endpoint))
        .region(Region.of(region))
        .credentialsProvider(awsCredentialsProvider)
        .serviceConfiguration(s3Configuration)
        .build();
  }
}
