package org.wso2.security.scanners;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.wso2.security.Constants;
import org.wso2.security.handlers.FileHandler;
import org.wso2.security.handlers.MavenHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.zip.ZipOutputStream;

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

public class DependencyCheckScanner extends Observable implements Runnable {

    //Maven Commands
    private static final String MVN_COMMAND_DEPENDENCY_CHECK = "org.owasp:dependency-check-maven:check";


    @Override
    public void run() {
        try {
            startScan();
            setChanged();
            notifyObservers(true);
        } catch (IOException | MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    private void startScan() throws IOException, MavenInvocationException {
        MavenHandler.runMavenCommand(Constants.DEFAULT_PRODUCT_PATH + File.separator + Constants.POM_FILE, MVN_COMMAND_DEPENDENCY_CHECK);

        String reportsFolderPath = Constants.DEFAULT_PRODUCT_PATH + File.separator + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER;
        FileHandler.findFilesAndMoveToFolder(Constants.DEFAULT_PRODUCT_PATH, reportsFolderPath, Constants.DEPENDENCY_CHECK_REPORT);

        FileOutputStream fos = new FileOutputStream(reportsFolderPath + Constants.ZIP_FILE_EXTENSION);
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath + Constants.ZIP_FILE_EXTENSION));
        File fileToZip = new File(reportsFolderPath);

        FileHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }
}
