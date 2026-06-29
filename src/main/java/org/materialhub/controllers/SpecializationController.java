package org.materialhub.controllers;
import org.materialhub.dto.PathDTO;
import org.materialhub.services.SpecializationService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/{collegeCode}/years/{yearId}/specializations/{specializationId}")
public class SpecializationController {

    @Inject
    Template specialization;

    @Inject
    SpecializationService specializationService; 

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/subjects")
    public TemplateInstance specializationPage(String collegeCode, Long yearId, Long specializationId) {
        return specialization
        .data("specialization", specializationService.getSpecializationByID(specializationId))
        .data("subjects", specializationService.getSpecializationSubjects(specializationId))
        .data("path" , new PathDTO(collegeCode , yearId , specializationId , null).buildDynamicPath())
        ;
    }

}
