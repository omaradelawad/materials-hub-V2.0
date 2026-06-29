package org.materialhub.services;

import java.util.List;
import org.materialhub.entities.Category;

import org.materialhub.dto.FolderDTO;
import org.materialhub.entities.Folder;

import org.materialhub.entities.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped 
public class FolderService { 

    @Inject CategoryService categoryService; 

    public Folder getFolderById(Long id) {
        return Folder.findById(id); 
    } 

    public List<Folder> getAllFolders() {
        return Folder.listAll(); 
    }   

    public List<Folder> getFoldersByCategoryId(Long categoryId) {
        return Folder.list("category.id", categoryId); 
    } 

    public Response createFolder(User admin , FolderDTO folder) {
        Category category = categoryService.getCategoryById(folder.categoryId); 
        
        if (category == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid category ID").build();
        } 

        Folder newFolder = new Folder(folder);  
        newFolder.createdBy = admin.name; 
        newFolder.category = category;  
        newFolder.persist();

        return Response.status(Response.Status.CREATED).entity("تم حفظ الجلد بنجاح").build();
    }

    @jakarta.transaction.Transactional
    public Response updateFolder(Long folderId , FolderDTO folderDTO) {

        Folder existingFolder = getFolderById(folderId); 
        if (existingFolder == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Folder not found").build();
        } 

        if (folderDTO.categoryId != null) {
            Category category = categoryService.getCategoryById(folderDTO.categoryId); 
            if (category == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid category ID").build();
            } 
            existingFolder.category = category; 
        }

        existingFolder.name = folderDTO.name; 
        existingFolder.description = folderDTO.description; 
        existingFolder.persist();

        return Response.ok("تم تحديث المجلد بنجاح").build();
    }  

    public Response deleteFolder(Long folderId) {

     Folder existingFolder = getFolderById(folderId); 
            if (existingFolder == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Folder not found").build();
            } 
    
            existingFolder.delete(); 
            return Response.ok("تم حذف الجلد بنجاح").build();
            
    }

    

}
    