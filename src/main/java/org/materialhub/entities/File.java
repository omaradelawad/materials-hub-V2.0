package org.materialhub.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.materialhub.dto.FileDTO;

import java.sql.Date;

@Entity
@Table
public class File extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = true)
    public String description;



    @Column
    public int views ;

    @Column
    public int downloads ;

    @Column
    public double size ;

    @Column
    public String url ; 

    @CreationTimestamp
    public Date created;

    @UpdateTimestamp
    public Date updated;

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    public User createdBy;

    @ManyToOne
    @JoinColumn(name = "folder_id" , nullable = true)
    public Folder folder;

    @ManyToOne
    @JoinColumn(name = "year_id" ,  nullable = false)
    public Year year;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    public Category category; 



    public File(String name, String description, int views, int downloads, double size, Date created, Date updated) {
        this.name = name;
        this.description = description;
        this.views = views;
        this.downloads = downloads;
        this.size = size;
        this.created = created;
        this.updated = updated;
    } 

    public File(FileDTO fileDTO) {
        this.name = fileDTO.name;
        this.description = fileDTO.description;
        this.url = fileDTO.url;
    }

    public File() {

    }
}
