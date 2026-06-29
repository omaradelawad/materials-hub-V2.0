package org.materialhub.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.materialhub.dto.YearDTO;
import org.materialhub.entities.Specialization;
import org.materialhub.entities.User;
import org.materialhub.entities.Year;
import java.util.List;

@ApplicationScoped
public class YearService {



    // CRUDS
    public List<Year> getYears(){
        List<Year> years = Year.list("ORDER BY yearNumber DESC") ;
        return years;
    }

    // ===========================================

    public Year getYear(Long id){
        Year year = Year.findById(id);
       return  year;
    }

    // ===========================================

    @Transactional
    public Response addYear(User user , YearDTO yearDTO){

        var year = new Year(yearDTO);
        year.college = user.college;
        year.persist();
        
        return  Response.ok().entity(year).build();
    }
    // ===========================================


    public List<Specialization> getYearSpecializationsById(Long yearId){
        List<Specialization> specializations = Specialization.list("year.id = ?1", yearId) ;
        return specializations ; 
    }

    // ===========================================
    @Transactional
    public Response updateYear( Long id ,  YearDTO yearDTO){
        Year year = Year.findById(id);

        if (year == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

            year.name = yearDTO.name;
            year.description = yearDTO.description;
            year.yearNumber =  yearDTO.yearNumber;
            return   Response.ok().entity("تم تحديث السنة بنجاح").build();

            
    }

    // ===========================================

    @Transactional
    public Response deleteYear(Long id){
        Year year = Year.findById(id);

        if (year == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        year.delete();
        return Response.ok().build();
    }


    // other methods

    public List<Year> getYearsByCollegeCode(String collegeCode){
        List<Year> years = Year.list("college.collegeCode = ?1 ORDER BY yearNumber DESC" ,  collegeCode) ;
        return years;
    }




}
