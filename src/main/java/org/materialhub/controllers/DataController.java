package org.materialhub.controllers;

import org.materialhub.dto.PathDTO;
import org.materialhub.services.FileService;
import org.materialhub.services.FolderService;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("{collegeCode}/years/{yearId}/specializations/{specializationId}/subjects/{subjectId}/categories/{categoryId}") 
public class DataController {

    @Inject
    FileService fileService;  

    @Inject
    FolderService folderService;   

    @Inject
    Template materials;

    @GET
    @Path("resources")
    public TemplateInstance getData(Long categoryId , String collegeCode , Long yearId , Long specializationId , Long subjectId) { 
        PathDTO pathDto  = new PathDTO(collegeCode , yearId , specializationId , subjectId , categoryId) ; 

        return materials
        .data("files", fileService.getFilesByCategory(categoryId))
        .data("folders" , folderService.getFoldersByCategoryId(categoryId))  
        // to avoid joins 
        .data("pathObj" , pathDto ) 
        // navigation path 
        .data("path" , pathDto.buildDynamicPath() ) ; 
    }  


}
