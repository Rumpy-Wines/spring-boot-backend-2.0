package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import com.example.rumpy.util.MyStringUtil;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
public class Address extends RootModel implements HasEntityRecord<Address.EntityRecord>{
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String landmarks;

    record EntityRecord (
            String id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String title,
            String streetAddress,
            String state,
            List<String> landmarks
    ){}

    public void setLandmarks(List<String> landmarks){
        this.landmarks = landmarks.stream()
                .collect(Collectors.joining(MyStringUtil.STRING_LIST_SEPARATOR));
    }//end method setLandmarks

    @Override
    public EntityRecord getEntityRecord() {
        List<String> landmarks;

        landmarks = Arrays.asList(this.landmarks.strip().split(MyStringUtil.STRING_LIST_SEPARATOR));

        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getTitle(),
                this.getStreetAddress(),
                this.getState(),
                landmarks
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
}//end class
