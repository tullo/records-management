/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_rm.test.legacy.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.test.util.BaseRMTestCase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.GUID;

/**
 * Records management security service test.
 *
 * @author Roy Wetherall
 */
public class ExtendedSecurityServiceImplTest extends BaseRMTestCase
{
    private NodeRef record;
    private NodeRef recordToo;
    private NodeRef moveRecordCategory;
    private NodeRef moveRecordFolder;

    @Override
    protected boolean isUserTest()
    {
        return true;
    }

    @Override
    protected void setupTestDataImpl()
    {
        super.setupTestDataImpl();

        record = utils.createRecord(rmFolder, "record.txt");
        recordToo = utils.createRecord(rmFolder, "recordToo.txt");

        moveRecordCategory = filePlanService.createRecordCategory(filePlan, "moveRecordCategory");
        moveRecordFolder = recordFolderService.createRecordFolder(moveRecordCategory, "moveRecordFolder");
    }

    private String createTestUser()
    {
        return doTestInTransaction(new Test<String>()
        {
            public String run()
            {
                String userName = GUID.generate();
                createPerson(userName);
                return userName;
            }
        }, AuthenticationUtil.getSystemUserName());
    }

    public void testExtendedSecurity()
    {
        final String monkey = createTestUser();
        final String elephant = createTestUser();
        final String snake = createTestUser();

        doTestInTransaction(new Test<Void>()
        {
            public Void run()
            {
                assertFalse(extendedSecurityService.hasExtendedSecurity(filePlan));
                assertFalse(extendedSecurityService.hasExtendedSecurity(rmContainer));
                assertFalse(extendedSecurityService.hasExtendedSecurity(rmFolder));
                assertFalse(extendedSecurityService.hasExtendedSecurity(record));

                assertNull(extendedSecurityService.getExtendedReaders(record));
                assertNull(extendedSecurityService.getExtendedWriters(record));

                Set<String> extendedReaders = new HashSet<String>(2);
                extendedReaders.add(monkey);
                extendedReaders.add(elephant);

                extendedSecurityService.addExtendedSecurity(record, extendedReaders, null);

                Map<String, Integer> testMap = new HashMap<String, Integer>(2);
                testMap.put(monkey, Integer.valueOf(1));
                testMap.put(elephant, Integer.valueOf(1));

                checkExtendedReaders(record, testMap);

                Set<String> extendedReadersToo = new HashSet<String>(2);
                extendedReadersToo.add(monkey);
                extendedReadersToo.add(snake);

                extendedSecurityService.addExtendedSecurity(recordToo, extendedReadersToo, null);

                Map<String, Integer> testMapToo = new HashMap<String, Integer>(2);
                testMapToo.put(monkey, Integer.valueOf(1));
                testMapToo.put(snake, Integer.valueOf(1));

                Map<String, Integer> testMapThree = new HashMap<String, Integer>(3);
                testMapThree.put(monkey, Integer.valueOf(2));
                testMapThree.put(elephant, Integer.valueOf(1));
                testMapThree.put(snake, Integer.valueOf(1));

                checkExtendedReaders(recordToo, testMapToo);

                // test remove (with no parent inheritance)

                Set<String> removeMap1 = new HashSet<String>(2);
                removeMap1.add(elephant);
                removeMap1.add(monkey);

                extendedSecurityService.removeExtendedSecurity(rmFolder, removeMap1, null, false);

                Map<String, Integer> testMapFour = new HashMap<String, Integer>(2);
                testMapFour.put(monkey, Integer.valueOf(1));
                testMapFour.put(snake, Integer.valueOf(1));

                checkExtendedReaders(recordToo, testMapToo);

                // test remove (apply to parents)

                Set<String> removeMap2 = new HashSet<String>(1);
                removeMap2.add(snake);

                extendedSecurityService.removeExtendedSecurity(recordToo, removeMap2, null, true);

                testMapThree.remove(snake);
                testMapFour.remove(snake);
                testMapToo.remove(snake);

                checkExtendedReaders(recordToo, testMapToo);

                return null;
            }
        });
    }

    public void testMove()
    {
        final String monkey = createTestUser();
        final String elephant = createTestUser();

        doTestInTransaction(new Test<Void>()
        {
            Map<String, Integer> testMap = new HashMap<String, Integer>(2);

            public Void run() throws Exception
            {
                testMap.put(monkey, Integer.valueOf(1));
                testMap.put(elephant, Integer.valueOf(1));

                assertFalse(extendedSecurityService.hasExtendedSecurity(filePlan));
                assertFalse(extendedSecurityService.hasExtendedSecurity(rmContainer));
                assertFalse(extendedSecurityService.hasExtendedSecurity(rmFolder));
                assertFalse(extendedSecurityService.hasExtendedSecurity(record));
                assertFalse(extendedSecurityService.hasExtendedSecurity(moveRecordCategory));
                assertFalse(extendedSecurityService.hasExtendedSecurity(moveRecordFolder));

                assertNull(extendedSecurityService.getExtendedReaders(record));

                Set<String> extendedReaders = new HashSet<String>(2);
                extendedReaders.add(monkey);
                extendedReaders.add(elephant);

                extendedSecurityService.addExtendedSecurity(record, extendedReaders, null);

                checkExtendedReaders(record, testMap);
                assertFalse(extendedSecurityService.hasExtendedSecurity(moveRecordCategory));
                assertFalse(extendedSecurityService.hasExtendedSecurity(moveRecordFolder));

                fileFolderService.move(record, moveRecordFolder, "movedRecord");

                return null;
            }

            @Override
            public void test(Void result) throws Exception
            {
                checkExtendedReaders(record, testMap);
            }
        });
    }


