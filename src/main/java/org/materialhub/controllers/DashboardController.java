package org.materialhub.controllers;
import java.util.List;
import org.materialhub.dto.CategoryDTO;
import org.materialhub.dto.SpecializationDTO;
import org.materialhub.dto.SubjectDTO;
import org.materialhub.dto.YearDTO;
import org.materialhub.dto.FolderDTO;
import org.materialhub.dto.FileUploadForm;
import org.materialhub.entities.Category;
import org.materialhub.entities.Folder;
import org.materialhub.entities.File;
import org.materialhub.entities.College;
import org.materialhub.entities.Specialization;
import org.materialhub.entities.Subject;
import org.materialhub.entities.User;
import org.materialhub.entities.Year;
import org.materialhub.services.CategoryService;
import org.materialhub.services.CollegeService;
import org.materialhub.services.DashboardService;
import org.materialhub.services.FolderService;
import org.materialhub.services.SpecializationService;
import org.materialhub.services.SubjectService;
import org.materialhub.services.YearService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard") 
@Authenticated 
public class DashboardController {
    
    @Inject SecurityIdentity identity ; 
    @Inject Template dashboard ;   
    @Inject Template dashboardFragments ;    

    @Inject CollegeService collegeService ;  
    @Inject YearService yearService ; 
    @Inject SpecializationService specializationService ; 
    @Inject SubjectService subjectService ; 
    @Inject CategoryService categoryService ; 
    @Inject FolderService folderService ;
    @Inject org.materialhub.services.FileService fileService ;
    @Inject DashboardService dashboardService ;  
    

    public User getUser(){
        return User.find("username = ?1" , identity.getPrincipal().getName()).firstResult();
    } 

    // other fragments chunks  

    //  recieves the wanted list , prepares it , sens them as html , client => htmx -> beforeend in the select input list 
    // <select hx-get="/dashboard/fragments/selectInputList?wanted=specs&by=yearId&yearId=1"  hx-target="#specsSelect" hx-swap="beforeend">
    // this is used for get list of  particular year,specs,subject etc... so you need to provide the id and what you want , don't use it for the starter page as firt opening of the modal or dashboard 
    @GET
    @Path("fragments/selectInputList")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getSelectInputList(@QueryParam("wanted") String wantedList , @QueryParam("by") String by ,@QueryParam("id") Long Id ,@QueryParam("name") String name ,@QueryParam("label") String label , @QueryParam("nextEndPoint") @DefaultValue("ignore") String nextEndPoint , @QueryParam("swapType") @DefaultValue("ignore") String swapType , @QueryParam("swapTarget") @DefaultValue("ignore") String swapTarget ){ 

        List<?> list = null ;  

        // scahng the comparison to start with the constant string to avoid null pointer exception in case the wantedList is not provided or is null 
        // do 
        switch(wantedList){
            case "specs" : 
                if( "year".equals(by)) {
                    list = specializationService.getSpecializationsByYearID(Id);
                } else if("college".equals(by)){
                    list = dashboardService.getAllSpecializationsByUserCollege(getUser());
                }
                break;  
            case "subjects" : 
                if( "spec".equals(by)) {
                    list = subjectService.getSubjectsBySpecializationId(Id);
                } 
                break;  
            case "categories" : 
                if( "subject".equals(by)) {
                    list = categoryService.getCategoriesBySubjectId(Id);
                } 
                break; 
            default :  
                return null ; 
        }

        return dashboardFragments.getFragment("selectOptons")
        .data("list", list)
        .data("name", name)
        .data("label", label)
        .data("nextEndPoint", nextEndPoint)
        .data("swapType", swapType)
        .data("swapTarget", swapTarget);
    }  


    // views 
    @GET
    @Path("/main") 
    
    public TemplateInstance showDashboard(){  

        College userCollege = getUser().college; 
        return dashboard
        .data("userCollege", userCollege)
        .data("collegeYears" , collegeService.getCollegeYears(userCollege.collegeCode));
    } 

    @GET
    @Path("/defaultView") 
    @Produces(MediaType.TEXT_HTML)   
    public TemplateInstance defaultView(){   
        College userCollege = getUser().college; 
        return dashboardFragments.getFragment("default")
        .data("userCollege", userCollege)
        .data("collegeYears" , collegeService.getCollegeYears(userCollege.collegeCode));
    }   

