package com.example.linkup.utility;

import java.util.UUID;

public class FileNameUtils {
    public String generateUniqueFilename(String originalFilename) {
        // Extract the file extension from the original filename
        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = originalFilename.substring(lastDotIndex);
        }

        // Generate a UUID and append it to the filename
        String uniqueID = UUID.randomUUID().toString();
        return "image_" + uniqueID + fileExtension;
    }
}
