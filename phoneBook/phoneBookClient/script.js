//URL's used to communicate with backend
const baseUrl = "http://localhost:8080/";
const endpoint_SaveUser = "api/save-user/";
const endpoint_GetUsers = "api/get-users/";
const endpoint_SaveRole = "api/save-role/";
const endpoint_GetUserContacts = "api/get-contacts/";
const endpoint_AddContact = "api/add-contact-to-user/";
const endpoint_DeleteContact = "api/delete-contact?";
const endpoint_UpdateContact = "api/update-contact/";
const endpoint_GetContact = "api/search-for-contact/";

//id's of used DOM elements
const idForm_NewUser = "id-form-AddNewUser";
const idForm_Contact = "id-form-AddNewContact";
const idForm_Authentication = "id-form-Authentication";
const idInput_ItemsOnPage = "id-input-ItemsOnPageId";
const idSelect_AuthenticationLogin = "id-select-Authentication-Login";
const idInput_AuthenticationPassword = "id-input-Authentication-Password";
const idTable_Users = 'id-table-UsersList';
const idButton_NextPage = "id-button-NextPage";
const idButton_PrevPage = "id-button-PrevPage";
const idInput_Page = "id-input-Page";
const idSelect_SortType= "id-select-SortType";
const idInput_NameFilter = "id-input-NameFilter";
const idSection_Sort = "id-section-Sort";
const idSection_NavigationBar = "id-section-NavigationBar";
const idButton_DeleteContact = "id-button-DeleteContact";
const idButton_AddContact = "id-button-AddContact";
const idButton_UpdateContact = "id-button-UpdateContact";
const idInput_UpdateContact_OldName = "id-input-FormContact-oldName";
const idButton_RefreshContactList = "id-button-RefreshContactList";
const idTable_Contacts = "id-table-Contacts";
const idButton_SearchContact = "id-button-SearchForContact";


//DOM elements. assignment to variables done when DOMContentLoaded
var input_NumOfDisplayedContacts;
var formUser;
var formContact;
var formAuthentication;
var input_AuthenticationPassword;
var select_AuthenticationLogin;
var table_Users;
var button_NextPage;
var input_Page;
var select_SortType;
var input_NameFilter;
var section_Sort;
var section_NavigationBar;
var button_DeleteContact;
var button_AddContact;
var button_UpdateContact;
var input_UpdateContact_OldName;
var button_RefreshContactList;
var table_Contacts;
var button_SearchContact;

var indexStartDisp = 0, 
dispRange = 30;

//Setting up the "environment" when the page is loaded
document.addEventListener('DOMContentLoaded', function(){
    console.log("DOMContentLoaded...");

    //assigning DOM elements to variables
    input_NumOfDisplayedContacts = document.getElementById(idInput_ItemsOnPage);
    formUser = document.getElementById(idForm_NewUser);
    formContact = document.getElementById(idForm_Contact);
    formAuthentication = document.getElementById(idForm_Authentication);
    input_AuthenticationPassword = document.getElementById(idInput_AuthenticationPassword);
    select_AuthenticationLogin = document.getElementById(idSelect_AuthenticationLogin);
    table_Users = document.getElementById(idTable_Users);
    button_PrevPage = document.getElementById(idButton_PrevPage);
    button_NextPage = document.getElementById(idButton_NextPage);
    input_Page = document.getElementById(idInput_Page);
    select_SortType = document.getElementById(idSelect_SortType);
    input_NameFilter = document.getElementById(idInput_NameFilter);
    section_Sort = document.getElementById(idSection_Sort);
    section_NavigationBar = document.getElementById(idSection_NavigationBar);
    button_DeleteContact = document.getElementById(idButton_DeleteContact);
    button_AddContact = document.getElementById(idButton_AddContact);
    button_UpdateContact = document.getElementById(idButton_UpdateContact);
    input_UpdateContact_OldName = document.getElementById(idInput_UpdateContact_OldName);
    button_RefreshContactList = document.getElementById(idButton_RefreshContactList);
    table_Contacts = document.getElementById(idTable_Contacts);
    button_SearchContact = document.getElementById(idButton_SearchContact);

    //form's handling section
    formUser.onsubmit = function(e) {
        e.preventDefault();
        createNewUser();
    }

    formContact.onsubmit = function(e) {
        e.preventDefault();
    }

    formAuthentication.onsubmit = function(e){
        e.preventDefault();
        logIn();
    }

    //setting up the filters for values put in the input boxes
    setInputFilter(input_Page, function (value) {
        return /^[0-9]+$|^$/.test(value); // Allow digits only, using a RegExp
    });
    
    setInputFilter(input_NumOfDisplayedContacts, function (value) {
        return /^[0-9]+$|^$/.test(value); // Allow digits only, using a RegExp
    });

    //setting up initial attributes
    input_NumOfDisplayedContacts.setAttribute("value", dispRange);

    //seeting up initial styles
    section_Sort.style.display = "none";
    section_NavigationBar.style.display = "none";
    document.getElementById("id-section-Filter").style.display = "none";

    //adding event handlers
    input_NumOfDisplayedContacts.addEventListener("change", setItemsOnPage);
    input_Page.addEventListener("change", displayRequest);
    button_PrevPage.addEventListener("click", previousPage);
    button_NextPage.addEventListener("click", nextPage);
    select_SortType.addEventListener("change", sortContacts);
    input_NameFilter.addEventListener("change", activateFilters);
    button_DeleteContact.addEventListener("click", deleteContact);
    button_AddContact.addEventListener("click", createNewContact);
    button_UpdateContact.addEventListener("click", updateContact);
    button_RefreshContactList.addEventListener("click", displayContacts);
    button_SearchContact.addEventListener("click", searchContact);

    //invoke initial functions
    displayUsers(baseUrl + endpoint_GetUsers);

}, false);


