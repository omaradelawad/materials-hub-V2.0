package org.materialhub.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.materialhub.dto.FileDTO;
import org.materialhub.entities.Category;
import org.materialhub.entities.File;
import org.materialhub.entities.Folder;
import org.materialhub.entities.User;
import org.materialhub.enums.FilePath;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ApplicationScoped
public class FileService {

    @Inject
    CategoryService categoryService;

    @Inject
    S3Client s3;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".csv", ".pptx", ".doc", ".docx");
    // private static final String STORAGE_ROOT = "uploads";

    // اسم الـ bucket في S3/R2
    @ConfigProperty(name = "backet.name")   
    private String BUCKET ;

    public File getFileById(Long id) {
        return File.findById(id);
    } 

    private String getContentTypeFromExtension(String fileName) {
    String ext = getExtension(fileName).toLowerCase();
    return switch (ext) {
        case ".pdf" -> "application/pdf";
        case ".doc" -> "application/msword";
        case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        case ".ppt" -> "application/vnd.ms-powerpoint";
        case ".pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        case ".csv" -> "text/csv";
        case ".txt" -> "text/plain";
        case ".jpg", ".jpeg" -> "image/jpeg";
        case ".png" -> "image/png";
        default -> "application/octet-stream";
    };
}

    // =============================================
    // S3 CLOUD STORAGE METHODS
    // =============================================

    /**
     * رفع ملف إلى S3/R2
     * @param s3Key المسار الكامل للملف داخل الـ bucket (مثل: materials/college/year-1-5/file.pdf)
     * @param localFile الملف المحلي المؤقت اللي تم رفعه عن طريق الفورم
     * @return حجم الملف بالبايت
     * @throws IOException في حالة فشل قراءة الملف المحلي
     * @throws S3Exception في حالة فشل الرفع إلى S3
     */
    private long uploadToS3(String s3Key, Path localFile , String originalFileName) throws IOException, S3Exception {
        // تحديد الـ content type بناءً على امتداد الملف
        String contentType = Files.probeContentType(localFile);

        if (contentType == null || contentType.equals("application/octet-stream")) {
            contentType = getContentTypeFromExtension(originalFileName);
        } 

        long fileSize = Files.size(localFile);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(s3Key)
                .contentType(contentType)
                .contentLength(fileSize)
                .build();

        // رفع الملف إلى S3
        s3.putObject(putRequest, RequestBody.fromFile(localFile));

        return fileSize;
    }

    /**
     * حذف ملف من S3/R2
     * @param s3Key المسار الكامل للملف داخل الـ bucket
     * @throws S3Exception في حالة فشل الحذف من S3
     */
    private void deleteFromS3(String s3Key) throws S3Exception {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET)
                .key(s3Key)
                .build();

        s3.deleteObject(deleteRequest);
    }

    // =============================================
    // FILE CRUD - S3 CLOUD VERSION
    // =============================================

    @Transactional
    // TODO : remove the category.id , folderId : we can get them from the DTO (Long categoryId;) & (Long folderId;) in the fileDTO instead of passing them from here

    public Response addFile(FileDTO fileDTO, Category category, Folder folder, Path formFile, String originalFileName, User uploader) {
        // check the file info from the request form
        if (formFile == null || originalFileName == null || originalFileName.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("الملف المطلوب مفقود").build();
        }

        // checking the file type for security reasons
        String extension = getExtension(originalFileName);
        if (!isAllowedExtension(extension)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("نوع الملف غير مدعوم. يجب أن يكون pdf أو csv أو pptx أو doc أو docx").build();
        }

        // create a safe name for the file with a unique identifier
        String safeName = normalizeName(fileDTO.name) + "-" + UUID.randomUUID().toString();
        // merge the filename with its extension
        String fileName = safeName + extension;

        // generate the url base for the file
        String urlBase = FilePath.generateUploadPath
        (
                category.subject.year.college.collegeCode,
                category.subject.year.yearNumber, category.subject.year.id,
                category.subject.specialization.name, category.subject.specialization.id,
                category.subject.name, category.subject.id,
                category.name, category.id,
                folder != null ? folder.name : null, folder != null ? folder.id : null
        );

        // المسار الكامل للملف في S3 (الـ key)
        String s3Key = urlBase + fileName;

        // رفع الملف إلى S3/R2 بدلاً من الحفظ محلياً
        try {
            long fileSize = uploadToS3(s3Key, formFile , originalFileName);

            // create the file object
            File file = new File();
            file.name = fileDTO.name != null ? fileDTO.name : safeName;
            file.description = fileDTO.description != null ? fileDTO.description : "";
            file.category = category;
            file.folder = folder;
            file.year = category.subject.year;
            file.createdBy = uploader;
            file.size = fileSize;
            // الـ url يحفظ الـ S3 key عشان نقدر نحذفه أو نحمله بعدين
            file.url = s3Key;
            // save the file data in the database after uploading it to S3
            file.persist();
            return Response.ok(file).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("فشل قراءة الملف المرفوع").build();
        } catch (S3Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("فشل رفع الملف إلى السحابة: " + e.getMessage()).build();
        }
    }


    // =============================================
    // LOCAL STORAGE VERSION (commented out - kept for reference)
    // =============================================

    // @Transactional
    // public Response addFileLocal(FileDTO fileDTO, Category category, Folder folder, Path formFile, String originalFileName, User uploader) {
    //     if (formFile == null || originalFileName == null || originalFileName.isBlank()) {
    //         return Response.status(Response.Status.BAD_REQUEST).entity("الملف المطلوب مفقود").build();
    //     }
    //
    //     String extension = getExtension(originalFileName);
    //     if (!isAllowedExtension(extension)) {
    //         return Response.status(Response.Status.BAD_REQUEST).entity("نوع الملف غير مدعوم. يجب أن يكون pdf أو csv أو pptx أو doc أو docx").build();
    //     }
    //
    //     String safeName = normalizeName(fileDTO.name) + "-" + UUID.randomUUID().toString();
    //     String fileName = safeName + extension;
    //
    //     String urlBase = FilePath.generateUploadPath(
    //             category.subject.year.college.collegeCode,
    //             category.subject.year.yearNumber, category.subject.year.id,
    //             category.subject.specialization.name, category.subject.specialization.id,
    //             category.subject.name, category.subject.id,
    //             category.name, category.id,
    //             folder != null ? folder.name : null, folder != null ? folder.id : null
    //     );
    //
    //     Path targetDirectory = Paths.get(STORAGE_ROOT).resolve(urlBase);
    //     try {
    //         Files.createDirectories(targetDirectory);
    //         Path targetFile = targetDirectory.resolve(fileName);
    //         Files.copy(formFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
    //
    //         File file = new File();
    //         file.name = fileDTO.name != null ? fileDTO.name : safeName;
    //         file.description = fileDTO.description != null ? fileDTO.description : "";
    //         file.category = category;
    //         file.folder = folder;
    //         file.year = category.subject.year;
    //         file.createdBy = uploader;
    //         file.size = Files.size(targetFile);
    //         file.url = urlBase + fileName;
    //         file.persist();
    //         return Response.ok(file).build();
    //     } catch (IOException e) {
    //         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("فشل حفظ الملف محلياً").build();
    //     }
    // }


    public List<File> getFilesByCategory(Long categoryId) {
        return File.list("category.id = ?1", categoryId);
    }

    public List<File> getFilesByFolder(Long folderId) {
        return File.list("folder.id = ?1", folderId);
    }

    @Transactional
    public Response deleteFile(Long id) {
        File file = File.findById(id);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // حذف الملف من S3/R2 أولاً
        if (file.url != null && !file.url.isBlank()) {
            try {
                deleteFromS3(file.url);
            } catch (S3Exception e) {
                // لو فشل الحذف من S3 نسجل الخطأ بس نكمل حذف السجل من القاعدة
                // لأن الملف ممكن يكون اتحذف يدوياً أو ما يكون موجود أصلاً
                System.err.println("تحذير: فشل حذف الملف من S3 (key=" + file.url + "): " + e.getMessage());
            }
        }

        // حذف سجل الملف من قاعدة البيانات
        file.delete();
        return Response.ok().build();
    }

    // حذف محلي (مُعلق - للمرجعية)
    // @Transactional
    // public Response deleteFileLocal(Long id) {
    //     File file = File.findById(id);
    //     if (file == null) {
    //         return Response.status(Response.Status.NOT_FOUND).build();
    //     }
    //     if (file.url != null && !file.url.isBlank()) {
    //         try {
    //             Path storedFile = Paths.get(STORAGE_ROOT).resolve(file.url);
    //             Files.deleteIfExists(storedFile);
    //         } catch (IOException ignored) {
    //         }
    //     }
    //     file.delete();
    //     return Response.ok().build();
    // }

    @Transactional
    public void updateFileViews(Long id){
        File file = File.findById(id);
        if (file == null) {
            return;
        }
        file.views++;
    } 

    @Transactional
    public void updateFileDownloads(Long id){
        File file = File.findById(id);
        if (file == null) {
            return;
        }
        file.downloads++;
    }  

    @Transactional
    public Response updateFile(Long id, FileDTO fileDTO) {
        File file = File.findById(id);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        file.name = fileDTO.name;
        file.description = fileDTO.description;
        return Response.ok().entity(file).build();
    }

    private boolean isAllowedExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex >= 0 ? fileName.substring(dotIndex).toLowerCase() : "";
    }

    private String normalizeName(String value) {
        if (value == null) {
            return "";
        }
        return value
            .trim()
            .replaceAll("[^a-zA-Z0-9\\-_. \\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF]", "_")
            .replaceAll("[\\s]+", "_");
    }
}
