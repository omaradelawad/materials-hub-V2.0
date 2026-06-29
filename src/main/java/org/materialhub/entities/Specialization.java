package org.materialhub.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

import org.materialhub.dto.SpecializationDTO;

@Table
@Entity
public class Specialization extends PanacheEntity {


    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    public String type ;


    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    public Year year; 


    @ManyToOne
    @JoinColumn(name = "college_id", nullable = false)
    public College college;

    @OneToMany(mappedBy = "specialization" ,  cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    public List<Subject> subjects ;


    public Specialization(String name, String description, Year year , College college) {
        this.name = name;
        this.description = description;
        this.year = year; 
        this.college = college ; 
    } 
    

    public Specialization(SpecializationDTO specializationDTO) {
        this.name = specializationDTO.name;
        this.description = specializationDTO.description;
        this.type = specializationDTO.type;
    } 


    public Specialization() {

    }

}