function displayUsers(url){
    console.info("displayUsers() invoked");

    fetchQuery(url).then(function(resolved){
        var users=resolved;
        console.log(users);
        var option = document.createElement('option');
        var tHead = document.createElement('thead');

        select_AuthenticationLogin.innerHTML="";
        option.innerHTML = "";
        table_Users.innerHTML="";
        tHead.innerHTML= '<th>User name</th>' +
        '<th>Login</th>';  

        select_AuthenticationLogin.appendChild(option);
        table_Users.appendChild(tHead);
        
        users.forEach(user => {
            var option = document.createElement('option');
            option.innerHTML = user.login;
            option.value = user.login;
            select_AuthenticationLogin.appendChild(option);
    
            var tr = document.createElement('tr');
            tr.innerHTML='<td>' + user.userName + '</td>' +
            '<td>' + user.login + '</td>';
            table_Users.appendChild(tr);
        });
    })
}

function fillContactsTable(jsnObj){

    if (jsnObj.length) {
        let keys = Object.keys(jsnObj[0])
        //TODO: create table headers
        table_Contacts.innerHTML="";
        let tHeader = document.createElement("thead");
        keys.forEach(key => {
            let th = document.createElement('th');
            th.innerHTML = key;
            tHeader.appendChild(th);
        });
        table_Contacts.innerHTML="";
        table_Contacts.appendChild(tHeader);
        
        jsnObj.forEach(contact => {
            let tr = document.createElement('tr');
            keys.forEach(key => {
                let td = document.createElement('td');
                td.innerHTML = contact[key];
                tr.appendChild(td);
            })
            table_Contacts.appendChild(tr);
        });
    } else {
        table_Contacts.innerHTML = "AUTHENTICATION FAILED OR \nTHERE IS NO CONTACT IN THE DATABASE THAT MEETS THE QUERY";
    }
}

async function displayContacts(){
    console.info("displayContacts() invoked");

    fetchQuery(baseUrl+endpoint_GetUserContacts).then(resolved => {
        fillContactsTable(resolved);
    }, rejected => {
        fillContactsTable(null);
    });
}

async function searchContact(){
    console.info("searchContact() invoked");
    let url = new URL(baseUrl+endpoint_GetContact);
    let params = collectNonNullData(formContact);

    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]));

    fetchQuery(url).then(resolved => {
        fillContactsTable(resolved);
    });
}
function logIn(){
    console.info("logIn() invoked");

    var activUserLogin = getActivUser();
    let data = input_AuthenticationPassword.value;
    let jsonObj = JSON.stringify(data);
    console.log(jsonObj);
    postData(baseUrl + endpoint_GetUserContacts+activUserLogin, jsonObj);
    //TODO: actual display contact
}

function sortContacts(){
    console.info("sortContacts() invoked");

    //TODO: body of the function
}
function getActivUser(){
    console.info("getActivUser() invoked");
    
    return select_AuthenticationLogin.value;
}

