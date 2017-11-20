package org.wso2.security.tools.findsecbugs.scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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
@Controller
@RequestMapping("findsecbugsScanner")
public class FindSecBugsScannerAPI {

    private final FindSecBugsScannerService findSecBugsScannerService;

    @Autowired
    public FindSecBugsScannerAPI(FindSecBugsScannerService findSecBugsScannerService) {
        this.findSecBugsScannerService = findSecBugsScannerService;
    }

    @GetMapping("isReady")
    @ResponseBody
    public boolean isReady() {
        return true;
    }

    @PostMapping("startScan")
    @ResponseBody
    public String startScan(@RequestParam String automationManagerHost,
                            @RequestParam int automationManagerPort,
                            @RequestParam String myContainerId,
                            @RequestParam boolean isFileUpload,
                            @RequestParam(required = false) MultipartFile zipFile,
                            @RequestParam(required = false) String gitUrl,
                            @RequestParam(required = false) String gitUsername,
                            @RequestParam(required = false) String gitPassword) {
        return findSecBugsScannerService.startScan(automationManagerHost, automationManagerPort, myContainerId, isFileUpload, zipFile, gitUrl, gitUsername, gitPassword);
    }

    @RequestMapping(value = "getReport", method = RequestMethod.GET, produces = "application/octet-stream")
    @ResponseBody
    public void getReport(HttpServletResponse response) {
        findSecBugsScannerService.getReport(response);
    }
}

