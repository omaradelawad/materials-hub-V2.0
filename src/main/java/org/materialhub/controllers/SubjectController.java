package org.materialhub.controllers;

import org.materialhub.dto.PathDTO;
import org.materialhub.services.SubjectService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/{collegeCode}/years/{yearId}/specializations/{specializationId}/subjects/{subjectId}")
public class SubjectController {

    @Inject
    Template subject;

    @Inject
    SubjectService subjectService; 

    @GET
    @Produces(MediaType.TEXT_HTML) 
    @Path("/categories")
    public TemplateInstance subjectPage(String collegeCode, Long yearId, Long specializationId, Long subjectId) {
        return subject.data("subject", subjectService.getSubjectById(subjectId))
        .data("categories", subjectService.getSubjectCategories(subjectId)) 
        // to avoid joins 
        .data("pathObj" , new PathDTO(collegeCode, yearId, specializationId, subjectId))
        .data("path" , new PathDTO(collegeCode, yearId, specializationId, subjectId).buildDynamicPath())
        ;
    } 


}
