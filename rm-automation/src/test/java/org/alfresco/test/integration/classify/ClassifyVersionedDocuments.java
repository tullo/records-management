/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.test.integration.classify;

import static org.junit.Assert.assertEquals;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.rm.dialog.classification.ClassifyContentDialog;
import org.alfresco.po.share.browse.documentlibrary.ContentBanner;
import org.alfresco.po.share.browse.documentlibrary.Document;
import org.alfresco.po.share.browse.documentlibrary.DocumentLibrary;
import org.alfresco.po.share.details.document.DocumentDetails;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.BaseTest;
import org.alfresco.test.DataPrepHelper;
import org.alfresco.test.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

/**
 * Tests for the interaction between versioning documents and classifying them.
 *
 * @author Tom Page
 * @since 2.4.a
 */
public class ClassifyVersionedDocuments extends BaseTest implements TestData
{
    private static final String SITE_NAME = "ClassifyVersionedDocuments";
    private static final String SITE_ID = "ClassifyVersionedDocuments" + System.currentTimeMillis();

    /* Page objects */
    @Autowired private DocumentLibrary documentLibrary;
    @Autowired private DocumentDetails documentDetails;
    @Autowired private ClassifyContentDialog classifyContentDialog;

    /* Data prep services */
    @Autowired private DataPrepHelper dataPrepHelper;
    @Autowired private SiteService siteService;
    @Autowired private ContentService contentService;

    /** Create a new collaboration site. */
    @Test
    (
        groups = { "integration" }
    )
    public void setUpSite() throws Exception
    {
        dataPrepHelper.createSite(SITE_NAME, SITE_ID);
    }

    /**
     * Classify a document, upload a new version and then revert to the first version. Check that the document is still
     * classified.
     */
    @Test
    (
        groups = { "integration" },
        description = "Verify classify action is available",
        dependsOnMethods = "setUpSite"
    )
    @AlfrescoTest(jira="RM-2606")
    public void revertToClassifiedVersion() throws Exception
    {
        final String documentName = "revertToClassifiedVersion Doc";

        openPage(documentLibrary, SITE_ID);
        createClassifiedDocument(documentName, SECRET_CLASSIFICATION_LEVEL_TEXT);

        openPage(documentLibrary, SITE_ID);
        documentLibrary.getDocument(documentName)
            .clickOnUploadNewVersion()
            .uploadFakeDocument();

        Document document = documentLibrary.getDocument(documentName);
        assertEquals("Expected the document to remain classified after new version is created.",
                    SECRET_CLASSIFICATION_LEVEL_TEXT.toUpperCase(),
                    document.getBannerText(ContentBanner.CLASSIFICATION));

        document.clickOnLink(documentDetails)
            .getVersionHistory()
            .revertToVersion("1.0")
            .clickOK();

        openPage(documentLibrary, SITE_ID);
        document = documentLibrary.getDocument(documentName);
        assertEquals("Expected the document to remain classified after reverting to version 1.0.",
                    SECRET_CLASSIFICATION_LEVEL_TEXT.toUpperCase(),
                    document.getBannerText(ContentBanner.CLASSIFICATION));
    }

    /** Create a classified document in the site. */
    private void createClassifiedDocument(String name, String level) throws Exception
    {
        // Upload document
        openPage(documentLibrary, SITE_ID);
        contentService.createDocument(getAdminName(), getAdminPassword(), SITE_ID, DocumentType.TEXT_PLAIN, name, TEST_CONTENT);

        // Classify document
        openPage(documentLibrary, SITE_ID);
        documentLibrary
            .getDocument(name)
            .clickOnClassify()
            .setLevel(level)
            .setClassifiedBy(CLASSIFIED_BY)
            .addReason(CLASSIFICATION_REASON)
            .clickOnClassify();
    }

    /** Remove the test site. */
    @AfterSuite(alwaysRun=true)
    public void tearDownReclassificationData() throws Exception
    {
        if (siteService.exists(SITE_ID, getAdminName(), getAdminPassword()))
        {
            siteService.delete(getAdminName(), getAdminPassword(), "", SITE_ID);
        }
    }
}