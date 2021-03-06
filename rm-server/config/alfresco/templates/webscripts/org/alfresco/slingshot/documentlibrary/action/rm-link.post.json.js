<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/action/action.lib.js">

/**
 * Add multiple files as children action
 * @method POST
 */

/**
 * Entrypoint required by action.lib.js
 *
 * @method runAction
 * @param p_params {object} Object literal containing files array
 * @return {object|null} object representation of action results
 */
function runAction(p_params)
{
   var results = [],
      destNode = p_params.destNode,
      files = p_params.files,
      file, fileNode, result, nodeRef;

   // Must have array of files
   if (!files || files.length == 0)
   {
      status.setCode(status.STATUS_BAD_REQUEST, "No files.");
      return;
   }

   for (file in files)
   {
      nodeRef = files[file];
      result =
      {
         nodeRef: nodeRef,
         action: "addChild",
         success: false
      }

      try
      {
         fileNode = search.findNode(nodeRef);
         if (fileNode === null)
         {
            result.id = file;
            result.nodeRef = nodeRef;
            result.success = false;
         }
         if (!rmService.getRecordsManagementNode(destNode).hasCapability("FillingPermissionOnly"))
         {
            result.name = fileNode.name;
            result.error = "You don't have filing permission on the destination or the destination is either frozen, closed or cut off!";
            results.push(result);
            continue;
         }
         else
         {
            result.id = fileNode.name;
            result.name = fileNode.name;
            result.type = fileNode.isContainer ? "folder" : "document";
            destNode.addNode(fileNode);
            result.success = true;
         }
      }
      catch (e)
      {
         result.id = file;
         result.nodeRef = nodeRef;
         result.success = false;
         result.error = e.message;

         // log the error
         logger.error(e.message);
      }

      results.push(result);
   }

   return results;
}

/* Bootstrap action script */
main();
