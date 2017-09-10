package org.wso2.security;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wso2.security.scanners.DependencyCheckScanner;
import org.wso2.security.scanners.FindSecBugsScanner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

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
@EnableAutoConfiguration
@RequestMapping("staticScanner/")
public class UserAPI {

    private String returnMessage;

    @RequestMapping(value = "dependencyCheck", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheck() throws GitAPIException, MavenInvocationException, IOException {
        DependencyCheckScanner dependencyCheckScanner = new DependencyCheckScanner();
        if (new File(MainController.getProductPath()).exists()) {
            Observer dependencyCheckObserver = new Observer() {
                @Override
                public void update(java.util.Observable o, Object arg) {
                    if (new File(MainController.getProductPath() + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                        returnMessage = "Success";
                    } else {
                        returnMessage = "Scan Failed";
                    }
                }
            };
            dependencyCheckScanner.addObserver(dependencyCheckObserver);
            new Thread(dependencyCheckScanner).start();
            return returnMessage;
        }
        return "Product Not Found";
    }

    @RequestMapping(value = "findSecBugs", method = RequestMethod.GET)
    @ResponseBody
    public String runFindSecBugs() throws MavenInvocationException, IOException, ParserConfigurationException, SAXException, TransformerException, GitAPIException, URISyntaxException {
        FindSecBugsScanner findSecBugsScanner = new FindSecBugsScanner();
        if (new File(MainController.getProductPath()).exists()) {
            Observer findSecBugsObserver = new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if (new File(MainController.getProductPath() + Constants.FIND_SEC_BUGS_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                        returnMessage = "Success";
                    } else {
                        returnMessage = "Scan Failed";
                    }
                }
            };
            findSecBugsScanner.addObserver(findSecBugsObserver);
            new Thread(findSecBugsScanner).start();
            return returnMessage;
        }
        return "Product Not Found";
    }

    @RequestMapping(value = "cloneProduct", method = RequestMethod.GET)
    @ResponseBody //TODO: change mthd name, add method to checkout from tags
    public boolean cloneProduct(@RequestParam("gitUrl") String url, @RequestParam("branch") String branch, @RequestParam("replaceExisting") boolean replaceExisting) throws GitAPIException, IOException {
        return MainController.gitClone(url, branch, replaceExisting);

    }

    @RequestMapping(value = "uploadProductZipFile", method = RequestMethod.GET)
    @ResponseBody
    public boolean uploadProductZipFile(@RequestParam("zipFile") String zipFile, @RequestParam("replaceExisting") boolean replaceExisting) throws GitAPIException, IOException {
        MainController.uploadProductZipFile(zipFile, replaceExisting);
        return new File(MainController.getProductPath()).exists() && !new File(zipFile).exists();
    }

    @RequestMapping(value = "productPath", method = RequestMethod.GET)
    @ResponseBody
    public boolean config(@RequestParam("productPath") String productPath) {
        MainController.setProductPath(productPath);
        return MainController.getProductPath() != null;

    }

}

