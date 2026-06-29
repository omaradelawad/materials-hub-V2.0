package org.materialhub.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SpecializationDTO {


        @NotNull(message = "اسم التخصص مطلوب") 
        @NotBlank(message = "اسم التخصص لا يمكن أن يكون فارغًا") 
        @Size(max = 255, message = "اسم التخصص لا يمكن أن يتجاوز 255 حرفًا")
        public String name ; 

        @NotNull(message = "وصف التخصص مطلوب") 
        @NotBlank(message = "وصف التخصص لا يمكن أن يكون فارغًا") 
        @Size(max = 400, message = "وصف التخصص لا يمكن أن يتجاوز 400 حرفًا")
        public String description ;  

        @NotNull(message = "نوع التخصص مطلوب") 
        @NotBlank(message = "نوع التخصص لا يمكن أن يكون فارغًا") 
        public String type ;

        
        @NotNull(message = "عام التخصص مطلوب") 
        public Long yearID ;   




}
