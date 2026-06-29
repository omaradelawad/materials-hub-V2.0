package org.materialhub.services;

import java.util.List;

import org.materialhub.dto.SpecializationDTO;
import org.materialhub.entities.College;
import org.materialhub.entities.Specialization;
import org.materialhub.entities.Subject;
import org.materialhub.entities.Year;
import org.materialhub.enums.SPECIALIZATIONS_TYPES;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SpecializationService {

    @Inject
    YearService yearService;

    public boolean chackAvailabilityOfSpecializationID(Long id) {
        Specialization specialization = Specialization.findById(id);
        return specialization != null;
    }

    public boolean isValidSpecializationType(String type) {
        for (SPECIALIZATIONS_TYPES specializationType : SPECIALIZATIONS_TYPES.values()) {
            if (specializationType.type.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    public List<Specialization> getSpecializationsByYearID(Long yearID) {
        List<Specialization> specializations = Specialization.list("year.id", yearID);
        return specializations;
    }

    public Specialization getSpecializationByID(Long id) {
        Specialization specialization = Specialization.findById(id);
        return specialization;
    }

    public List<Specialization> getSpecializationsByType(String type) {
        List<Specialization> specializations = Specialization.list("type", type);
        return specializations;
    } 

    public List<Specialization> getSpecializationsByTypeAndCollegeCode(String type , String collegeCode) {
        List<Specialization> specializations = Specialization.list("type = ?1 and college.collegeCode = ?2", type, collegeCode);
        return specializations;
    }

    public List<Specialization> getSpecializationsByYearIDAndType(Long yearID, String type) {
        List<Specialization> specializations = Specialization.list("year.id = ?1 and type = ?2", yearID, type);
        return specializations;
    }

    public List<Specialization> getAllSpecializationsByCollegeId(Long collegeId) {  
        return Specialization.list("college.id", collegeId);  
    }  

    // Direct Panache call — no SubjectService injection needed
    public List<Subject> getSpecializationSubjects(Long specializationId) {
        return Subject.list("specialization.id = ?1", specializationId);
    }

    // create specialization - special logic only  
    @Transactional
    public Response addSpecialization(College college , SpecializationDTO specialization) {

        Year year = yearService.getYear(specialization.yearID); 
        
        if (year == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean isValidType = isValidSpecializationType(specialization.type);
        if (!isValidType) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid specialization type. Allowed values are: comprehensive, branched.")
                    .build();
        }

        Specialization specializationEntity = new Specialization(specialization);
        specializationEntity.year = year;  
        specializationEntity.college = college; 

        specializationEntity.persist();

        return Response.ok("تم اضافة التخصص بنجاح").build();
    }

    // edit specialization
    @Transactional
    public Response updateSpecializationByID(Long id, SpecializationDTO specializationDTO) {

        Specialization specialization = Specialization.findById(id);

        if (!chackAvailabilityOfSpecializationID(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // check if the specialization type is valid
        if (!isValidSpecializationType(specializationDTO.type)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid specialization type. Allowed values are: comprehensive, branched.")
                    .build();
        }

        specialization.name = specializationDTO.name;
        specialization.description = specializationDTO.description;
        specialization.type = specializationDTO.type;
        return Response.ok("تم تعديل التخصص بنجاح").build();
    }

    @Transactional
    public Response deleteSpecializationByID(Long id) {
        Specialization specialization = Specialization.findById(id);

        if (!chackAvailabilityOfSpecializationID(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        specialization.delete();
        return Response.ok().build();
    }

}