    // =========================================================== YEARS================================================================================

    // views and fragments
    @GET
    @Path("/years/yearsView") 
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance yearsManagementView(){
        return dashboardFragments.getFragment("years").data("yearsList", yearService.getYearsByCollegeCode(getUser().college.collegeCode));
    }  


    @GET
    @Path("years/yearsModal/year")      
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getYearsModal(@QueryParam("id") Long id){
        return   id == null ?  dashboardFragments.getFragment("YearModal").data("year", new Year() ) :  dashboardFragments.getFragment("YearModal").data("year", yearService.getYear(id))   ;
    }  
    
    // logic   
    
    // add 
    @POST
    @Path("years/addYear") 
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addYear(YearDTO yearDto){ 
        User admin = getUser() ;
        return dashboardService.addYear(admin , yearDto);
    }   
    
    // update 
    @POST
    @Path("years/updateYear/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateYear(Long id, YearDTO yearDTO){
        User admin = getUser() ; 
       return dashboardService.updateYear(admin , id, yearDTO);
    }  
    
    // delete 
    @POST
    @Path("years/deleteYear")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteYear(Long id){
        User admin = getUser() ; 
        return dashboardService.deleteYear(admin , id);   
    }  


    //=========================================================== Specialization================================================================================


    // views and fragments 
    @GET
    @Path("/specializations/specializationsView") 
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance specializationsManagementView(@QueryParam("yearId") Long yearId){ 
  
        return dashboardFragments.getFragment("specializations")
        .data("specializationsList", dashboardService.getAllSpecializations(getUser() , yearId))
        .data("yearsList", yearService.getYearsByCollegeCode(getUser().college.collegeCode));
    }

    @GET
    @Path("specializations/specializationsModal/spec")      
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getSpecializationsModal(@QueryParam("id") Long id){
        return id == null ? 
            dashboardFragments.getFragment("SpecializationModal")
                .data("spec", new Specialization())
                .data("yearsList", yearService.getYearsByCollegeCode(getUser().college.collegeCode)) : 
            dashboardFragments.getFragment("SpecializationModal")
                .data("spec", dashboardService.getSpecialization(id))
                .data("yearsList", yearService.getYearsByCollegeCode(getUser().college.collegeCode));
    }


    // logic 

    // add 
    @POST
    @Path("specializations/addSpecialization")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSpecialization(SpecializationDTO specDTO){ 
        User admin = getUser(); 
        return dashboardService.addSpecialization(admin , specDTO);
    } 

    // update 
    @POST
    @Path("specializations/updateSpecialization/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSpecialization(Long id, SpecializationDTO specDTO){
        User admin = getUser(); 
        return dashboardService.updateSpecialization(admin , id, specDTO);
    }

    // delete 
    @POST
    @Path("specializations/deleteSpecialization/{id}")
    public Response deleteSpecialization(@PathParam("id") Long id){
        User admin = getUser();
        return dashboardService.deleteSpecialization(admin , id);
    } 

    // ====================================================================== subjects ======================================================================
    @GET
    @Path("/subjects/subjectsView")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance subjectsManagementView(@QueryParam("specId") Long specId){
        return dashboardFragments.getFragment("subjects")
        .data("subjectsList", dashboardService.getAllSubjects(getUser()))
        .data("specsList", dashboardService.getAllSpecializationsByUserCollege(getUser()))
        .data("yearsList", yearService.getYearsByCollegeCode(getUser().college.collegeCode)); 
    } 
    
    @GET
    @Path("subjects/subjectsModal/subject")      
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getSubjectsModal(@QueryParam("id") Long id , @QueryParam("yearId") Long yearId){
        User user = getUser();
        return id == null ? 
            dashboardFragments.getFragment("SubjectModal")
                .data("subject", new Subject())
                .data("specsList", dashboardService.getAllSpecializationsByUserCollege(user))
                .data("yearsList", yearService.getYearsByCollegeCode(user.college.collegeCode)) : 

            dashboardFragments.getFragment("SubjectModal")
                .data("subject", subjectService.getSubjectById(id)) 
                .data("specsList", dashboardService.getAllSpecializationsByUserCollege(user))    
                // if yearid is null show all specs of the college , if not show the specs of that year 
                .data("specsList", yearId != null ? specializationService.getSpecializationsByYearID(yearId) : dashboardService.getAllSpecializationsByUserCollege(user)) 
                .data("yearsList", yearService.getYearsByCollegeCode(user.college.collegeCode));
    } 

