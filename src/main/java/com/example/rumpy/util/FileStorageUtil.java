package com.example.rumpy.util;

import com.example.rumpy.service.ProductItemService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//@Service
public class FileStorageUtil {
    private Path fileStoragePath;

    private String fileStorageLocation ;

    @Autowired
    private MyStringUtil randomStringGenerator;

    public FileStorageUtil() {
        this.fileStorageLocation = "fileStorage";
        this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

        try {
            if (!Files.exists(fileStoragePath))
                Files.createDirectories(fileStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Issue in creating file directory");
        }
    }// end constructor


    public String storeFile(MultipartFile file, String directory, boolean generateFileName) {
        String fileExtension = com.google.common.io.Files.getFileExtension(file.getOriginalFilename());
        directory = cleanRelativePathString(directory);

        String fileName = generateFileName
                ?
                String.format("%s%s", randomStringGenerator.generateRandom(30), "".equals(fileExtension) ? "" : "." + fileExtension)
                :
                StringUtils.cleanPath(file.getOriginalFilename());
        fileName = cleanRelativePathString(fileName);

        Path directoryPath = Paths.get(fileStoragePath.toString()).resolve(directory);
        Path filePath = directoryPath.resolve(fileName);

        try {
            if (!Files.exists(directoryPath))
                Files.createDirectory(directoryPath);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Issue in storing file");
        }

        return cleanRelativePathString(filePath.toString().replace(fileStoragePath.toString(), ""));
    }//end method storeFile

    public Resource getFileUrlResource(String stringPath) {
        stringPath = cleanRelativePathString(stringPath);
        Path path = fileStoragePath.resolve(stringPath);

        Resource resource = null;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (resource.exists() && resource.isReadable())
            return resource;

        return null;
    }//end method getFileResource

    public Resource getFileUrlResourceForItemImage(String fileName) {
        String path = fileStoragePath.resolve(ProductItemService.STORAGE_PATH)
                .resolve(fileName)
                .toString()
                .replace(fileStoragePath.toString(), "");
        return getFileUrlResource(path);
    }

    public String cleanRelativePathString(String string) {
        return string.replaceAll("^(/+|\\\\+)+", "");
    }
}//end class FileStorageUtil