    @SuppressWarnings("unchecked")
    private void checkExtendedReaders(NodeRef nodeRef, Map<String, Integer> testMap)
    {
        assertTrue(extendedSecurityService.hasExtendedSecurity(nodeRef));

        Map<String, Integer> readersMap = (Map<String,Integer>)nodeService.getProperty(nodeRef, PROP_READERS);
        assertNotNull(readersMap);
        assertEquals(testMap.size(), readersMap.size());

        for (Map.Entry<String, Integer> entry: testMap.entrySet())
        {
            assertTrue(readersMap.containsKey(entry.getKey()));
            assertEquals(entry.getKey(), entry.getValue(), readersMap.get(entry.getKey()));

        }

        Set<String> readers = extendedSecurityService.getExtendedReaders(nodeRef);
        assertNotNull(readers);
        assertEquals(testMap.size(), readers.size());
    }

    public void testDifferentUsersDifferentPermissions()
    {
    	final String userNone = createTestUser();
    	final String userRead = createTestUser();
    	final String userWrite = createTestUser();
    	final String siteShortName = GUID.generate();

    	doTestInTransaction(new Test<Void>()
        {
            public Void run() throws Exception
            {
            	siteService.createSite(null, siteShortName, "test", "test", SiteVisibility.PRIVATE);
            	return null;
            }
        });

    	final NodeRef documentLibrary = doTestInTransaction(new Test<NodeRef>()
        {
            public NodeRef run() throws Exception
            {
            	siteService.setMembership(siteShortName, userRead, SiteModel.SITE_CONSUMER);
            	siteService.setMembership(siteShortName, userWrite, SiteModel.SITE_COLLABORATOR);
            	return siteService.createContainer(siteShortName, SiteService.DOCUMENT_LIBRARY, null, null);
            }
        });

    	final NodeRef record = doTestInTransaction(new Test<NodeRef>()
        {
            public NodeRef run() throws Exception
            {
            	NodeRef record = fileFolderService.create(documentLibrary, GUID.generate(), ContentModel.TYPE_CONTENT).getNodeRef();
            	recordService.createRecord(filePlan, record);
            	return record;
            }
        });

    	doTestInTransaction(new Test<Void>()
        {
            public Void run() throws Exception
            {
            	AuthenticationUtil.runAs(new RunAsWork<Void>()
            	{
					public Void doWork() throws Exception
					{
						// check permissions
		            	assertEquals(AccessStatus.DENIED, permissionService.hasPermission(record, READ_RECORDS));
		            	assertEquals(AccessStatus.DENIED, permissionService.hasPermission(record, FILING));
						return null;
					}
				}, userNone);

            	AuthenticationUtil.runAs(new RunAsWork<Void>()
            	{
					public Void doWork() throws Exception
					{
						// check permissions
		            	assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(record, READ_RECORDS));
		            	assertEquals(AccessStatus.DENIED, permissionService.hasPermission(record, FILING));
						return null;
					}
				}, userRead);

            	AuthenticationUtil.runAs(new RunAsWork<Void>()
            	{
					public Void doWork() throws Exception
					{
						// check permissions
		            	assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(record, READ_RECORDS));
		            	assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(record, FILING));
						return null;
					}
				}, userWrite);

            	AuthenticationUtil.runAs(new RunAsWork<Void>()
            	{
					public Void doWork() throws Exception
					{
						// check permissions
		            	assertEquals(AccessStatus.DENIED, permissionService.hasPermission(record, READ_RECORDS));
		            	assertEquals(AccessStatus.DENIED, permissionService.hasPermission(record, FILING));
						return null;
					}
				}, userNone);

            	AuthenticationUtil.runAs(new RunAsWork<Void>()
            	{
					public Void doWork() throws Exception
					{
						// check permissions
		            	assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(record, READ_RECORDS));
		            	assertEquals(AccessStatus.DENIED, permissionService.hasPermission(record, FILING));
						return null;
					}
				}, userRead);

            	AuthenticationUtil.runAs(new RunAsWork<Void>()
            	{
					public Void doWork() throws Exception
					{
						// check permissions
		            	assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(record, READ_RECORDS));
		            	assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(record, FILING));
						return null;
					}
				}, userWrite);

            	return null;
            }
        });
    }
}
