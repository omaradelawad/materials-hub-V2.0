package org.materialhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FileDTO {

    // constrains for all fields with messeges in arabic
    // + constrains for not blabk fields with messeges in arabic
    @NotNull(message = "الاسم مطلوب") 
    @NotBlank(message = "الاسم لا يمكن ان يكون فارغا")
    public String name;

    @NotNull(message = "الوصف مطلوب")
    @NotBlank(message = "الوصف لا يمكن ان يكون فارغا")
    public String description;

    @NotNull(message = "الرابط مطلوب")
    @NotBlank(message = "الرابط لا يمكن ان يكون فارغا")
    public String url;

    public Long folderId;


    @NotNull(message = "التصنيف مطلوب")
    public Long categoryId; 


    @NotNull(message = "السنة مطلوبة")
    public Long yearId;  
    

    public FileDTO() {
        
    } 

    public FileDTO(FileUploadForm fileUploadForm) {
        this.name = fileUploadForm.name;
        this.description = fileUploadForm.description;
        this.folderId = fileUploadForm.folderId;
        this.categoryId = fileUploadForm.categoryId;
    }   






}
