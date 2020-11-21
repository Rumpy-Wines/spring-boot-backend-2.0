package com.example.rumpy.listener;

import com.example.rumpy.model.RootModel;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.UUID;

public class RootModelEventListener {
    @PrePersist
    private void initializeRootModel(RootModel model){
        validateRootModelId(model);
        validateRootModelCreatedAt(model);
        validateRootModelUpdatedAt(model);
    }//end method initializeRootModel

    @PreUpdate
    private void preUpdate(RootModel model) {
        model.setUpdatedAt(LocalDateTime.now());
    }//end method preUpdate

    private void validateRootModelCreatedAt(RootModel model) {
        if(model.getCreatedAt() == null){
            model.setCreatedAt(LocalDateTime.now());
        }
    }//end method validateRootModelCreatedAt

    private void validateRootModelUpdatedAt(RootModel model) {
        if(model.getUpdatedAt() == null){
            model.setUpdatedAt(LocalDateTime.now());
        }
    }//end method validateRootModelUpdatedAt


    private void validateRootModelId(RootModel model){
        if(model.getId() == null || "".equals(model.getId())){
            model.setId(UUID.randomUUID().toString());
        }
    }// end method validateRootModelId
}
