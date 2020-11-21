package com.example.rumpy.model;

import com.example.rumpy.util.MyStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class AddressAbstractClass extends RootModel {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, columnDefinition = "TEXT")
    protected String landmarks;

    public void setLandmarks(List<String> landmarks){
        this.landmarks = landmarks.stream()
                .collect(Collectors.joining(MyStringUtil.STRING_LIST_SEPARATOR));
    }//end method setLandmarks
}
