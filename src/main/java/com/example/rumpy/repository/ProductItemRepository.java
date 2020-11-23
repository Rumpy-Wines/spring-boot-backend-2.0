package com.example.rumpy.repository;

import com.example.rumpy.model.ProductItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, String> {
}//end class ProductItemRepository
