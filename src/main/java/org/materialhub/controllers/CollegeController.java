package org.materialhub.controllers;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.materialhub.entities.User;
import org.materialhub.entities.Year;
import org.materialhub.services.CollegeService;

import java.util.List;

@Path("/{collegeCode}")
public class CollegeController {

    @Inject
    Template College ;

    @Inject
    CollegeService collegeService;

    @GET
    public TemplateInstance collegePage(String collegeCode){

        List<Year> years = collegeService.getCollegeYears(collegeCode); 
        var college = collegeService.getCollegeByCollegeCode(collegeCode) ; 
        var BranchedspecializationsCount = collegeService.getColegeSpecializations("branched" , collegeCode).size()  ;
        var ComprehensiveSpecializationsCount = collegeService.getColegeSpecializations("comprehensive" , collegeCode).size()  ;
        var collegeUsersCount = User.count("college.collegeCode = ?1" , collegeCode) ;

        return College
        .data("college" , college).data( "college_Years" , years )
        .data("total_years", years.size())
        .data("BranchedspecializationsCount", BranchedspecializationsCount)
        .data("ComprehensiveSpecializationsCount", ComprehensiveSpecializationsCount).data("collegeUsersCount", collegeUsersCount) 
        .data("path", new String[]{collegeCode} )  ;

        

    }

}
