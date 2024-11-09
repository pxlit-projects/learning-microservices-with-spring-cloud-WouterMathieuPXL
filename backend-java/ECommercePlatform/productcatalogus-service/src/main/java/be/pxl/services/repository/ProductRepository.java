package be.pxl.services.repository;

import org.springframework.data.jpa.repository.Query;

import be.pxl.services.domain.Label;
import be.pxl.services.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query(value = "DELETE FROM product_label WHERE label_id = :labelId", nativeQuery = true)
    void removeLabelAssociations(@Param("labelId") Long labelId);
}