function collectData(formId){
    console.info("collectData() invoked");

    var data = {};
    
    var form = document.getElementById(formId);

    console.info(form.length + " = form.length");
    for (var i = 0; i<form.length; i++){
        var input = form[i];
        if (input.name) {
            data[input.name] = input.value;
        }
    }
    return data;
}

function collectNonNullData(form){
    console.info("collectNonNullData() invoked");

    var data = {};
    for (var i = 0; i<form.length; i++){
        var input = form[i];
        if (input.name && input.value) {
            data[input.name] = input.value;
        }
    }
    return data;
}

function createNewUser(){
    console.info("createNewUser() invoked");

    var jsonObj = JSON.stringify(collectData(idForm_NewUser));
    postData(baseUrl+endpoint_SaveUser, jsonObj);
    displayUsers(baseUrl+endpoint_GetUsers);
}

function createNewContact(){
    console.info("createNewContact() invoked");

    var jsonObj = JSON.stringify(collectData(idForm_Contact));
    console.log(jsonObj);
    postData(baseUrl + endpoint_AddContact, jsonObj);
}

function deleteContact(){
    let query="";
    for (let index = 0; index < formContact.length; index++) {
        const element = formContact[index];
        if (element.name && element.value) {
            query+=element.name + "=" +element.value + "&&";
        }
    }
    deleteData(baseUrl + endpoint_DeleteContact + query, null);
}

function updateContact(){
    let query="";

    let data = collectData(idForm_Contact);
    let jsonObj = JSON.stringify(data);

    for (let index = 0; index < formContact.length; index++) {
        const element = formContact[index];
        if (element.name && element.value) {
            query+=element.name + "=" +element.value + "&&";
        }
    }

    putData(baseUrl+endpoint_UpdateContact+ input_UpdateContact_OldName.value, jsonObj);
}

function getData(url, jsnoObj){
    console.info("getData() invoked");

    // Sending and receiving data in JSON format using GET method
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("login", select_AuthenticationLogin.value);
    xhr.setRequestHeader("password", input_AuthenticationPassword.value);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4){
            var json = JSON.parse(xhr.responseText);
            if (xhr.status === 500){
                window.alert(json.message);
            } else if (xhr.status === 201) {
            console.log(json);
            }
        }};
    xhr.send(jsnoObj);

    return json;
}

function postData(url, jsnoObj){
    console.info("postData() invoked");

    // Sending and receiving data in JSON format using POST method
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("login", select_AuthenticationLogin.value);
    xhr.setRequestHeader("password", input_AuthenticationPassword.value);
    xhr.onreadystatechange = function () {

        if (xhr.readyState === 4){
            var json = JSON.parse(xhr.responseText);
            if (xhr.status === 500){
                window.alert(json.message);
            } else if (xhr.status === 201) {
            console.log(json);
            }
        }};
    xhr.send(jsnoObj);
}

function deleteData(url, jsnoObj){
    console.info("deleteData() invoked");

    // Sending and receiving data in JSON format using GET method
    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("login", select_AuthenticationLogin.value);
    xhr.setRequestHeader("password", input_AuthenticationPassword.value);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4){
            var json = JSON.parse(xhr.responseText);
            if (xhr.status === 500){
                window.alert(json.message);
            } else if (xhr.status === 201) {
            console.log(json);
            }
        }};
    xhr.send(jsnoObj);
}

function putData(url, jsnoObj){
    console.info("putData() invoked");

    // Sending and receiving data in JSON format using GET method
    var xhr = new XMLHttpRequest();
    xhr.open("PUT", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("login", select_AuthenticationLogin.value);
    xhr.setRequestHeader("password", input_AuthenticationPassword.value);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4){
            var json = JSON.parse(xhr.responseText);
            if (xhr.status === 500){
                window.alert(json.message);
            } else if (xhr.status === 201) {
            console.log(json);
            }
        }};
    xhr.send(jsnoObj);
}

//setting up the limit of items on single page
function setItemsOnPage(){    
    console.log("setItemsOnPage() invoked.");

    dispRange=Number(input_NumOfDisplayedContacts.value);

    //TODO: update following code
    displayCards(charactersArray, indexStartDisp, dispRange);
    dispNavigationData();
}

