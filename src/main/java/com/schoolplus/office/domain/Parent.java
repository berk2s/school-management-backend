package com.schoolplus.office.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("PARENT")
@Entity
public class Parent extends User {

    @ManyToMany(mappedBy = "parents", fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE
    })
    private List<Student> students = new ArrayList<>();

}
