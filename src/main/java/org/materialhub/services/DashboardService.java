package org.materialhub.services;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.materialhub.dto.*;
import org.materialhub.entities.*;
import java.util.List;
import java.nio.file.Path;

@ApplicationScoped
public class DashboardService {

    @Inject SecurityIdentity securityIdentity;
    @Inject YearService yearService;
    @Inject SpecializationService specializationService;
    @Inject SubjectService subjectService;
    @Inject CategoryService categoryService;
    @Inject FolderService folderService;
    @Inject FileService fileService;
    @Inject CollegeService collegeService; 


    // --- Helper Methods ---

    private User getCurrentUser() {
        return User.find("username", securityIdentity.getPrincipal().getName()).firstResult();
    }

    private boolean isNotAuthorized(College resourceCollege, User admin) {
        return resourceCollege == null || !resourceCollege.id.equals(admin.college.id);
    }

    private boolean isNotAuthorized(Category category, User admin) {
        return category == null || isNotAuthorized(category.subject.year.college, admin);
    }

    private boolean isNotAuthorized(Folder folder, User admin) {
        return folder == null || isNotAuthorized(folder.category, admin);
    }

    // =============================================
    // YEAR CRUD
    // =============================================

    @Transactional
    public Response addYear(User admin , YearDTO yearDTO) { 
        // more restrictions logic will be added in the future like banned user or something  , in next versions if needed 
        return yearService.addYear(admin,yearDTO);
    }

