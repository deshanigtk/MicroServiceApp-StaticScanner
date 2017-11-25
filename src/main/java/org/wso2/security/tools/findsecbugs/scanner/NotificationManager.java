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
package org.wso2.security.tools.findsecbugs.scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class NotificationManager {

    private final static String NOTIFY = "automationManager/staticScanner/notify";
    private final static String FILE_UPLOADED = NOTIFY + "/fileUploaded";
    private final static String FILE_EXTRACTED = NOTIFY + "/fileExtracted";
    private final static String PRODUCT_CLONED = NOTIFY + "/productCloned";
    private final static String SCAN_STATUS = NOTIFY + "/scanStatus";
    private final static String REPORT_READY = NOTIFY + "/reportReady";
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManager.class);
    private static String myContainerId;
    private static String automationManagerHost;
    private static int automationManagerPort;

    public static void configure(String myContainerId, String automationManagerHost, int automationManagerPort) {
        NotificationManager.myContainerId = myContainerId;
        NotificationManager.automationManagerHost = automationManagerHost;
        NotificationManager.automationManagerPort = automationManagerPort;
    }

    private static void notifyStatus(String path, boolean status) {
        try {
            URI uri = (new URIBuilder()).setHost(automationManagerHost).setPort(automationManagerPort).setScheme
                    ("http").setPath(path)
                    .addParameter("containerId", myContainerId)
                    .addParameter("status", String.valueOf(status))
                    .build();
            LOGGER.info("Notifying status");
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(uri);

            HttpResponse response = httpClient.execute(get);
            if (response != null) {
                LOGGER.info("Notifying status response: " + response);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
        }
    }

    public static void notifyScanStatus(String status) {
        try {
            URI uri = (new URIBuilder()).setHost(automationManagerHost).setPort(automationManagerPort).setScheme
                    ("http").setPath(SCAN_STATUS)
                    .addParameter("containerId", myContainerId)
                    .addParameter("status", status)
                    .build();
            LOGGER.info("Notifying scan status: " + uri);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(uri);
            HttpResponse response = httpClient.execute(get);

            if (response != null) {
                LOGGER.info("Notifying scan status response: " + response);
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
        }
    }

    public static void notifyFileUploaded(boolean status) {
        notifyStatus(FILE_UPLOADED, status);
    }

    public static void notifyFileExtracted(boolean status) {
        notifyStatus(FILE_EXTRACTED, status);
    }

    public static void notifyProductCloned(boolean status) {
        notifyStatus(PRODUCT_CLONED, status);
    }

    public static void notifyReportReady(boolean status) {
        notifyStatus(REPORT_READY, status);
    }

    public static boolean isConfigured() {
        return automationManagerHost != null && automationManagerPort != 0 && myContainerId != null;
    }
}
