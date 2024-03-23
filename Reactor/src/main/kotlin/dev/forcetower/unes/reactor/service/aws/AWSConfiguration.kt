package dev.forcetower.unes.reactor.service.aws

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AWSConfiguration(
    @Value("\${amazon.iam.access.key}") private val accessKey: String,
    @Value("\${amazon.iam.access.secret}") private val accessSecret: String,
    @Value("\${amazon.s3.bucket.region}") private val bucketRegion: String
) {
    fun credentials(): AwsCredentials {
        return AwsBasicCredentials.create(accessKey, accessSecret)
    }

    @Bean
    fun s3(): S3Client {
        return S3Client.builder()
            .region(Region.of(bucketRegion))
            .credentialsProvider(StaticCredentialsProvider.create(credentials()))
            .build()
    }
}