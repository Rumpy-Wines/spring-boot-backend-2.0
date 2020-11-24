package com.example.rumpy.model;

import com.example.rumpy.util.MyStringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.stream.Collectors;

@Data
@MappedSuperclass
public abstract class PlatformItemAbstractClass extends RootModel {
    @Column(nullable = false)
    private String origin;

    @Column(nullable = false, name="image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String year;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, name="alcohol_content")
    private Double alcoholContent;

    @Column(nullable = false, name="price_per_item")
    private Long pricePerItem = 0L;

    @Column(nullable = false, name="numberAvailable")
    private Integer numberAvailable;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WineCategory category;

    public void setTags(List<String> tags){
        this.tags = tags.stream()
                .collect(Collectors.joining(MyStringUtil.STRING_LIST_SEPARATOR));
    }//end method setTags
}//end abstract class PlatformItemAbstractClass
