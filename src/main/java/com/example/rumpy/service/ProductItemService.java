package com.example.rumpy.service;

import com.example.rumpy.model.ProductItem;
import com.example.rumpy.model.ProductReview;
import com.example.rumpy.model.User;
import com.example.rumpy.model.WineCategory;
import com.example.rumpy.repository.ProductItemRepository;
import com.example.rumpy.repository.ProductReviewRepository;
import com.example.rumpy.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductItemService {
    public static final String STORAGE_PATH = "productItemDisplayPhotos";

    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    public Page<ProductItem> findAll(Pageable pageable){
        return productItemRepository.findAll(pageable);
    }//end method findAll

    public boolean existsById(String id) {
        return productItemRepository.existsById(id);
    }//end method existsById

    public ProductItem createProductItem(ProductItem productItem, MultipartFile displayPhoto, User user) {
        productItem.setUser(user);

        String imageUrl = productItem.getImageUrl();
        if(displayPhoto != null){
            String fileName = fileStorageUtil.storeFile(displayPhoto, STORAGE_PATH, true);
            String directory = fileStorageUtil.cleanRelativePathString(STORAGE_PATH);
            fileName = fileStorageUtil.cleanRelativePathString(fileName);
            fileName = fileStorageUtil.cleanRelativePathString(fileName.replaceFirst(directory, ""));
            productItem.setImageUrl(fileName);
        }

        productItemRepository.save(productItem);

        return productItem;
    }//end method createProductItem

    public Optional<ProductItem> findById(String id) {
        return productItemRepository.findById(id);
    }//end method findById

    public Optional<ProductItem> findByIdWithReviewsAndReviewUser(String id){
        return productItemRepository.findByIdWithReviewsAndReviewUser(id);
    }//end method findByIdWithReviewsAndReviewUser

    public ProductReview giveAReview(ProductReview productReview, ProductItem productItem, User user){

        productReview.setProductItem(productItem);
        productReview.setUser(user);

        return productReviewRepository.save(productReview);
    }//end method giveAReview

    public Page<ProductItem> findByCategory(WineCategory category, Pageable pageable) {
        return productItemRepository.findByCategory(category, pageable);
    }
}//end class ProductItemService
