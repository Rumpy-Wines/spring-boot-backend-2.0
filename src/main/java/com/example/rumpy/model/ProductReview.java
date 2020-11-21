package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview extends RootModel implements HasEntityRecord<ProductReview.EntityRecord> {
    @Column(nullable = false, scale = 1, precision = 2)
    private Double rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reviewText;

    record EntityRecord(
            String id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Double rating,
            String reviewText
    ){}

    @Override
    public EntityRecord getEntityRecord() {
        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getRating(),
                this.getReviewText()
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private ProductItem productItem;
}//end class ProductReview
