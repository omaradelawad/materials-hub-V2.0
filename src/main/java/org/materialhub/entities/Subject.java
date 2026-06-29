package org.materialhub.entities;

import java.util.List;

import org.materialhub.dto.SubjectDTO;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Table
@Entity
public class Subject extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String description;

    
    public int term ;

    @ManyToOne
    @JoinColumn(name = "year_id" , nullable = false)
    public Year year ;

    @ManyToOne
    @JoinColumn(name = "specialization_id" , nullable = false)
    public Specialization specialization; 


    @OneToMany(mappedBy = "subject" , cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    public List<Category> categories ;


    public Subject(String name, String description, int term, Year year , Specialization specialization ) {
        this.name = name;
        this.description = description;
        this.term = term;
        this.year = year;
        this.specialization = specialization;
    } 

    public Subject(SubjectDTO subjectDTO) {
        this.name = subjectDTO.name;
        this.description = subjectDTO.description;
        this.term = subjectDTO.term;
    }

    public Subject() {

    }




}
