package org.wso2.security.staticscanner.tests;/*
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.wso2.security.staticscanner.FindSecBugsScannerService;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StaticScannerServiceTest {

    @Autowired
    private StaticScannerService staticScannerService;

    @Test
    public void testStartScan() throws Exception {
        String automationManagerHost = "0.0.0.0";
        int automationManagerPort = 8080;
        String containerId = "testContainerId";
        boolean isFileUpload = false;
        MultipartFile zipFile = new MockMultipartFile("file.zip", new FileInputStream(new File("/home/deshani/Documents/wso2is-5.3.0.zip")));
        String url = "http://github.org/dessi";
        String branch = "master";
        String tag = null;
        boolean isFindSecBugs = true;
        boolean isDependencyCheck = true;

        assertEquals("Ok", staticScannerService.startScan(automationManagerHost, automationManagerPort, containerId, isFileUpload, zipFile, url, branch, tag, isFindSecBugs, isDependencyCheck));
    }
}
