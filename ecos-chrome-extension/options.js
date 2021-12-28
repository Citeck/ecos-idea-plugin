
function save_options() {
    var testServers = document.getElementById("test_servers").value;
    var productiveServers = document.getElementById("productive_servers").value;
    chrome.storage.local.set({
        testServers: testServers,
        productiveServers: productiveServers
    }, function() {
    });
}


function restore_options() {
    console.log("restore");
    chrome.storage.local.get({
        testServers: "",
        productiveServers: ""
    }, function (items) {
        document.getElementById("test_servers").value = items.testServers;
        document.getElementById("productive_servers").value = items.productiveServers;
    });
}

document.addEventListener("DOMContentLoaded", restore_options);
document.getElementById("save").addEventListener("click", save_options);