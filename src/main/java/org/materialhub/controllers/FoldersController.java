package org.materialhub.controllers;

import org.materialhub.services.FileService;
import org.materialhub.services.FolderService;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;  

@Path("{collegeCode}/years/{yearId}/specializations/{specializationId}/subjects/{subjectId}/categories/{categoryId}/folders/") 
public class FoldersController { 

    @Inject
    FileService fileService;  

    @Inject
    FolderService folderService;   

    @Inject
    Template folders; 


    @GET 
    @Path("/{folderId}")
    public TemplateInstance getFolder(@PathParam("folderId") Long folderId) {
        return folders
        .data("files", fileService.getFilesByFolder(folderId))
        .data("folder" , folderService.getFolderById(folderId))
        ;   
    } 


}
