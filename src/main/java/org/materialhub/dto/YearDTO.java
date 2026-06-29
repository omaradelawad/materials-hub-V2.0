package org.materialhub.dto;

import jakarta.validation.constraints.*;
import org.materialhub.entities.Year;

public class YearDTO {

    @NotNull(message = "اسم السنة مطلوب")
    @NotBlank(message ="اسم السنة لا يجب ات يكون فارغا")
    @Size(min = 3, max = 100, message = "اسم السنة بين 3 و 100 حرف")
    public String name ;

    @NotNull(message = "اسم السنة مطلوب")
    @NotBlank(message ="اسم السنة لا يجب ان يكون فارغا")
    @Size(min = 3, max = 100, message = "وصف السنة بين 3 و 100 حرف")
    public String description;

    @Min(1)
    @Max(10)
    public int yearNumber ;

    public String imageURL ;



    public YearDTO() {
    }

    public YearDTO(String name, String description, int yearNumber, String imageURL) {
        this.name = name;
        this.description = description;
        this.yearNumber = yearNumber;
        this.imageURL = imageURL;
    }

    public YearDTO(String name, String description, int yearNumber) {
        this.name = name;
        this.description = description;
        this.yearNumber = yearNumber;
    }


    @SuppressWarnings("unused")
    private YearDTO(Year year) {
        this.name = year.name ;
        this.description = year.description ;
        this.yearNumber = year.yearNumber ;
    }




}
