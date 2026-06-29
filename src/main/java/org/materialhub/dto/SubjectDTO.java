package org.materialhub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubjectDTO {
    
    @NotBlank(message = "Name is required") 
    @NotNull(message = "subject name is required")
    public String name;

    @NotBlank(message = "Description is required") 
    @NotNull(message = "subject description is required")
    public String description;

    @NotNull(message = "subject term is required") 
    @Min(value = 1, message = "subject term must be at least 1")
    @Max(value = 5, message = "subject term must be at most 5") 
    public int term;

    @NotNull(message = "subject year ID is required")
    public Long yearId;

    @NotNull(message = "subject specialization ID is required")
    public Long specializationId;  

}
