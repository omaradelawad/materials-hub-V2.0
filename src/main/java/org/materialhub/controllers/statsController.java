package org.materialhub.controllers;

import org.jboss.resteasy.reactive.RestPath;
import org.materialhub.services.FileService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/stats") 
public class statsController { 

    @Inject
    FileService fileService ; 

    @GET 
    @Path("/viewFile")
    public void updateFileViews(@QueryParam("fileId") Long fileId) {  
        fileService.updateFileViews(fileId);    
    }   


    @GET 
    @Path("/downloadFile")
    public void updateFileDownloads(@QueryParam("fileId") Long fileId) {  
        fileService.updateFileDownloads(fileId);    
    }     

}
