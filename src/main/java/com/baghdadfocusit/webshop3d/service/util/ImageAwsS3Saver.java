package com.baghdadfocusit.webshop3d.service.util;

import com.baghdadfocusit.webshop3d.configuration.aws.AmazonFileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
public class ImageAwsS3Saver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAwsS3Saver.class);

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final AmazonFileStore amazonFileStore;

    /**
     * Constructor
     *
     * @param amazonFileStore amazonFileStore
     */
    public ImageAwsS3Saver(final AmazonFileStore amazonFileStore) {
        this.amazonFileStore = amazonFileStore;
    }

    /**
     * Save product image
     *
     * @param productImage productImage
     * @return link of the image
     */
    public String saveImageInAmazonAndGetLink(final MultipartFile productImage) {
        isImage(productImage);
        final Map<String, String> metadata = getMetaData(productImage);
        final String path = String.format("%s", bucket);
        final String fileName = String.format("%s-%s", UUID.randomUUID(), LocalDateTime.now());
        try {
            LOGGER.info("Uploading image with name= " + fileName);
            amazonFileStore.saveImageInAmazon(path, fileName, Optional.of(metadata), productImage.getInputStream());
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
     *w
     * @param productImage productImage
     */
    private void isImage(final MultipartFile productImage) {
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(),
                           IMAGE_PNG.getMimeType(),
                           IMAGE_GIF.getMimeType())
                .contains(productImage.getContentType())) {
            throw new IllegalStateException("File must be an Image [" + productImage.getContentType() + "]");
        }
    }
}
