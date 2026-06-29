package org.materialhub.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.materialhub.dto.SubjectDTO;
import org.materialhub.entities.Category;
import org.materialhub.entities.Specialization;
import org.materialhub.entities.Subject;
import org.materialhub.entities.Year;
import java.util.List;

@ApplicationScoped
public class SubjectService {

    public List<Subject> getAllSubjects() {
        return Subject.listAll();
    }

    public Subject getSubjectById(Long id) {
        return Subject.findById(id);
    }

    public List<Subject> getSubjectsByYearId(Long yearId) {
        return Subject.list("year.id = ?1", yearId);
    }

    public List<Subject> getSubjectsBySpecializationId(Long specializationId) {
        return Subject.list("specialization.id = ?1", specializationId);
    }
 
    // get subjects by collage
    public List<Subject> getSubjectsByCollegeId(Long collegeId) {
        return Subject.list("year.college.id = ?1", collegeId);
    }

    // Direct Panache call — no CategoryService injection needed
    public List<Category> getSubjectCategories(Long subjectId) {
        return Category.list("subject.id = ?1 ORDER BY name ASC", subjectId);
    }

    @Transactional
    public Response createSubject(SubjectDTO subjectDTO) {
        Subject subject = new Subject(subjectDTO);

        
        Specialization specialization = Specialization.findById(subjectDTO.specializationId);
        Year year = Year.findById(subjectDTO.yearId);

        if (specialization == null || year == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        subject.specialization = specialization;
        subject.year = year;
        subject.persist();
        return Response.ok(subject).build();
    } 

    // for dashboard service unified logic 
    @Transactional
    public Response createSubject(SubjectDTO subjectDTO , Year year , Specialization specialization ) {
        Subject subject = new Subject(subjectDTO);
        subject.specialization = specialization;
        subject.year = year;
        subject.persist();
        return Response.ok("تم اضافة المادة بنجاح").build();
    }

    @Transactional
    public Response transfereSubject(Long id, Long specializationId, Long yearId) {

        Subject subject = Subject.findById(id);
        Specialization spec = Specialization.findById(specializationId);
        Year year = Year.findById(yearId);

        if (subject == null || spec == null || year == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

       // prevent cross-college transfers
        if (!year.college.id.equals(spec.college.id)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Year and Specialization belong to different colleges.")
                    .build(); 
        }

        subject.specialization = spec;
        subject.year = year;
        return Response.ok(subject).build();
    }

    @Transactional
    public Response updateSubject(Long id, SubjectDTO updatedSubject) {
        Subject subject = Subject.findById(id);

        if (subject == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        subject.name = updatedSubject.name;
        subject.description = updatedSubject.description;
        subject.term = updatedSubject.term;
        return Response.ok("تم تعديل المادة بنجاح").build();
    }

    @Transactional
    public Response deleteSubject(Long id) {
        Subject subject = Subject.findById(id);

        if (subject == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        subject.delete();
        return Response.ok().build();
    }
}
