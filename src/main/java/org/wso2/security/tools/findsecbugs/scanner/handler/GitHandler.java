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
package org.wso2.security.tools.findsecbugs.scanner.handler;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utility methods for Git handling
 *
 * @author Deshani Geethika
 */
@SuppressWarnings({"unused"})
public class GitHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHandler.class);

    /**
     * Clone from GitHub and returns a {@link Git} object. If the URL is related to a private repository, GitHub account credentials should be given
     *
     * @param gitURL   GitHub URL to clone the product. By default master branch is cloned. If a specific branch or tag to be cloned, the specified URL for the branch or tag should be given
     * @param username GitHub user name if the product is in a private repository
     * @param password GitHub password if the product is in private repository
     * @param filePath Set directory to clone the product
     * @return {@link Git} object
     */
    public static Git gitClone(String gitURL, String username, String password, String filePath) {
        try {
            String branch = "master";
            if (gitURL.contains("/tree/")) {
                branch = gitURL.substring(gitURL.lastIndexOf("/") + 1);
                gitURL = gitURL.substring(0, gitURL.indexOf("/tree/"));
            }
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                    .setURI(gitURL)
                    .setDirectory(new File(filePath))
                    .setBranch(branch);

            if (username != null && password != null) {
                cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
            }
            return cloneCommand.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static boolean hasAtLeastOneReference(Repository repo) {
        for (Ref ref : repo.getAllRefs().values()) {
            if (ref.getObjectId() == null) {
                continue;
            }
            return true;
        }
        return false;
    }

    private static Ref gitCheckout(String tag, Git git) {
        try {
            return git.checkout().setName(tag).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static String gitDescribe(Git git) throws GitAPIException {
        return git.describe().call();
    }

    public static Git gitOpen(String productPath) throws IOException {
        return Git.open(new File(productPath));
    }

}
