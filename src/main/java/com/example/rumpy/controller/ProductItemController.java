package com.example.rumpy.controller;

import com.example.rumpy.model.Gender;
import com.example.rumpy.model.ProductItem;
import com.example.rumpy.model.User;
import com.example.rumpy.model.WineCategory;
import com.example.rumpy.service.ProductItemService;
import com.example.rumpy.service.UserService;
import com.example.rumpy.util.FileStorageUtil;
import com.example.rumpy.util.HttpErrors;
import com.example.rumpy.util.ValidateRequestParamUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/product-items")
public class ProductItemController {

    @Autowired
    private ProductItemService productItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @GetMapping
    public ResponseEntity<?> findAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));
        Page<ProductItem> productItems = productItemService.findAll(pageable);

        Page<ProductItem.EntityRecord> productItemEntityRecords = productItems.map(ProductItem::getEntityRecord);

        return ResponseEntity.ok(productItemEntityRecords);
    }//end method findAll

    @PostMapping
    public ResponseEntity<?> createProductItem(
            @RequestParam Map<String, Object> requestMap,
            @RequestParam("tags") Optional<List<String>> optionalTags,
            @RequestParam("image") Optional<MultipartFile> optionalDisplayPhoto
    ) {
        Optional<User> optionalUser = userService.getAuthenticatedUser();

        if (optionalUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<>(Map.of("message", "Unauthorized")));

        Map<String, Class<?>> requiredValues = Map.of(
                "origin", String.class,
                "name", String.class,
                "year", String.class,
                "address", String.class,
                "alcoholContent", Integer.class,
                "pricePerItem", Long.class,
                "category", WineCategory.class,
                "manufacturerDescription", String.class,
                "numberAvailable", Integer.class
        );

        ValidateRequestParamUtil validateRequestParamUtil = ValidateRequestParamUtil
                .forRequired(requiredValues);

        validateRequestParamUtil.setReferenceMap(requestMap);
        HttpErrors validationErrors = validateRequestParamUtil.validate();

        if(optionalTags.isEmpty())
            validationErrors.put("tags", "Must be an array of strings");

        if(optionalDisplayPhoto.isEmpty())
            validationErrors.put("image", "The image field is required");

        if(validationErrors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(validationErrors);

        ObjectMapper objectMapper = new ObjectMapper();


        WineCategory category = null;
        try {
            category = objectMapper.readValue(objectMapper.writeValueAsString(requestMap.get("category")), WineCategory.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ProductItem productItem = new ProductItem();

        productItem.setOrigin((String) requestMap.get("origin"));
        productItem.setName((String) requestMap.get("name"));
        productItem.setYear((String) requestMap.get("year"));
        productItem.setAddress((String) requestMap.get("address"));
        productItem.setAlcoholContent(Integer.parseInt((String) requestMap.get("alcoholContent")) );
        productItem.setPricePerItem(Long.parseLong((String) requestMap.get("pricePerItem")));
        productItem.setNumberAvailable(Integer.parseInt((String) requestMap.get("numberAvailable")));
        productItem.setTags(optionalTags.get());
        productItem.setCategory(category);
        productItem.setManufacturerDescription((String) requestMap.get("manufacturerDescription"));

        productItemService.createProductItem(productItem, optionalDisplayPhoto.get(), optionalUser.get());
        return ResponseEntity.ok(productItem.getEntityRecord());
    }//end method createProductItem

    @GetMapping("/display-photo/{fileName}")
    public ResponseEntity<?> renderDisplayPhoto(@PathVariable("fileName") String fileName, HttpServletRequest request){
        Resource resource = fileStorageUtil.getFileUrlResourceForItemImage(fileName);

        String mimeType = "*/*";

        try {
            mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + resource.getFilename())
                .body(resource);
    }//end method filePath
}//end class ProductItemController