    // logic 

    // add 
    @POST
    @Path("subjects/addSubject")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSubject(SubjectDTO subjectDTO){
        User admin = getUser();
        return dashboardService.addSubject(admin , subjectDTO);
    }

    // update 
    @POST
    @Path("subjects/updateSubject/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSubject(@PathParam("id") Long id, SubjectDTO subjectDTO){
        User admin = getUser();
        return dashboardService.updateSubject(admin , id, subjectDTO);
    }

    // delete 
    @POST
    @Path("subjects/deleteSubject/{id}")
    public Response deleteSubject(@PathParam("id") Long id){
        User admin = getUser();
        return dashboardService.deleteSubject(admin , id);
    }
    

    // ============================================================ categories ====================================================================== 

    // views and fragments  
    @GET 
    @Path("/categories/categoriesView") 
    @Produces(MediaType.TEXT_HTML) 
    public TemplateInstance categoriesManagementView(@QueryParam("subjectId") Long subjectId){ 
        User user = getUser();   
        return dashboardFragments.getFragment("categories")
            .data("categoriesList",  categoryService.getCategoriesBySubjectId(subjectId))
            .data("subjectsList", subjectService.getSubjectsByYearId(user.year.id))
            .data("selectedSubjectId", subjectId);
    }

    @GET
    @Path("categories/categoriesModal/category")      
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getCategoriesModal(@QueryParam("id") Long id, @QueryParam("subjectId") Long subjectId){
        User user = getUser();
        Category category = id == null ? new Category() : categoryService.getCategoryById(id);
        if (category == null) {
            category = new Category();
        }
        Long selectedId = id == null ? subjectId : (category.subject != null ? category.subject.id : null);

        return dashboardFragments.getFragment("CategoryModal")
            .data("category", category)
            .data("subjectsList", subjectService.getSubjectsByYearId(user.year.id))
            .data("selectedSubjectId", selectedId);
    }

    // logic 

    @POST
    @Path("categories/addCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCategory(CategoryDTO categoryDTO){
        User admin = getUser();
        return dashboardService.addCategory(admin , categoryDTO);
    }

    @POST
    @Path("categories/updateCategory/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategory(@PathParam("id") Long id, CategoryDTO categoryDTO){
        User admin = getUser();
        return dashboardService.updateCategory(admin , id, categoryDTO);
    }

    @POST
    @Path("categories/deleteCategory/{id}")
    public Response deleteCategory(@PathParam("id") Long id){
        User admin = getUser();
        return dashboardService.deleteCategory(admin , id);
    }

    // ============================================================ FILES & FOLDERS ======================================================================

    private TemplateInstance renderFilesView(Long subjectId, Long categoryId) { 
        // get the current admin 
        User user = getUser(); 
        // get all subjects for the current admin's college 
        List<Subject> subjects = subjectService.getSubjectsByCollegeId(user.college.id);
        // get all categories for the current subject if the subject id is provided
        List<Category> categories = subjectId != null ? categoryService.getCategoriesBySubjectId(subjectId) : List.of();
        
        // get the selected category if the category id is provided
        Category selectedCategory = null;
        if (categoryId != null) {
            selectedCategory = Category.findById(categoryId);
        }
        // get all folders for the current category if the category id is provided
        List<Folder> folders = categoryId != null ? dashboardService.getFoldersByCategoryId(user, categoryId) : List.of();
        List<File> files = categoryId != null ? dashboardService.getFilesByCategory(user, categoryId) : List.of();

        // so -> 
        // 1- select subject from the list , get the Categories of it or empty list  
        // 2- select category from the list , get the folders/files of it or empty list 


        // IDs will be sent for UX informations
        return dashboardFragments.getFragment("files")
            .data("subjectsList", subjects)
            .data("selectedSubjectId", subjectId)
            .data("categoriesList", categories)
            .data("selectedCategoryId", categoryId) 
            // if there as no category found from thew query -> empty name
            .data("selectedCategoryName", selectedCategory != null ? selectedCategory.name : "")
            .data("foldersList", folders)
            .data("filesList", files);
    }

    @GET
    @Path("/files/filesView")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance filesManagementView(@QueryParam("subjectId") Long subjectId, @QueryParam("categoryId") Long categoryId) {
        return renderFilesView(subjectId, categoryId);
    }

