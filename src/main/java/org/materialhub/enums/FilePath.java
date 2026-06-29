package org.materialhub.enums;



public enum FilePath {

    MATERIALS("materials/"),
    THUMBNAILS("thumbnails/");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    } 


    // identifiers -> ["college" , "`year`" , "specialization" , "subject" , "category" , "fileId"] ,
    //  so /college/someCollege/year/someYear/specialization/someSpecialization/subject/someSubject/category/someCategory/fileId

    public String generatePath (String... subPaths) {
        StringBuilder fullPath = new StringBuilder(path);
        for (String subPath : subPaths) {
            fullPath.append(subPath).append("/");
        }
        return fullPath.toString();
    }

    public static String generateUploadPath(
        String collegeCode,
        int yearNum, Long yearId,
        String specName, Long specId,
        String subjectName, Long subjectId,
        String categoryName, Long categoryId,
        String folderName, Long folderId
    ) {
        StringBuilder sb = new StringBuilder(MATERIALS.path);
        sb.append(normalize(collegeCode)).append("/");
        sb.append("year-").append(yearNum).append("-").append(yearId).append("/");
        sb.append("spec-").append(normalize(specName)).append("-").append(specId).append("/");
        sb.append("subject-").append(normalize(subjectName)).append("-").append(subjectId).append("/");
        sb.append("category-").append(normalize(categoryName)).append("-").append(categoryId).append("/");
        if (folderName != null && folderId != null) {
            sb.append("folder-").append(normalize(folderName)).append("-").append(folderId).append("/");
        } 

        System.out.printf("--- DEBUG PATH ---%n Code: %s%n Spec: '%s'%n Subject: '%s'%n Category: '%s'%n", collegeCode, specName, subjectName, categoryName);
        return sb.toString();
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value
                .trim()
                .replaceAll("[^a-zA-Z0-9\\-_. \\u0600-\\u06FF\\u0750-\\u077F]", "_")
                .replaceAll("[\\s]+", "_");
    }

}
