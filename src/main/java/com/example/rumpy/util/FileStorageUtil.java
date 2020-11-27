package com.example.rumpy.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.rumpy.property.CloudinaryProperty;
import com.example.rumpy.service.ProductItemService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
@NoArgsConstructor
public class FileStorageUtil {
    @Autowired
    private MyStringUtil randomStringGenerator;

    @Autowired
    private CloudinaryProperty cloudinaryProperty;

    private Cloudinary cloudinary;


    public String storeFile(MultipartFile file, String directory, boolean generateFileName) {
        directory = cleanRelativePathString(directory);

        String fileExtension = com.google.common.io.Files.getFileExtension(file.getOriginalFilename());
        String fileName = randomStringGenerator.generateRandom(30);
        String filePathString =
                cleanRelativePathString(directory) + "/" + fileName;

        cloudinary = new Cloudinary(Map.of(
                "cloud_name", cloudinaryProperty.getName(),
                "api_key", cloudinaryProperty.getApiKey(),
                "api_secret", cloudinaryProperty.getApiSecret()
        ));

        Map params = Map.of(
                "public_id", filePathString,
                "overwrite", true
//                "resource_type", "image"
        );

        File convFile = null;

        try {
            convFile = multipartToFile(file, fileName + ("".equals(fileExtension) ? "" : fileExtension));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map uploadResult = null;
        try {
            uploadResult = cloudinary.uploader().upload(convFile, params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (String) uploadResult.get("secure_url");
    }//end method storeFile

    private File multipartToFile(MultipartFile file, String fileName) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" +fileName);
        file.transferTo(convFile);
        return convFile;
    }

    public String cleanRelativePathString(String string) {
        return string.replaceAll("^(/+|\\\\+)+", "");
    }
}//end class FileStorageUtil
