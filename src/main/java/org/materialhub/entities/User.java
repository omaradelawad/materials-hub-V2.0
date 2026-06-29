package org.materialhub.entities;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

@Entity
@Table(name = "user")
@UserDefinition
public class User extends PanacheEntity  {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false) 
    @Roles
    public String ROLE ; 



    @Column(nullable = false)
    @Username
    public  String username ;

    @Column(nullable = false) 
    @Password
    public String password ;


    @ManyToOne
    @JoinColumn(name = "college_id" , nullable = false)
    public College college;


    @ManyToOne
    @JoinColumn(name = "year_id")
    public Year year ; 


    @OneToMany(mappedBy = "createdBy" , cascade = CascadeType.PERSIST , orphanRemoval = false)
    public List<File> files ; 

}