    @Transactional
    public Response updateYear( User admin ,Long id, YearDTO yearDTO) {



        Year year = Year.findById(id);

        if (year == null) return Response.status(Status.NOT_FOUND).build();

        // IDOR Check
        if (isNotAuthorized(year.college, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية تعديل هذه السنة").build();
        }
        return yearService.updateYear(id, yearDTO);
    }

    @Transactional
    public Response deleteYear(User admin ,Long id) {

        Year year = Year.findById(id);
        if (year == null) return Response.status(Status.NOT_FOUND).build();

        // IDOR Check
        if (isNotAuthorized(year.college, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية حذف هذه السنة").build();
        }
        
        return yearService.deleteYear(id);
    }

    // =============================================
    // SPECIALIZATION CRUD (dashboard service = more restrictions logic )
    // =============================================

    @Transactional
    public Response addSpecialization(User user , SpecializationDTO specDTO) {

        Year year = Year.findById(specDTO.yearID);
        
        if (year == null) return Response.status(Status.NOT_FOUND).entity("السنة غير موجودة").build();



 
        if (isNotAuthorized(year.college, user)) {
            return Response.status(Status.FORBIDDEN).entity("السنة المحددة لا تنتمي لكليتك").build();
        } 
        
        // NOTE , passing the user or the college as argument to unify the logic with addYear 
        return specializationService.addSpecialization(user.college  ,  specDTO);
    }

    @Transactional
    public Response updateSpecialization(User user , Long id, SpecializationDTO specDTO) {
        
        Specialization spec = Specialization.findById(id);
        if (spec == null) return Response.status(Status.NOT_FOUND).build();

        if (isNotAuthorized(spec.college, user)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        return specializationService.updateSpecializationByID(id, specDTO);
    }

    @Transactional
    public Response deleteSpecialization(User user ,Long id) {  
        
        Specialization spec = Specialization.findById(id);
        if (spec == null) return Response.status(Status.NOT_FOUND).build();

        if (isNotAuthorized(spec.college, user)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        return specializationService.deleteSpecializationByID(id);
    }

    // =============================================
    // SUBJECT CRUD
    // =============================================

    @Transactional
    public Response addSubject(User admin, SubjectDTO subjectDTO) {
       
        Year year = Year.findById(subjectDTO.yearId);
        Specialization spec = Specialization.findById(subjectDTO.specializationId);

        if (year == null || spec == null) return Response.status(Status.NOT_FOUND).build();

        if (spec.year.id != year.id) return Response.status(Status.BAD_REQUEST).entity("السنة والتخصص غير متوافقين").build();
        // IDOR Check
        if (isNotAuthorized(year.college, admin) || isNotAuthorized(spec.college, admin)) {
            return Response.status(Status.FORBIDDEN).entity("السنة أو التخصص خارج كليتك").build();
        }

        return subjectService.createSubject(subjectDTO , year , spec);
    }

    @Transactional
    public Response updateSubject(User admin , Long id, SubjectDTO subjectDTO) {
        Subject subject = Subject.findById(id);
        if (subject == null) return Response.status(Status.NOT_FOUND).build();

        if (isNotAuthorized(subject.year.college, admin)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        return subjectService.updateSubject(id, subjectDTO);
    }

    @Transactional
    public Response deleteSubject(User admin,Long id) {
        
        Subject subject = Subject.findById(id);
        if (subject == null) return Response.status(Status.NOT_FOUND).build();

        if (isNotAuthorized(subject.year.college, admin)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        return subjectService.deleteSubject(id);
    }

    // =============================================
    // CATEGORY CRUD
    // =============================================

    @Transactional
    public Response addCategory(User admin  , CategoryDTO categoryDTO) {
        Subject subject = Subject.findById(categoryDTO.subjectID);
        if (subject == null) return Response.status(Status.NOT_FOUND).build();

        // IDOR Check
        if (isNotAuthorized(subject.year.college, admin)) {
            return Response.status(Status.FORBIDDEN).build();
        }

        return categoryService.addCategory(categoryDTO);
    }

    @Transactional
    public Response updateCategory(User admin, Long id, CategoryDTO categoryDTO) {
        Category category = Category.findById(id);
        if (category == null) return Response.status(Status.NOT_FOUND).build();

        if (isNotAuthorized(category.subject.year.college, admin)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        return categoryService.updateCategory(id, categoryDTO);
    }

    @Transactional
    public Response deleteCategory(User admin, Long id) {
        Category category = Category.findById(id);
        if (category == null) return Response.status(Status.NOT_FOUND).build();

        if (isNotAuthorized(category.subject.year.college, admin)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        return categoryService.deleteCategory(id);
    }


    public List<Folder> getFoldersByCategoryId(User admin, Long categoryId) {
        Category category = Category.findById(categoryId);
        if (category == null || isNotAuthorized(category, admin)) {
            return List.of();
        }
        return folderService.getFoldersByCategoryId(categoryId);
    }

    public List<File> getFilesByCategory(User admin, Long categoryId) {
        Category category = Category.findById(categoryId);
        if (category == null || isNotAuthorized(category, admin)) {
            return List.of();
        }
        return fileService.getFilesByCategory(categoryId);
    }

    // =============================================
    // GETTERS (Filtered by College)
    // =============================================

    public List<Year> getAllYears() {
        User admin = getCurrentUser();
        return Year.list("college.id", admin.college.id);
    }

    public List<Specialization> getAllSpecializations(User admin , Long yearId) {  
        
        if (yearId == null) {
            return Specialization.list("college.id", admin.college.id);  
        }  

        Year year = Year.findById(yearId);

        if (year == null) {
            throw new NotFoundException("العام الدراسي غير موجود");  
        };  

        if (isNotAuthorized(year.college, admin)){
            throw new ForbiddenException("العام الدراسي المحدد لا ينتمي لكليتك");  
        }; 
        
        return  specializationService.getSpecializationsByYearID(yearId)  ; 
    } 

    // =============================================
    // SPECIALIZATION GETTER (filtered by College)
    // ============================================= 

    public List<Specialization> getAllSpecializationsByUserCollege(User user) {  
        return specializationService.getAllSpecializationsByCollegeId(user.college.id);  
    }  

    public Specialization getSpecialization(Long id) {  
        return Specialization.findById(id); 
    }  

    public List<Subject> getAllSubjects(User admin) {
        return subjectService.getSubjectsByCollegeId(admin.college.id);  
    }  

    // =============================================
    // FOLDERS AND FILES CRUD WITH IDOR CHECK
    // =============================================

    @Transactional
    public Response addFolder(User admin, FolderDTO folderDTO) {
        Category category = Category.findById(folderDTO.categoryId);
        if (category == null) {
            return Response.status(Status.NOT_FOUND).entity("التصنيف غير موجود").build();
        }

        // IDOR Check
        if (isNotAuthorized(category, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية إضافة مجلد في هذا التصنيف").build();
        }

        return folderService.createFolder(admin, folderDTO);
    }

    @Transactional
    public Response deleteFolder(User admin, Long folderId) {
        Folder folder = Folder.findById(folderId);
        if (folder == null) {
            return Response.status(Status.NOT_FOUND).entity("المجلد غير موجود").build();
        }

        // IDOR Check
        if (isNotAuthorized(folder, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية حذف هذا المجلد").build();
        }

        return folderService.deleteFolder(folderId);
    }

    @Transactional
    public Response updateFolder(User admin, Long folderId, FolderDTO folderDTO) {
        Folder folder = Folder.findById(folderId);
        if (folder == null) {
            return Response.status(Status.NOT_FOUND).entity("المجلد غير موجود").build();
        }

        // IDOR Check
        if (isNotAuthorized(folder, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية تعديل هذا المجلد").build();
        }

        return folderService.updateFolder(folderId, folderDTO);
    }

    @Transactional 

    // idea : form includes input data  + file , cheks (IDOR , files  , etc...) 
    // 2 fill the FIleDTO with the form data to pass it th fileService method to complite saving 
    // fileService method has more than fileDto parameter to avoid fetching category, folder .. etc again 

    public Response addFile(User admin, FileUploadForm form) { 
        // if the form has not been recived or it's categoryId is null so it doesn't make any sense to continue  
        if (form == null || form.categoryId == null) {
            return Response.status(Status.BAD_REQUEST).entity("البيانات المطلوبة مفقودة").build();
        }

        // check if the category exists 
        Category category = Category.findById(form.categoryId);
        if (category == null) {
            return Response.status(Status.NOT_FOUND).entity("التصنيف غير موجود").build();
        }

        // IDOR Check on Category - if  the category doesn't match the user college so return forbidden
        if (isNotAuthorized(category, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية إضافة ملف في هذا التصنيف").build();
        } 



        // searching for the folder if provided
        Folder folder = null;     
        // if the folder id is not provided so the file will be aved in the category itself
        if (form.folderId != null) { 
            // searching for the folder 
            folder = Folder.findById(form.folderId);
            // if the folder is not found so return not found 
            if (folder == null) {
                return Response.status(Status.NOT_FOUND).entity("المجلد المحدد غير موجود").build();
            } 

            // IDOR Check on Folder - check if the folder match the useer college , ckeck if folder belongs to the category or not (we already cheked the category belongs to the user college) without this the user can publish a file in any folder in the system even if the folder in other college 
            if (isNotAuthorized(folder, admin) || !folder.category.id.equals(category.id)) {
                return Response.status(Status.FORBIDDEN).entity("المجلد المحدد لا ينتمي لهذا التصنيف أو لا تملك الصلاحية").build();
            }
        }

        // check if the file has been uploaded with the form
        if (form.file == null) {
            return Response.status(Status.BAD_REQUEST).entity("الملف مطلوب").build();
        }
        

            // transfere the form data to the DTO to pass it to the fileService   
            // this will set the name , description , folderId (IDOR cheked already so safe to pass) , categoryId (IDOR cheked already so safe to pass)
            FileDTO fileDTO = new FileDTO(form); 
            // getting the year if from the safely retreived category to ensure the year is the correct one so no need to check th IDOR 
            fileDTO.yearId = category.subject.year.id;  
            
            Path file = form.file.uploadedFile() ; 
            // passing the file data to the fileService to add the file   
            // TODO : remove the category.id , folderId : we can get them from the DTO (Long categoryId;) & (Long folderId;) in the fileDTO instead of passing them from here   
            return fileService.addFile(fileDTO /* file data from the modal*/, category /* category id */, folder /* folder id */, file /* file stream */, form.file.fileName() /*real file name */, admin /* admin */); 
      

        
    }

    @Transactional
    public Response deleteFile(User admin, Long fileId) {
        File file = File.findById(fileId);
        if (file == null) {
            return Response.status(Status.NOT_FOUND).entity("الملف غير موجود").build();
        }

        // IDOR Check
        if (file.category == null || isNotAuthorized(file.category, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية حذف هذا الملف").build();
        }

        return fileService.deleteFile(fileId);
    }

    @Transactional
    public Response updateFile(User admin, Long fileId, FileDTO fileDTO) {
        File file = File.findById(fileId);
        if (file == null) {
            return Response.status(Status.NOT_FOUND).entity("الملف غير موجود").build();
        }

        // IDOR Check
        if (file.category == null || isNotAuthorized(file.category, admin)) {
            return Response.status(Status.FORBIDDEN).entity("لا تملك صلاحية تعديل هذا الملف").build();
        }

        return fileService.updateFile(fileId, fileDTO);
    }


}
