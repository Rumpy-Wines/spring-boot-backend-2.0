package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import com.example.rumpy.util.MyStringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
public class Address extends AddressAbstractClass implements HasEntityRecord<Address.EntityRecord>{
    private Boolean isDefault = false;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EntityRecord implements Serializable {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String title;
        private String streetAddress;
        private String state;
        private List<String> landmarks;
        private Boolean isDefault;
        private User.EntityRecord user;
    }

    @Override
    public EntityRecord getEntityRecord() {
        List<String> landmarks;

        landmarks = Arrays.asList(this.getLandmarks().strip().split(MyStringUtil.STRING_LIST_SEPARATOR));

        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getTitle(),
                this.getStreetAddress(),
                this.getState(),
                landmarks,
                this.getIsDefault(),
                null
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
}//end class
