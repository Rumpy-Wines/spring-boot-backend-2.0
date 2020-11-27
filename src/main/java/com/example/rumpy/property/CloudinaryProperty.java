package com.example.rumpy.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloudinary")
@Setter
@Getter
public class CloudinaryProperty {
    private String apiSecret;
    private String apiKey;
    private String name;
}//end class CloudinaryProperty
