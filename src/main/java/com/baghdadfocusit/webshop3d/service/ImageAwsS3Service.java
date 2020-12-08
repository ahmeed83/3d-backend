package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.configuration.aws.AmazonFileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
public class ImageAwsS3Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAwsS3Service.class);

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    private final AmazonFileStore amazonFileStore;


    /**
     * Constructor
     *
     * @param amazonFileStore amazonFileStore
     */
    public ImageAwsS3Service(final AmazonFileStore amazonFileStore) {
        this.amazonFileStore = amazonFileStore;
    }

    /**
     * Save product image
     *
     * @param productImage productImage
     * @return link of the image
     */
    public String saveImageInAmazonAndGetLink(final MultipartFile productImage, final String imageTypeName) {
        isImage(productImage);
        final Map<String, String> metadata = getMetaData(productImage);
        final String path = String.format("%s", bucket);

        final String fileName = String.format("%s/%s", imageTypeName,
                                              imageTypeName + "-" + productImage.getOriginalFilename() + "-" + UUID.randomUUID());
        try {
            LOGGER.info("Uploading image with name= " + fileName);
            amazonFileStore.saveImageInS3(path, fileName, Optional.of(metadata), productImage.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return amazonFileStore.getImageUrl(bucket, fileName);
    }

    /**
     * Get meta data.
     *
     * @param productImage productImage
     * @return metadata
     */
    private Map<String, String> getMetaData(final MultipartFile productImage) {
        final Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", productImage.getContentType());
        metadata.put("Content-Length", String.valueOf(productImage.getSize()));
        return metadata;
    }

    /**
     * Be sure if the multipart is a image.
     * w
     *
     * @param productImage productImage
     */
    private void isImage(final MultipartFile productImage) {
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType()).contains(productImage.getContentType())) {
            throw new IllegalStateException("File must be an Image [" + productImage.getContentType() + "]");
        }
    }

    /**
     * Delete images form S3 bucket.
     *
     * @param imageLocationOnS3 full image Url location.
     */
    public void deleteImage(final String imageLocationOnS3) {
        final StringBuilder awsS3UrlLocation = new StringBuilder();
        awsS3UrlLocation.append("https://");
        awsS3UrlLocation.append(bucket);
        awsS3UrlLocation.append(".s3.");
        awsS3UrlLocation.append(region);
        awsS3UrlLocation.append(".amazonaws.com/");
        String imageKeyId = imageLocationOnS3.replace(awsS3UrlLocation, "");
        amazonFileStore.deleteImageFromS3(bucket, imageKeyId);
        LOGGER.info("Image is deleted with image Id: {}", imageKeyId);
    }
}
