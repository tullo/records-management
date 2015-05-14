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
package org.alfresco.po.share.browse.documentlibrary;

import org.alfresco.po.rm.dialog.DeleteConfirmationDialog;
import org.alfresco.po.share.browse.ListItem;
import org.alfresco.po.share.details.document.DocumentDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Document list item
 * 
 * @author Roy Wetherall
 */
@Scope("prototype")
@Component
public class Document extends ListItem implements DocumentActions
{
    @Autowired
    private DocumentDetails documentDetails;
    
    @Autowired
    private DeleteConfirmationDialog deleteConfirmationDialog;

    /**
     * Click on declare as record action
     */
    public DocumentLibrary clickOnDeclareAsRecord()
    {
        return (DocumentLibrary)clickOnAction(ACTION_DECLARE_RECORD);
    }

    /**
     * Click on record details link
     */
    @Override
    public DocumentDetails clickOnLink()
    {
        return super.clickOnLink(documentDetails);
    }
    
    /**
     * Click on delete action
     */
    public DeleteConfirmationDialog clickOnDelete()
    {
    	return (DeleteConfirmationDialog)clickOnAction(DELETE, deleteConfirmationDialog);
    }

}
