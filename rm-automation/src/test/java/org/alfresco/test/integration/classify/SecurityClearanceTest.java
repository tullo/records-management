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

import org.alfresco.po.share.console.users.SecurityClearancePage;
import org.alfresco.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * UI Integration tests for the Security Clearance admin page.
 *
 * @author tpage
 * @since 3.0
 */
public class SecurityClearanceTest extends BaseTest
{
    @Autowired
    private SecurityClearancePage securityClearancePage;

    /**
     * Check the security clearance page loads and contains an ordered list of users and clearances.
     *
     * <pre>
     * Given that I am a compliance officer
     * When I view the admin tools UI
     * Then I can see the security clearance "tool" link under the Classification header
     *
     * Given that I am a compliance officer
     * When I click the "Security Clearance" link
     * Then the security clearance UI is displayed
     * And the user search filter is empty
     * And the first page of users are displayed ordered alphabetically
     * And their current clearance is displayed
     *
     * Given that I am a compliance officer
     * When I view the user security clearances
     * Then the current security clearance of each displayed user is shown as the default selection in a drop down against the user name in the list
     * </pre>
     */
    @Test
    (
        groups = { "integration" },
        description = "Check the security clearance page loads and contains an ordered list of users and clearances",
        dependsOnGroups = { "integration-dataSetup-rmSite" }
    )
    public void loadSecurityClearancePage()
    {

    }

    /**
     * Check the admin user is not found by using the filter.
     *
     * <pre>
     * Given that there is no filter set
     * When I start to type a filter
     * Then the users shown in the list automatically change to match the filter
     *
     * Given that there is not filter set
     * When I enter a filter that has no results
     * Then no results are shown in the list
     * And a message is shown in the list indicating no results where found
     * </pre>
     */
    @Test
    (
        groups = { "integration" },
        description = "Check the admin user is not found by using the filter",
        dependsOnGroups = { "integration-dataSetup-rmSite" }
    )
    public void adminIsNotShown()
    {

    }

    /**
     * Give a user clearance, reload the page and then revoke it again.
     *
     * <pre>
     * Given that there is no filter set
     * When I start to type a filter
     * Then the users shown in the list automatically change to match the filter
     *
     * Given that I am a compliance officer
     * When view the available options for a users security clearance level
     * Then I am presented with all the classification hierarchies and "no clearance"
     *
     * Given that I am a compliance officer
     * And a user has no clearance
     * Or any security clearance level
     * When I set their clearance level to something new
     * Then I am asked to confirm the change
     * And the new security clearance is show in the list
     * </pre>
     */
    @Test
    (
        groups = { "integration" },
        description = "Give a user clearance, reload the page and then revoke it again",
        dependsOnGroups = { "integration-dataSetup-rmSite", "integration-dataSetup-users" }
    )
    public void giveUserClearance()
    {

    }

    /*
    Acceptance criteria from RM-1842 which are not covered here.

    Paging through user list

    Given that the first page of users is currently being displayed
    When I page through the list
    Then the next page is loaded

    Ordering

    When a list of users are displayed
    Then they are ordered in ascending alphabetical order by default

    When I change the order to descending
    Then they are reordered accordingly

    Changing the filter

    Given that there is a filter set
    And results are shown
    When I change the filter
    Then the users and groups shown in the list automatically change to match the changed filter

    Removing the filter

    Given that there is a filter set
    And results are shown
    When I delete the filter
    Then the first page of all results is shown
    And the filter value prompts the user to enter a filter
     */
}
