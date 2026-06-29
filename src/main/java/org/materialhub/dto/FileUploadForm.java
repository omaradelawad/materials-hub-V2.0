package org.materialhub.dto;


import org.jboss.resteasy.reactive.RestForm; 
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class FileUploadForm {

    @RestForm("name")
    public String name;

    @RestForm("description")
    public String description;

    @RestForm("categoryId")
    public Long categoryId;

    @RestForm("folderId")
    public Long folderId;

    @RestForm("yearId")
    public Long yearId;

    @RestForm("userId")
    public Long userId;

    @RestForm("file")
    public FileUpload file;
     
}