async function fetchQuery(query) {
    let myResponse, myObject;
    let headers = {"headers" : new Headers({'content-type' : 'application/json', 'login' : getActivUser(), 'password' : input_AuthenticationPassword.value})}

    myResponse = await fetch(query, headers);
    console.log("fetching: " + query);
    myObject = await myResponse.json();

    return myObject;
}

//TODO: modify function according current tast
function activateFilters() {
    console.info("activateFilters() invoked");

    let endPoint = "character/?";

    //name filtering:
    let filtName = document.getElementById("nameFilter").value;
    endPoint += filtName === "" ? "" : ("name=" + filtName);

    //status filtering:
    if (document.getElementById("status").value == 'alive') {
        endPoint += "&status=alive"
    } else if (document.getElementById("status").value == 'dead') {
        endPoint += "&status=dead"
    } else if (document.getElementById("status").value == 'unknown') {
        endPoint += "&status=unknown"
    } else if (document.getElementById("status").value == 'unchosen') {

    }

    //gender filtering: checkbox is unfortunate to use in this place
    //Rick and Morty API does not support filtering with multiple choice
    //the first checked box will be the one taken for filtering
    let genderEndPoint = "";

    genderEndPoint = document.getElementById("male").checked ? "&gender=male" : ""
    genderEndPoint = document.getElementById("female").checked ? "&gender=female" : ""
    genderEndPoint = document.getElementById("gender-unknown").checked ? "&gender=unknown" : ""
    genderEndPoint = document.getElementById("genderless").checked ? "&gender=genderless" : ""

    endPoint += genderEndPoint != "" ? genderEndPoint : "";

    console.log("I'm filtering via API with endpoint: " + endPoint);

    fillCharArray(endPoint);
}

function displayRequest() {
    console.info("displayRequest() invoked");

    displayCards(charactersArray, (document.getElementById("page").value - 1) * dispRange, dispRange)
    dispNavigationData();
}

function nextPage() {
    console.info("nextPage() invoked");

    let currPage = Number(document.getElementById("page").value);
    if (currPage == Math.ceil(charactersArray.length / dispRange)) {
        return;
    } else {
        document.getElementById("page").value = parseInt(currPage) + 1;
        displayRequest();
    }
}

function previousPage() {
    console.info("previousPage() invoked");

    let currPage = document.getElementById("page").value;

    if (currPage <= 1) {
        return;
    } else {
        document.getElementById("page").value = parseInt(currPage) - 1;
        displayRequest();
    }
}

//TODO: Is it useful??
//display - used to refresh the view everytime the content changes
function displayCards(locCharactersArray, startIndex, range) {
    console.info("displayCards(..) invoked");

    const charListContainer = document.querySelector(".charListContainer");
    charListContainer.innerHTML = "";

    document.getElementById("page").value =
        Math.ceil(startIndex / range + 1)

    for (i = startIndex; i < (startIndex + range) && i < locCharactersArray.length; i++) {

        charListContainer.innerHTML = charListContainer.innerHTML +
            '<div class="singleCharacter" id="charIdCard' + locCharactersArray[i].id + '" onclick="showDetails(' + locCharactersArray[i].id + ')">' +
            '<div class="charImageHolder">' +
            '<img src="' + locCharactersArray[i].image + '"/>' +
            '</div>' +
            '<div class="charDescriptionHolder">' +
            '<div class="charDescription">Name: ' + locCharactersArray[i].name + '</div>' +
            '<div class="charDescription">Status: ' + locCharactersArray[i].status + '</div>' +
            '<div class="charDescription">Gender: ' + locCharactersArray[i].gender + '</div>' +
            '<div class="charDescription">Species: ' + locCharactersArray[i].species + '</div>' +
            '</div>'
    }
}

//TODO: update function
function dispNavigationData(){
    console.info("displayNavigationData() invoked");

    document.getElementById("charsNumber").innerHTML = charactersArray.length;
    document.getElementById('pageId').innerHTML =
    "/" + Math.ceil(charactersArray.length / dispRange) + " stron";
}

//code taken from outsource (stackoverflow proposal)
// Restricts input for the given textbox to the given inputFilter function.
function setInputFilter(textbox, inputFilter) {
    ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop"].forEach(function (event) {
        textbox.addEventListener(event, function () {
            if (inputFilter(this.value)) {
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;
            } else if (this.hasOwnProperty("oldValue")) {
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                this.value = "";
            }
        });
    });
}