    @GET
    @Path("/files/folderModal")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getFolderModal(@QueryParam("id") Long id, @QueryParam("categoryId") Long categoryId) {
        Category category = Category.findById(categoryId);
        String categoryName = category != null ? category.name : "";
        Folder folder = null;
        if (id != null) {
            folder = Folder.findById(id);
        }
        if (folder == null) {
            folder = new Folder();
        }
        return dashboardFragments.getFragment("FolderModal")
            .data("folder", folder)
            .data("selectedCategoryId", categoryId)
            .data("selectedCategoryName", categoryName);
    }

    @GET
    @Path("/files/fileModal")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getFileModal(@QueryParam("id") Long id, @QueryParam("categoryId") Long categoryId) {
        Category category = Category.findById(categoryId);
        String categoryName = category != null ? category.name : "";
        List<Folder> folders = categoryId != null ? dashboardService.getFoldersByCategoryId(getUser(), categoryId) : List.of();
        
        File file = null;
        if (id != null) {
            file = File.findById(id);
        }
        if (file == null) {
            file = new File();
        }

        return dashboardFragments.getFragment("FileModal")
            .data("file", file)
            .data("selectedCategoryId", categoryId)
            .data("selectedCategoryName", categoryName)
            .data("foldersList", folders);
    }

    @POST
    @Path("/files/addFolder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response addFolder(FolderDTO folderDTO) {
        User admin = getUser();
        Response serviceResponse = dashboardService.addFolder(admin, folderDTO);
        if (serviceResponse.getStatus() != Response.Status.CREATED.getStatusCode() && serviceResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return serviceResponse; 
        }
        Category category = Category.findById(folderDTO.categoryId);
        Long subjectId = category != null && category.subject != null ? category.subject.id : null;
        return Response.ok("تم اضافة المجلد بنجاح").build();
    }

    @POST
    @Path("/files/updateFolder/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response updateFolder(@PathParam("id") Long id, @QueryParam("categoryId") Long categoryId, FolderDTO folderDTO) {
        User admin = getUser();
        Response serviceResponse = dashboardService.updateFolder(admin, id, folderDTO);
        if (serviceResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return serviceResponse;
        }
        Category category = Category.findById(categoryId);
        Long subjectId = category != null && category.subject != null ? category.subject.id : null;
        return Response.ok(renderFilesView(subjectId, categoryId)).build();
    }

    @POST
    @Path("/files/deleteFolder/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response deleteFolder(@PathParam("id") Long folderId, @QueryParam("categoryId") Long categoryId) {
        User admin = getUser();
        Response serviceResponse = dashboardService.deleteFolder(admin, folderId);
        if (serviceResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return serviceResponse;
        }
        Category category = Category.findById(categoryId);
        Long subjectId = category != null && category.subject != null ? category.subject.id : null;
        return Response.ok(renderFilesView(subjectId, categoryId)).build();
    }

    @POST
    @Path("/files/addFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML) 
    // form to take the file + the data 
    public Response addFile(FileUploadForm form) {
        User admin = getUser();
        Response serviceResponse = dashboardService.addFile(admin, form);

        if (serviceResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return serviceResponse;
        }
        
        return Response.ok("تم اضافة الملف بنجاح").build();
    }

    @POST
    @Path("/files/deleteFile/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response deleteFile(@PathParam("id") Long fileId, @QueryParam("categoryId") Long categoryId) {
        User admin = getUser();
        Response serviceResponse = dashboardService.deleteFile(admin, fileId);
        if (serviceResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return serviceResponse;
        }
        Category category = Category.findById(categoryId);
        Long subjectId = category != null && category.subject != null ? category.subject.id : null;
        return Response.ok(renderFilesView(subjectId, categoryId)).build();
    }

    @POST
    @Path("/files/updateFile/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response updateFile(@PathParam("id") Long id, @QueryParam("categoryId") Long categoryId, org.materialhub.dto.FileDTO fileDTO) {
        User admin = getUser();
        Response serviceResponse = dashboardService.updateFile(admin, id, fileDTO);
        if (serviceResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return serviceResponse;
        }
        Category category = Category.findById(categoryId);
        Long subjectId = category != null && category.subject != null ? category.subject.id : null;
        return Response.ok(renderFilesView(subjectId, categoryId)).build();
    }

}
