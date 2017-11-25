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
package org.wso2.security.tools.findsecbugs.scanner.scanner;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.security.tools.findsecbugs.scanner.Constants;
import org.wso2.security.tools.findsecbugs.scanner.NotificationManager;
import org.wso2.security.tools.findsecbugs.scanner.handler.FileHandler;
import org.wso2.security.tools.findsecbugs.scanner.handler.GitHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.Observable;

public class FindSecBugsScannerExecutor extends Observable implements Runnable {

    private static String productPath = Constants.DEFAULT_PRODUCT_PATH;
    private final Logger LOGGER = LoggerFactory.getLogger(FindSecBugsScannerExecutor.class);
    private boolean isFileUpload;
    private String zipFileName;
    private String gitUrl;
    private String gitUsername;
    private String gitPassword;


    public FindSecBugsScannerExecutor(boolean isFileUpload, String zipFileName, String gitUrl, String gitUsername,
                                      String gitPassword) {
        this.isFileUpload = isFileUpload;
        this.zipFileName = zipFileName;
        this.gitUrl = gitUrl;
        this.gitUsername = gitUsername;
        this.gitPassword = gitPassword;
    }

    public static String getProductPath() {
        return productPath;
    }

    private static void setProductPath(String productPath) {
        FindSecBugsScannerExecutor.productPath = productPath;
    }

    @Override
    public void run() {
        startScan();
        setChanged();
        notifyObservers(true);
    }

    private void startScan() {
        boolean isProductAvailable = false;
        if (isFileUpload) {
            String folderName;
            try {
                folderName = FileHandler.extractZipFile(Constants.DEFAULT_PRODUCT_PATH + File.separator +
                        zipFileName);
                FindSecBugsScannerExecutor.setProductPath(Constants.DEFAULT_PRODUCT_PATH + File.separator + folderName);
                isProductAvailable = true;
                NotificationManager.notifyFileExtracted(true);
            } catch (IOException e) {
                NotificationManager.notifyFileExtracted(false);
            }
        } else {
            File productFile = new File(Constants.DEFAULT_PRODUCT_PATH);
            Git git;
            if (productFile.exists() || productFile.mkdir()) {
                try {
                    git = GitHandler.gitClone(gitUrl, gitUsername, gitPassword, Constants.DEFAULT_PRODUCT_PATH);
                    isProductAvailable = GitHandler.hasAtLeastOneReference(git.getRepository());
                    NotificationManager.notifyProductCloned(true);
                } catch (GitAPIException e1) {
                    NotificationManager.notifyProductCloned(false);
                }
            }
        }
        if (isProductAvailable) {
            FindSecBugsScanner findSecBugsScanner = new FindSecBugsScanner();
            try {
                findSecBugsScanner.startScan();
                NotificationManager.notifyScanStatus("completed");
            } catch (MavenInvocationException | TransformerException | IOException | ParserConfigurationException |
                    SAXException e) {
                NotificationManager.notifyScanStatus("failed");
            }
        }
    }
}

