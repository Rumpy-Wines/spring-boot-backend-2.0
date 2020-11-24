package com.example.rumpy.repository;

import com.example.rumpy.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, String> {

    @Query("SELECT p from ProductItem p  LEFT JOIN FETCH p.productReviews pr WHERE p.id = :id")
    Optional<ProductItem> findByIdWithReviewsAndReviewUser(@Param("id") String id);
}//end class ProductItemRepository
