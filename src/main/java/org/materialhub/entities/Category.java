package org.materialhub.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

import org.materialhub.dto.CategoryDTO;

@Table
@Entity
public class Category extends PanacheEntity {

    @Column(nullable = false)
    public  String name ;

    @Column(nullable = false)
    public  String description ;

    @ManyToOne
    @JoinColumn(name = "subject_id" , nullable = false)
    public Subject subject ;


    @OneToMany(mappedBy = "category" , cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    public List<Folder> folders ;

    @OneToMany(mappedBy = "category" , cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    public List<File> files ;




    public Category(String name, String description, Subject subject) {
        this.name = name;
        this.description = description;
        this.subject = subject;
    } 

    public Category(CategoryDTO categoryDTO){
        this.name = categoryDTO.name;
        this.description = categoryDTO.description;
    }

    public Category() {

    }



}
