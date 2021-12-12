function getNodeRef(contextInfo) {
    var url = new URL(contextInfo.linkUrl);
    var nodeRef = url.searchParams.get("nodeRef");
    if (nodeRef != null) {
        return nodeRef;
    } else {
        return url.searchParams.get("recordRef");
    }
}

function onEcosCopyNodeRef(contextInfo, tab) {
    console.log("onEcosCopyNodeRef  " + contextInfo.menuItemId);
    if (contextInfo.menuItemId !== "EcosCopyNodeRef") {
        return;
    }
    var nodeRef = getNodeRef(contextInfo);
    chrome.scripting.executeScript({
        target: {tabId: tab.id},
        function: function (nodeRef) {
            navigator.clipboard.writeText(nodeRef);
        },
        args: [nodeRef]
    })
}

function onEcosOpenInNodeBorwser(contextInfo, tab) {
    console.log("onEcosOpenInNodeBorwser  " + contextInfo.menuItemId);
    if (contextInfo.menuItemId !== "EcosOpenInNodeBorwser") {
        return;
    }
    var nodeRef = getNodeRef(contextInfo);
    var url = new URL(contextInfo.linkUrl);
    var link = url.origin + "/share/page/console/admin-console/node-browser#state=panel" +
        encodeURIComponent("=view&nodeRef=" + nodeRef + "&search=" + nodeRef + "&lang=noderef&store=workspace%3A%2F%2FSpacesStore")
    chrome.tabs.create({url: link});
}

function onEcosOpenCard(contextInfo, tab) {
    console.log("onEcosOpenCard  " + contextInfo.menuItemId);
    if (contextInfo.menuItemId !== "EcosOpenCard") {
        return;
    }
    var nodeRef = getNodeRef(contextInfo);
    var url = new URL(contextInfo.linkUrl);
    var link = url.origin + "/share/page/card-details?&nodeRef=" + nodeRef;
    chrome.tabs.create({url: link});
}

function onEcosOpenCardOld(contextInfo, tab) {
    console.log("onEcosOpenCardOld  " + contextInfo.menuItemId);
    if (contextInfo.menuItemId !== "EcosOpenCardOld") {
        return;
    }
    var nodeRef = getNodeRef(contextInfo);
    var url = new URL(contextInfo.linkUrl);
    var link = url.origin + "/share/page/card-details?forceOld=true&nodeRef=" + nodeRef;
    chrome.tabs.create({url: link});
}


chrome.runtime.onInstalled.addListener(() => {

    chrome.contextMenus.create({
        title: "NodeRef",
        contexts: ["link"],
        "targetUrlPatterns": ["*://*/*recordRef=*", "*://*/*nodeRef=*"],
        id: "EcosNodeRef"
    });

    chrome.contextMenus.create({
            title: "Copy",
            parentId: "EcosNodeRef",
            contexts: ["link"],
            id: "EcosCopyNodeRef"
        }
    );

    chrome.contextMenus.create({
        title: "Open in Node browser",
        parentId: "EcosNodeRef",
        contexts: ["link"],
        id: "EcosOpenInNodeBorwser"
    });

    chrome.contextMenus.create({
        title: "Open card",
        parentId: "EcosNodeRef",
        contexts: ["link"],
        id: "EcosOpenCard"
    });

    chrome.contextMenus.create({
        title: "Open card (old)",
        parentId: "EcosNodeRef",
        contexts: ["link"],
        id: "EcosOpenCardOld"
    });

});

chrome.contextMenus.onClicked.addListener(onEcosCopyNodeRef);
chrome.contextMenus.onClicked.addListener(onEcosOpenInNodeBorwser);
chrome.contextMenus.onClicked.addListener(onEcosOpenCard);
chrome.contextMenus.onClicked.addListener(onEcosOpenCardOld);