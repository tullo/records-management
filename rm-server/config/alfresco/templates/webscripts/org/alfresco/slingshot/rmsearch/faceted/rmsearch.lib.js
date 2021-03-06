// RM Search Lib.

// Wrap the original document item method with our own one that appends RM specific properties.
// Additional properties will also need rendering in rmsearch.get.json.ftl.
var getOriginalDocumentItem = getDocumentItem,
   getOriginalRepositoryItem = getRepositoryItem;
getDocumentItem = function(siteId, containerId, pathParts, node, populate){
   // Get original Document item.
   var item = getOriginalDocumentItem(siteId, containerId, pathParts, node, populate);

   item.nodeJSON = appUtils.toJSON(node, true);

   return item;
};

getRepositoryItem = function(folderPath, node, populate){
   // Get Original Repo item
   var item = getOriginalRepositoryItem(folderPath, node, populate);

   if (item.type === "document") {
      item.nodeJSON = appUtils.toJSON(node, true);
   }

   return item;
};
