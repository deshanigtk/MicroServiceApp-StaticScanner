package org.wso2.security.staticscanner.handlers;

import org.w3c.dom.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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

public class XMLHandler {

    //FindSecBugs plugin related
    private static final String BUILD_ELEMENT = "build";
    private static final String PLUGINS_ELEMENT = "plugins";
    private static final String PLUGIN_ELEMENT = "plugin";
    private static final String GROUP_ID_ELEMENT = "groupId";
    private static final String GROUP_ID_TEXT = "org.codehaus.mojo";
    private static final String ARTIFACT_ID_ELEMENT = "artifactId";
    private static final String ARTIFACT_ID_TEXT = "findbugs-maven-plugin";
    private static final String VERSION_ELEMENT = "version";
    private static final String VERSION_TEXT = "3.0.1";
    private static final String CONFIGURATION_ELEMENT = "configuration";
    private static final String EFFORT_ELEMENT = "effort";
    private static final String EFFORT_TEXT = "Max";
    private static final String THRESHOLD_ELEMENT = "threshold";
    private static final String THRESHOLD_TEXT = "Low";
    private static final String FAIL_ON_ERROR_ELEMENT = "failOnError";
    private static final String FAIL_ON_ERROR_TEXT = "true";
    private static final String INCLUDE_FILTER_FILE_ELEMENT = "includeFilterFile";
    private static final String INCLUDE_FILTER_FILE_TEXT = "${session.executionRootDirectory}/findbugs-security-include.xml";
    private static final String EXCLUDE_FILTER_FILE_ELEMENT = "excludeFilterFile";
    private static final String EXCLUDE_FILTER_FILE_TEXT = "${session.executionRootDirectory}/findbugs-security-exclude.xml";
    private static final String GROUP_ID_TEXT_2 = "com.h3xstream.findsecbugs";
    private static final String ARTIFACT_ID_TEXT_2 = "findsecbugs-plugin";
    private static final String VERSION_TEXT_2 = "LATEST";

    //FindSecBugs include, exclude files related
    private static final String FIND_BUGS_FILTER_ELEMENT = "FindBugsFilter";
    private static final String MATCH_ELEMENT = "match";
    private static final String BUG_ELEMENT = "Bug";
    private static final String CATEGORY_ATTRIBUTE = "category";
    private static final String CATEGORY_ATTRIBUTE_VALUE = "SECURITY";

