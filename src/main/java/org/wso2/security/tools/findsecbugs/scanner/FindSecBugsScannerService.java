package org.wso2.security.tools.findsecbugs.scanner;/*
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

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wso2.security.tools.findsecbugs.scanner.scanners.MainScanner;
import org.wso2.security.tools.findsecbugs.scanner.handlers.FileHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Observer;
import java.util.zip.ZipOutputStream;

@Service
public class FindSecBugsScannerService {
    private final Logger LOGGER = LoggerFactory.getLogger(FindSecBugsScannerService.class);

    private boolean configureNotificationManager(String containerId, String automationManagerHost, int automationManagerPort) {
        NotificationManager.configure(containerId, automationManagerHost, automationManagerPort);
        return NotificationManager.isConfigured();
    }

    public String startScan(String automationManagerHost, int automationManagerPort, String containerId, boolean isFileUpload,
                            MultipartFile zipFile, String gitUrl, String gitUsername, String gitPassword) {

        String zipFileName = null;
        if (!configureNotificationManager(containerId, automationManagerHost, automationManagerPort)) {
            return "Notification manager is not configured";
        }
        if (isFileUpload) {
            if (zipFile == null || !zipFile.getOriginalFilename().endsWith(".zip")) {
                return "Please upload a zip file";
            } else {
                zipFileName = zipFile.getOriginalFilename();
                if (new File(Constants.DEFAULT_PRODUCT_PATH).exists() || new File(Constants.DEFAULT_PRODUCT_PATH).mkdir()) {
                    String fileUploadPath = Constants.DEFAULT_PRODUCT_PATH + File.separator + zipFileName;
                    if (FileHandler.uploadFile(zipFile, fileUploadPath)) {
                        LOGGER.info("File successfully uploaded");
                    } else {
                        return "Error occurred while uploading zip file";
                    }
                }
            }
        } else {
            if (gitUrl == null) {
                return "Please enter a URL and branch to perform clone operation";
            }
        }

        Observer mainScannerObserver = (o, arg) -> {
            if (new File(Constants.REPORTS_FOLDER_PATH + File.separator + Constants.FIND_SEC_BUGS_REPORTS_FOLDER).exists()) {
                LOGGER.info("FindSecBugs scanning completed");
                NotificationManager.notifyScanStatus("completed");
                NotificationManager.notifyReportReady(true);
                try {
                    LOGGER.info("Zipping the reports folder");
                    FileOutputStream fos = new FileOutputStream(Constants.REPORTS_FOLDER_PATH + Constants.ZIP_FILE_EXTENSION);
                    ZipOutputStream zipOut = new ZipOutputStream(fos);
                    File fileToZip = new File(Constants.REPORTS_FOLDER_PATH);

                    FileHandler.zipFolder(fileToZip, fileToZip.getName(), zipOut);
                    zipOut.close();
                    fos.close();

                    LOGGER.info("Report zip file ready");
                    NotificationManager.notifyReportReady(true);

                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error(e.getMessage());
                }
            } else {
                LOGGER.error("FindSecBugs scan failed");
            }
        };

        MainScanner mainScanner = new MainScanner(isFileUpload, zipFileName, gitUrl, gitUsername, gitPassword);
        mainScanner.addObserver(mainScannerObserver);
        new Thread(mainScanner).start();
        return "Ok";
    }

    public void getReport(HttpServletResponse response) {
        String reportsPath = Constants.REPORTS_FOLDER_PATH + Constants.ZIP_FILE_EXTENSION;

        if (new File(reportsPath).exists()) {
            try {
                InputStream inputStream = new FileInputStream(reportsPath);
                IOUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
                LOGGER.info("Successfully written to output stream");

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error(e.toString());
            }
        } else {
            LOGGER.error("Report is not found");
        }
    }
}
