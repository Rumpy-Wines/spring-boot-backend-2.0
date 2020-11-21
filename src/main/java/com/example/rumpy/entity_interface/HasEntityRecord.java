package com.example.rumpy.entity_interface;

import java.io.Serializable;

public interface HasEntityRecord<T extends Serializable>{
    T getEntityRecord();
}//end interface HasEntityRecord
