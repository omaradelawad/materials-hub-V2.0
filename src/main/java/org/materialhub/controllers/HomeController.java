package org.materialhub.controllers;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.materialhub.services.CollegeService;

@Path("/")
public class HomeController {

    @Inject
    CollegeService collegeService;

    @Inject
    Template index ;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return index.data("collegeList", collegeService.getCollegesList())  ;
    }


}
