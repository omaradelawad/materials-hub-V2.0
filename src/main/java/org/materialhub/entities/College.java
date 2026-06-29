package org.materialhub.entities;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "college")
public class College extends PanacheEntity {

    @Column(nullable = false)
    public String collegeName;

    @Column(nullable = false)
    public String collegeDescription;


    @Column(nullable = false , unique = true , name = "college_code")
    public String collegeCode;

    @Column(nullable = false)
    public String collegePicturePath = "https://jkfenner.com/wp-content/uploads/2019/11/default.jpg" ;



    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL , orphanRemoval = true , mappedBy = "college" )
    List<Year> years;

    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL , orphanRemoval = true , mappedBy = "college" )
    List<User> users; 

    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL , orphanRemoval = true , mappedBy = "college" )
    List<Specialization> specializations ;


    public College(String collegeName, String collegeDescription ,  String collegePicturePath) {
        this.collegeName = collegeName;
        this.collegeDescription = collegeDescription;
        this.collegePicturePath = collegePicturePath;
    }

    public College() {

    }


}
