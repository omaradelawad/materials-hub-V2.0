package org.materialhub.services ;


import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.materialhub.dto.CollegeDTO;
import org.materialhub.entities.College;
import org.materialhub.entities.Specialization;
import org.materialhub.entities.Year;

import java.util.List;


@ApplicationScoped
public class CollegeService {


    @Inject
    YearService yearService; 

    @Inject
    SpecializationService specializationService;


    @Inject
    Template fragments ;

    // public TemplateInstance getColleges(){
    //     List<College> colleges = College.list("ORDER BY collegeName") ;
    //    return fragments.getFragment("colleges_section").data("colleges", colleges) ;
    // }

    public List<College> getCollegesList(){
        List<College> colleges = College.list("ORDER BY collegeName") ;
        return colleges;
    } 

    public College getCollegeByCollegeCode(String colegeCode){
        return College.find("collegeCode = ?1", colegeCode).firstResult()  ; 
    } 


    public List<Specialization> getColegeSpecializations(String type , String collegeCode ) {
        return specializationService.getSpecializationsByTypeAndCollegeCode(type, collegeCode);
    }   


    public Response createCollege(CollegeDTO collegeDTO) {
        var college = new College();

        college.collegeName = collegeDTO.collegeName;
        college.collegeDescription = collegeDTO.collegeDescription;
        college.collegeCode = collegeDTO.collegeCode;
        college.collegePicturePath = collegeDTO.collegePicturePath;

        college.persist();
        return  Response.ok().build();
    }


    public Response editCollege(Long id , CollegeDTO collegeDTO)  {

        College college = College.findById(id);

        if(college == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        college.collegeName = collegeDTO.collegeName ;
        college.collegeDescription = collegeDTO.collegeDescription  ;
        college.collegeCode = collegeDTO.collegeCode  ;
        college.collegePicturePath = collegeDTO.collegePicturePath ;
        return Response.ok().build();

    }

    public List<Year> getCollegeYears(String  collegeCode){
        return yearService.getYearsByCollegeCode(collegeCode) ;
    }  


}
