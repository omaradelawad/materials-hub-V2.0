package org.materialhub.controllers;
import org.materialhub.dto.PathDTO;
import org.materialhub.services.YearService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/{collegeCode}/years/{yearId}")
public class YearController {

    @Inject 
    Template year ; 

    @Inject 
    YearService yearService ;

    @GET 
    @Produces(MediaType.TEXT_HTML) 
    @Path("/specializations")
    public TemplateInstance yearsPage(String collegeCode , Long yearId){
        return year
        .data("year" , yearService.getYear(yearId))
        .data("path" , new PathDTO(collegeCode , yearId , null , null ).buildDynamicPath())
        .data("year_specializations" , yearService.getYearSpecializationsById(yearId)); 
    }

}
