package org.wso2.security.staticscanner.handlers;


import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.wso2.security.staticscanner.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.*;

/*
*  Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
public class FileHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    public static void findFilesAndMoveToFolder(String sourcePath, String destinationPath, String fileName) {
        try {
            File dir = new File(destinationPath);
            if (dir.mkdir()) {
                Files.find(Paths.get(sourcePath), Integer.MAX_VALUE,
                        (filePath, fileAttr) -> filePath.getFileName().toString().equals(fileName)).forEach((f) -> {

                    File file = f.toFile();
                    String newFileName = file.getAbsolutePath().replace(sourcePath, Constants.NULL_STRING).replace(File.separator, Constants.UNDERSCORE);
                    File newFile = new File(destinationPath + File.separator + newFileName);
                    file.renameTo(newFile);
                    try {
                        FileUtils.copyFileToDirectory(newFile, dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFolder(File fileToZip, String fileName, ZipOutputStream zipOut) {
        try {
            if (fileToZip.isHidden()) {
                return;
            }
            if (fileToZip.isDirectory()) {
                File[] children = fileToZip.listFiles();
                for (File childFile : children) {
                    zipFolder(childFile, fileName + File.separator + childFile.getName(), zipOut);
                }
                return;
            }
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String extractZipFile(String zipFilePath) {
        try {
            int BUFFER = 2048;
            File file = new File(zipFilePath);
            ZipFile zip = new ZipFile(file);
            String newPath = file.getParent();
            String fileName = file.getName();
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(newPath, currentEntry);
                File destinationParent = destFile.getParentFile();
                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
                if (currentEntry.endsWith(Constants.ZIP_FILE_EXTENSION)) {
                    // found a zip file, try to open
                    extractZipFile(destFile.getAbsolutePath());
                }
            }
            //FileUtils.deleteDirectory(new File(zipFolder));
            return fileName.substring(0, fileName.length() - 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean uploadFile(MultipartFile file, String filePath) {
        try {
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            stream.write(bytes);
            stream.close();
            LOGGER.info("File successfully uploaded");
            if (new File(filePath).exists()) {
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("File is not uploaded" + e.toString());
        }
        return false;
    }
}
