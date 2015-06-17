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
package org.alfresco.po.share.page;

import org.alfresco.po.common.Page;
import org.alfresco.po.common.annotations.RenderableChild;
import org.alfresco.po.common.annotations.WaitFor;
import org.alfresco.po.common.annotations.WaitForStatus;
import org.alfresco.po.common.renderable.Renderable;
import org.alfresco.po.share.login.LoginPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Reporter;

/**
 * Share page implementation
 *
 * @author Tuna Aksoy
 * @since 2.2
 * @version 1.0
 */
public abstract class SharePage extends Page
{
    /** A lock used when accessing the {@link #currentLoggedInUser}. */
    public static final String CURRENT_LOGGED_IN_USER_LOCK = new String("CURRENT_LOGGED_IN_USER_LOCK");
    /**
     * The currently logged-in user. This is only suitable for testing against a single client, if we want to support
     * testing against a Selenium grid then we need to add some code to track which user is logged in on which client.
     */
    protected static String currentLoggedInUser;

    /** page footer */
    @WaitFor(status=WaitForStatus.VISIBLE)
    @FindBy(css="div.sticky-footer")
    private WebElement footer;

    /** share page navigation */
    @Autowired
    @RenderableChild
    private SharePageNavigation sharePageNavigation;

    /** login page */
    @Autowired
    private LoginPage loginPage;

    @Autowired
    private Message message;

    /**
     * Get share page navigation
     */
    public SharePageNavigation getSharePageNavigation()
    {
        return sharePageNavigation;
    }

    /**
     * @see org.alfresco.po.common.Page#render()
     */
    @Override
    public <T extends Renderable> T render()
    {
        if (!(getLastRendered() instanceof SharePage))
        {
            message.waitUntillHidden();
        }
        return super.render();
    }

    /**
     * Get the URL of the page
     */
    protected abstract String getPageURL(String ... context);

    /**
     * Open this page, login to Share if required.
     *
     * @param server        base server URL
     * @param userName      user name
     * @param password      password
     * @param context       context
     * @return {@link SharePage} rendered page
     */
    public SharePage open(String server, String userName, String password, String ... context)
    {
        // get the page URL
        String url = server + getPageURL(context);

        // This addresses the race condition of two threads logging out/in at the same time. It's a little bit pointless
        // as we only support testing against a single client anyway.
        synchronized(CURRENT_LOGGED_IN_USER_LOCK)
        {
            Reporter.log("Request to open page as "+userName+ " ("+password+"), previously "+currentLoggedInUser);
            // Logout if previously logged in as someone else.
            if (currentLoggedInUser != null && !userName.equals(currentLoggedInUser))
            {
                sharePageNavigation.openUserDropdownMenu().logout();
                currentLoggedInUser = null;
                Reporter.log("Successfully logged out");
            }

            Reporter.log("Opening "+url);

            // open the page
            webDriver.get(url);

            Reporter.log("Opened "+url+ ". Title is '"+webDriver.getTitle()+"'");

            // if redirected to the login page
            if (webDriver.getTitle().contains("Login"))
            {
                Reporter.log("Logging in as "+userName);
                // login
                loginPage.render();
                loginPage
                    .setUsername(userName)
                    .setPassword(password)
                    .clickOnLoginButton();
                currentLoggedInUser = userName;
                Reporter.log("Logged in as "+userName+". New title is '"+webDriver.getTitle()+"'");
            }
        }
        Reporter.log("About to render page (currentlyLoggedInUser is now "+currentLoggedInUser+")");

        return this.render();
    }
}
