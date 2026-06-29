package org.materialhub.controllers.fragmentControllers;

import org.materialhub.services.SpecializationService;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType; 

@Path("/{collegeCode}/year/{yearId}")
public class SpecializatinFragmentController {

    @Inject
    SpecializationService specializationService ;

    @Inject 
    Template fragments ; 

    @GET  
    @Path("/specializations")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getSpecializationFragment(Long yearId , String collegeCode){
         return fragments.getFragment("specializations_section").data("specializations" , specializationService.getSpecializationsByYearID(yearId)).data("collegeCode", collegeCode) ;   
    }


}