    public static Document iterateNode(Node node, Document document) throws TransformerException {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                if (currentNode.getNodeName().equals(BUILD_ELEMENT)) {
                    appendFindSecBugsPlugin(document, (Element) currentNode);
                    break;
                }
                iterateNode(currentNode, document);
            }
        }
        return document;
    }

    private static void appendFindSecBugsPlugin(Document document, Element rootElement) throws TransformerException {
        //Get the <plugins> element that available under <build> element
        Element pluginsElement = (Element) rootElement.getElementsByTagName(PLUGINS_ELEMENT).item(0);

        //Create new element <plugin>
        Element pluginElement = document.createElement(PLUGIN_ELEMENT);

        //Create <groupId>org.codehaus.mojo</groupId> under <plugin>
        Element groupIdElement = document.createElement(GROUP_ID_ELEMENT);
        groupIdElement.appendChild(document.createTextNode(GROUP_ID_TEXT));

        //Create <artifactId>findbugs-maven-plugin</artifactId> under <plugin>
        Element artifactIdElement = document.createElement(ARTIFACT_ID_ELEMENT);
        artifactIdElement.appendChild(document.createTextNode(ARTIFACT_ID_TEXT));

        //Create <version>3.0.1</version> under <plugin>
        Element versionElement = document.createElement(VERSION_ELEMENT);
        versionElement.appendChild(document.createTextNode(VERSION_TEXT));

        //Create <configuration> under <plugin>
        Element configurationElement = document.createElement(CONFIGURATION_ELEMENT);
        pluginElement.appendChild(configurationElement);

        //Create <effort>Max</effort> under <configuration>
        Element effortElement = document.createElement(EFFORT_ELEMENT);
        effortElement.appendChild(document.createTextNode(EFFORT_TEXT));

        //Create <threshold>Low</threshold> under <configuration>
        Element thresholdElement = document.createElement(THRESHOLD_ELEMENT);
        thresholdElement.appendChild(document.createTextNode(THRESHOLD_TEXT));

        //Create <failOnError>true</failOnError> under <configuration>
        Element failOnErrorElement = document.createElement(FAIL_ON_ERROR_ELEMENT);
        failOnErrorElement.appendChild(document.createTextNode(FAIL_ON_ERROR_TEXT));

        //Create <includeFilterFile>${session.executionRootDirectory}/findbugs-security-include.xml</includeFilterFile> under <configuration>
        Element includeFilterFileElement = document.createElement(INCLUDE_FILTER_FILE_ELEMENT);
        includeFilterFileElement.appendChild(document.createTextNode(INCLUDE_FILTER_FILE_TEXT));

        //Create <excludeFilterFile>${session.executionRootDirectory}/findbugs-security-exclude.xml</excludeFilterFile> under <configuration>
        Element excludeFilterFileElement = document.createElement(EXCLUDE_FILTER_FILE_ELEMENT);
        excludeFilterFileElement.appendChild(document.createTextNode(EXCLUDE_FILTER_FILE_TEXT));

        //Create <plugins> under <configuration>
        Element pluginsElement2 = document.createElement(PLUGINS_ELEMENT);

        //Create <plugin> under <plugins>
        Element pluginElement2 = document.createElement(PLUGIN_ELEMENT);

        //Create <groupId>com.h3xstream.findsecbugs<groupId> under <plugin>
        Element groupIdElement2 = document.createElement(GROUP_ID_ELEMENT);
        groupIdElement2.appendChild(document.createTextNode(GROUP_ID_TEXT_2));

        //Create <artifactId>findsecbugs-plugin</artifactId> under <plugin>
        Element artifactIdElement2 = document.createElement(ARTIFACT_ID_ELEMENT);
        artifactIdElement2.appendChild(document.createTextNode(ARTIFACT_ID_TEXT_2));

        //Create <version>LATEST</version> under <plugin>
        Element versionElement2 = document.createElement(VERSION_ELEMENT);
        versionElement2.appendChild(document.createTextNode(VERSION_TEXT_2));

        pluginElement2.appendChild(groupIdElement2);
        pluginElement2.appendChild(artifactIdElement2);
        pluginElement2.appendChild(versionElement2);
        pluginsElement2.appendChild(pluginElement2);

        configurationElement.appendChild(effortElement);
        configurationElement.appendChild(thresholdElement);
        configurationElement.appendChild(failOnErrorElement);
        configurationElement.appendChild(includeFilterFileElement);
        configurationElement.appendChild(excludeFilterFileElement);
        configurationElement.appendChild(pluginsElement2);

        pluginElement.appendChild(groupIdElement);
        pluginElement.appendChild(artifactIdElement);
        pluginElement.appendChild(versionElement);
        pluginElement.appendChild(configurationElement);

        pluginsElement.appendChild(pluginElement);
    }

    public static void writeFindSecBugsExcludeFile(Document document) throws ParserConfigurationException {
        // create the root element
        Element rootEle = document.createElement(FIND_BUGS_FILTER_ELEMENT);
        document.appendChild(rootEle);
    }

    public static void writeFindSecBugsIncludeFile(Document document) throws ParserConfigurationException {
        Element rootEle = document.createElement(FIND_BUGS_FILTER_ELEMENT);
        Element matchElement = document.createElement(MATCH_ELEMENT);
        Element bugElement = document.createElement(BUG_ELEMENT);
        bugElement.setAttribute(CATEGORY_ATTRIBUTE, CATEGORY_ATTRIBUTE_VALUE);
        matchElement.appendChild(bugElement);
        rootEle.appendChild(matchElement);
        document.appendChild(rootEle);
    }

}