package org.materialhub.dto;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathDTO {

    public String collegeCode;
    public Long yearId;
    public Long specializationId;
    public Long subjectId; 
    public Long categoryId;
    public Long folderId;

    public PathDTO(String collegeCode, Long yearId, Long specializationId, Long subjectId) {
        this.collegeCode = collegeCode;
        this.yearId = yearId;
        this.specializationId = specializationId;
        this.subjectId = subjectId;
    }  

        public PathDTO(String collegeCode, Long yearId, Long specializationId, Long subjectId , Long categoryId ,Long folderId) {
        this.collegeCode = collegeCode;
        this.yearId = yearId;
        this.specializationId = specializationId;
        this.subjectId = subjectId;
        this.categoryId = categoryId;
        this.folderId = folderId;     
    }  

    public PathDTO(String collegeCode, Long yearId, Long specializationId, Long subjectId , Long categoryId) {
        this.collegeCode = collegeCode;
        this.yearId = yearId;
        this.specializationId = specializationId;
        this.subjectId = subjectId;
        this.categoryId = categoryId;     
    }   
     
              
    




    public PathDTO() {
    }

    // الميثود التي تبني الرابط ديناميكياً
    public String buildDynamicPath() {
        return Stream.of(
            collegeCode,
            yearId != null ? "years/" + yearId : null,
            specializationId != null ? "specializations/" + specializationId : null,
            subjectId != null ? "subjects/" + subjectId : null,
            categoryId != null ? "categories/" + categoryId : null,
            folderId != null ? "folders/" + folderId : null
        )
        .filter(Objects::nonNull) 
        .collect(Collectors.joining("/")); 
    }
}
    

