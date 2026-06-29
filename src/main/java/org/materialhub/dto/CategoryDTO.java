package org.materialhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryDTO {

    @NotBlank(message = "اسم الفئة لا يجب ان يكون فارغا")
    @NotNull(message = "اسم الفئة لا يجب ان يكون فارغا")
    public String name;

    @NotBlank(message = "الوصف لا يجب ان يكون فارغا")
    @NotNull(message = "الوصف لا يجب ان يكون فارغا")
    public String description;

    @NotNull(message = "معر المادة لا يجب ان يكون فارغا")
    public Long subjectID; 
}
