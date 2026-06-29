package org.materialhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CollegeDTO {

    @NotNull(message = "اسم الكلية مطلوب")
    @NotBlank(message ="اسم الكلية لا يجب ا يكون فارغا")
    @Size(min = 3, max = 100, message = "اسم الكلية بين 3 و 100 حرف")
    public String collegeName;

    @NotNull(message = "اسم الكلية مطلوب")
    @NotBlank(message ="اسم الكلية لا يجب ا يكون فارغا")
    @Size(min = 3, max = 100, message = "وصف الكلية بين 3 و 100 حرف")
    public String collegeDescription;

    @NotNull(message = "اسم الكلية مطلوب")
    @NotBlank(message ="اسم الكلية لا يجب ا يكون فارغا")
    @Size(min = 4, max = 9, message = "كود الكلية بي ن4 الى 0 خروف")
    public String collegeCode;

    @Pattern(regexp = "^(http|https)://.*$", message = "لابد أن يكون رابط صحيح")
    public String collegePicturePath;

}
