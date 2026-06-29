package org.materialhub.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.materialhub.dto.CategoryDTO;
import org.materialhub.entities.Category;
import org.materialhub.entities.Specialization;
import org.materialhub.entities.Subject;
import org.materialhub.entities.Year;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    SubjectService subjectService;

    @Inject
    SpecializationService specializationService;

    @Inject
    YearService yearService;

    // CRUDS
    public List<Category> getCategories() {
        List<Category> categories = Category.list("ORDER BY name ASC");
        return categories;
    }

    // ===========================================

    public Category getCategoryById(Long id) {
        Category category = Category.findById(id);
        return category;
    }

    public List<Category> getCategoriesBySubjectId(Long subjectId) {
        if (subjectId == null) {
            return List.of();
        }
        List<Category> categories = Category.list("subject.id = ?1 ORDER BY name ASC", subjectId);
        return categories;
    }

    public List<Category> getCategoriesByYearId(Long yearId) {
        List<Category> categories = Category.list("subject.year.id = ?1 ORDER BY name ASC", yearId);
        return categories;
    }

    // ===========================================

    @Transactional
    public Response addCategory(CategoryDTO categoryDTO) {
        Category category = new Category(categoryDTO); 
        

        // check the subject  
        Subject subject = subjectService.getSubjectById(categoryDTO.subjectID);
        if (subject == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        category.subject = subject;


        category.persist();
        return Response.ok().entity("تم حفظ الفئه بنجاح").build();
    }
    // ===========================================

    public List<Subject> getCategorySubjectsById(Long categoryId) {
        List<Subject> subjects = Category.find("subject.id = ?1", categoryId).list();
        return subjects;
    }

    // ===========================================
    @Transactional
    public Response updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = Category.findById(id);

        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        category.name = categoryDTO.name;
        category.description = categoryDTO.description;
        return Response.ok().entity("تم تحديث الفئه بنجاح").build();

    }

    // ===========================================
    @Transactional
    public Response transfereCategory(Long id, Long subjectId, Long SpecializationId, Long yearId) {

        Category category = Category.findById(id);

        Subject subject = subjectService.getSubjectById(subjectId);

        Specialization specialization = specializationService.getSpecializationByID(SpecializationId);

        Year year = yearService.getYear(yearId);

        if (category != null && subject != null && specialization != null && year != null) {
            category.subject = subject;
            subject.year = year;
            subject.specialization = specialization;
            return Response.ok().entity("تم نقل الفئه بنجاح").build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // ===========================================

    @Transactional
    public Response deleteCategory(Long id) {
        Category category = Category.findById(id);

        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        category.delete();
        return Response.ok().entity("تم حذف الفئه بنجاح").build();
    }

    // other methods



}
