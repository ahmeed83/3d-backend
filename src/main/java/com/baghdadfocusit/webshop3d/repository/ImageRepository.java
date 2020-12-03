package com.baghdadfocusit.webshop3d.repository;

import com.baghdadfocusit.webshop3d.entities.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends CrudRepository<Image, UUID> {

    List<Image> findImagesByProduct_Id(@Param("productId") UUID productId);
    void deleteByProduct_Id(@Param("productId") UUID productId);
}
