﻿<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.rm.component.RMUsersAndGroups("usersAndGroups").setOptions({}).setMessages(${messages});
   //]]></script>

   <div id="usersAndGroups">
      <div class="yui-gb view-role-list">
         <div id="roleSelection" class="yui-u first">
            <div class="list-header">
               <h3>${msg('label.roles')}</h3>
            </div>
            <div id="roles">
               <ul>
               </ul>
            </div>
         </div>
         <div id="roleContentGroups" class="yui-u">
            <div class="list-header">
               <div class="deleteGroupButton">
                  <button id="deleteGroup" class="action">${msg('label.delete-group')}</button>
               </div>
               <div class="addGroupButton">
                  <button id="addGroup" class="action">${msg('label.add-group')}</button>
               </div>
               <div class="groups-header">${msg('label.groups')}</div>
            </div>
            <div class="roleGroups">
               <ul class="groups-list" id="groups-list">
               </ul>
            </div>
         </div>
         <div id="roleContentUsers" class="yui-u">
            <div class="list-header">
               <div class="deleteUserButton">
                  <button id="deleteUser" class="action">${msg('label.delete-user')}</button>
               </div>
               <div class="addUserButton">
                  <button id="addUser" class="action">${msg('label.add-user')}</button>
               </div>
               <div class="users-header">${msg('label.users')}</div>
            </div>
            <div class="roleUsers">
               <ul class="users-list" id="users-list">
               </ul>
            </div>
         </div>
      </div>
   </div>
</#if>