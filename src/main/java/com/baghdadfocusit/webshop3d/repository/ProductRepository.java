package com.baghdadfocusit.webshop3d.repository;

import com.baghdadfocusit.webshop3d.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, UUID> {

    @Query("SELECT p FROM Product p LEFT JOIN Category c ON (c.id = p.category) WHERE UPPER(p.name)"
            + " LIKE CONCAT" + "('%',UPPER(:productName),'%') AND UPPER(c.name) LIKE CONCAT('%',UPPER(:categoryName),'%')")
    Page<Product> getFilterProducts(@Param("productName") String productName,
                                    @Param("categoryName") String categoryName, Pageable pageable);

    Page<Product> findProductsByCategory_Id(@Param("categoryId") UUID categoryId, Pageable pageable);
    
    Page<Product> findProductsByRecommendedTrue(Pageable pageable);
    
    List<Product> findProductsByRecommendedTrue();

    Optional<Product> findProductByNameIgnoreCase(String productName);

    Page<Product> findProductsByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
