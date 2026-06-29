package org.materialhub.controllers.fragmentControllers;
import org.materialhub.services.YearService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/{collegeCode}")
public class YearsFragmentController {


    @Inject
    YearService yearService ;

    @Inject 
    Template fragments ; 


    @GET
    @Path("/years")
    public TemplateInstance getYearsFragment(String collegeCode){
        return fragments.getFragment("years_section").data("years" , yearService.getYearsByCollegeCode(collegeCode)) ;   
    }

}
