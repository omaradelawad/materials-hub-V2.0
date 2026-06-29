// package org.materialhub.controllers;

// import jakarta.ws.rs.GET;
// import jakarta.ws.rs.Path;
// import jakarta.ws.rs.PathParam;
// import jakarta.ws.rs.core.MediaType;
// import jakarta.ws.rs.core.Response;
// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Files;

// @Path("/materials")
// public class MaterialsController {

//     private static final String STORAGE_ROOT = "uploads";

//     @GET
//     @Path("/{path: .*}")
//     public Response serveFile(@PathParam("path") String path) {
//         if (path == null || path.isBlank()) {
//             return Response.status(Response.Status.NOT_FOUND).build();
//         }

//         // Clean path to prevent path traversal vulnerability (LFI)
//         String cleanPath = path.replace("..", "").replace("//", "/");
//         java.nio.file.Path fileRoot = java.nio.file.Paths.get(STORAGE_ROOT, "materials");
//         java.nio.file.Path targetFile = fileRoot.resolve(cleanPath).normalize();

//         // Security check: ensure targetFile resides inside the fileRoot directory
//         if (!targetFile.startsWith(fileRoot)) {
//             return Response.status(Response.Status.FORBIDDEN).entity("غير مصرح بالوصول لهذا المسار").build();
//         }

//         File file = targetFile.toFile();
//         if (!file.exists() || !file.isFile()) {
//             return Response.status(Response.Status.NOT_FOUND).entity("الملف غير موجود").build();
//         }

//         try {
//             String contentType = Files.probeContentType(targetFile);
//             if (contentType == null) {
//                 // fallback content types
//                 if (path.endsWith(".pdf")) {
//                     contentType = "application/pdf";
//                 } else if (path.endsWith(".csv")) {
//                     contentType = "text/csv";
//                 } else if (path.endsWith(".pptx")) {
//                     contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
//                 } else if (path.endsWith(".docx")) {
//                     contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
//                 } else if (path.endsWith(".doc")) {
//                     contentType = "application/msword";
//                 } else {
//                     contentType = MediaType.APPLICATION_OCTET_STREAM;
//                 }
//             }

//             return Response.ok(file)
//                     .header("Content-Type", contentType)
//                     .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
//                     .build();
//         } catch (IOException e) {
//             return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("خطأ في قراءة الملف").build();
//         }
//     }
// }
