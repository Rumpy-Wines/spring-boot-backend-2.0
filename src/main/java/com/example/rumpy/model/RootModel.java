package com.example.rumpy.model;

import com.example.rumpy.listener.RootModelEventListener;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Builder
@EntityListeners({
        RootModelEventListener.class
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RootModel {
    @Id
    private String id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}//end class RootModel
