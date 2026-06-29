package org.materialhub.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.materialhub.dto.FolderDTO ; 
import java.util.List;


@Entity
@Table
public class Folder extends PanacheEntity {


    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    public String createdBy;

    @OneToMany(mappedBy = "folder" , cascade = {CascadeType.MERGE , CascadeType.PERSIST} , fetch = FetchType.LAZY)
    public List<File> files; 

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category; 


    public Folder(String name, String description,  String createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Folder() {

    } 

    public Folder(FolderDTO folderDTO) {
        this.name = folderDTO.name;
        this.description = folderDTO.description;
    }


}
