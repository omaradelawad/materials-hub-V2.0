package org.materialhub.entities;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.materialhub.dto.YearDTO;

import java.util.List;

@Entity
@Table(name = "year")
public class Year extends PanacheEntity {


    @Column(nullable = false)
    public String name ;


    @Column(columnDefinition = "varchar(500)"  , nullable = false)
    public String description;

    @Column(nullable = false , unique = true)
    public int yearNumber ;


    public String imageURL = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png" ;

    @ManyToOne
    @JoinColumn(name = "college_id", nullable = false)
    public College college;

    @OneToMany(mappedBy = "year" ,  cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} ,  fetch = FetchType.LAZY)
    public List<User> users;


    @OneToMany(mappedBy = "year" ,  cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    List<Subject> subjects;


    @OneToMany(mappedBy = "year" ,  cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    List<Specialization> specializations;

    @OneToMany(mappedBy = "year" ,  cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} ,  fetch = FetchType.LAZY)
    List<File> files ;

    public Year( @Nonnull String name,String description, int yearNumber , College college) {
        this.description = description;
        this.name = name;
        this.yearNumber = yearNumber;
        this.college = college;
    }


    public Year(YearDTO yearDTO) {
        this.name = yearDTO.name;
        this.description = yearDTO.description;
        this.yearNumber = yearDTO.yearNumber ;
        this.imageURL = yearDTO.imageURL;
    }



    public Year() {}


}
