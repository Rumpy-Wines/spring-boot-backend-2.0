package com.example.rumpy.service;

import com.example.rumpy.model.ProductItem;
import com.example.rumpy.model.User;
import com.example.rumpy.repository.ProductItemRepository;
import com.example.rumpy.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ProductItemService {
    public static final String STORAGE_PATH = "productItemDisplayPhotos";

    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    public Page<ProductItem> findAll(Pageable pageable){
        return productItemRepository.findAll(pageable);
    }//end method findAll

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
}//end class ProductItemService
