package com.baghdadfocusit.webshop3d.configuration.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class AmazonFileStore {

    private final AmazonS3 amazonS3;

    public AmazonFileStore(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * Get image from AWS bucket.
     *
     * @param bucketName aws bucketName
     * @param imageKey   image key
     * @return url of the image in AWS
     */
    public String getImageUrl(final String bucketName, final String imageKey) {
        return amazonS3.getUrl(bucketName, imageKey).toExternalForm();
    }

    /**
     * Save image  to the Amazon bucket.
     *
     * @param path             path where the image will be stores
     * @param fileName         file name
     * @param optionalMetaData image optionalMetaData
     * @param inputStream      image inputStream
     */
    @Async
    public void saveImageInS3(String path, String fileName, Optional<Map<String, String>> optionalMetaData,
                              InputStream inputStream) {
        ObjectMetadata metaData = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(metaData::addUserMetadata);
            }
        });
        try {
            amazonS3.putObject(path, fileName, inputStream, metaData);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("failed to store content s3", e);
        }
    }

    /**
     * Delete image from the Amazon bucket.
     *
     * @param bucketName aws bucketName
     * @param imageKeyId image key id on S3
     */
    @Async
    public void deleteImageFromS3(final String bucketName, final String imageKeyId) {
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, imageKeyId);
        amazonS3.deleteObject(deleteObjectRequest);
    }
}
