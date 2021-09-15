const baseUrl = "http://localhost:8080/";
const saveUserEndpoint = "api/save-user/";
const getUsersEndpoint = "api/get-users/";
const saveRoleEndpoint = "api/save-role/";
const getUserContactsEndpoint = "api/get-contacts/";
const addContactEndpoint = "api/add-contact-to-user/";

var charactersArray, 
indexStartDisp = 0, 
dispRange = 30;

displayUsers(getUsersEndpoint);
//displayContacts();

function displayUsers(url){

    fetchQuery(url).then(function(resolved){
        var users=resolved;
        console.log(users);
        var selector = document.getElementById('selectUser')
        selector.innerHTML="";
        var option = document.createElement('option');
        option.innerHTML = "";
        selector.appendChild(option);

        var table = document.getElementById('usersList');
        table.innerHTML="";

        var tHead = document.createElement('thead');
        tHead.innerHTML= '<th>User name</th>' +
        '<th>Login</th>';  
        table.appendChild(tHead);
        
        users.forEach(user => {
            var option = document.createElement('option');
            option.innerHTML = user.login;
            option.value = user.login;
            selector.appendChild(option);
    
            var tr = document.createElement('tr');
            tr.innerHTML='<td>' + user.userName + '</td>' +
            '<td>' + user.login + '</td>';
            table.appendChild(tr);
        });
    })
}

function displayContacts(){
    var activUserLogin = getActivUser();
    getData(baseUrl + getUserContactsEndpoint+activUserLogin, null);
}

function getActivUser(){
    return document.getElementById("selectUser").value;
}

function collectData(formId){
    var data = {};
    var form = document.getElementById(formId);
    for (var i = 0; i<form.length; i++){
        var input = form[i];
        if (input.name) {
            data[input.name] = input.value;
        }
    }
    return data;
}
function postUser(){
    var jsonObj = JSON.stringify(collectData("formAddNewUser"));
    postData(baseUrl+saveUserEndpoint, jsonObj);
    displayUsers(getUsersEndpoint);
}

function postContact(){
    let userInfo={'userLogin': getActivUser()};
    let temp = "/?userLogin=LesioLogin&name=&surname=&phoneHome=&phoneWork=&email=&photo=";
    data = {...userInfo, ...collectData("formAddNewContact")};
    console.log(data);
    var jsonObj = JSON.stringify(collectData("formAddNewContact"));
    console.log(jsonObj);
    postData(baseUrl + addContactEndpoint + getActivUser(), jsonObj);
}

function getData(url, jsnoObj){
    // Sending and receiving data in JSON format using POST method
    var xhr = new XMLHttpRequest();
    console.log("initial xhr.readyState: "+xhr.readyState + "\n" +
    "xhr.status: "+ xhr.status);
    xhr.open("GET", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    console.log("open post request, xhr.readyState: " + xhr.readyState + "\n" +
    "xhr.status: "+ xhr.status);
    xhr.onreadystatechange = function () {
        console.log("xhr.readyState inside function: "+xhr.readyState + "\n" +
        "xhr.status: "+ xhr.status);

        if (xhr.readyState === 4){
            console.log("readyState === 4");
            if (xhr.status === 500){
                var json = JSON.parse(xhr.responseText);
                window.alert(json.message);
            } else if (xhr.status === 201) {
            var json = JSON.parse(xhr.responseText);
            console.log(json);
            }
        }};
    console.log("just about to send 'data'");
    xhr.send(jsnoObj);
}

function postData(url, jsnoObj){
    // Sending and receiving data in JSON format using POST method
    var xhr = new XMLHttpRequest();
    console.log("initial xhr.readyState: "+xhr.readyState + "\n" +
    "xhr.status: "+ xhr.status);
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    console.log("open post request, xhr.readyState: " + xhr.readyState + "\n" +
    "xhr.status: "+ xhr.status);
    xhr.onreadystatechange = function () {
        console.log("xhr.readyState inside function: "+xhr.readyState + "\n" +
        "xhr.status: "+ xhr.status);

        if (xhr.readyState === 4){
            console.log("readyState === 4");
            if (xhr.status === 500){
                var json = JSON.parse(xhr.responseText);
                window.alert(json.message);
            } else if (xhr.status === 201) {
            var json = JSON.parse(xhr.responseText);
            console.log(json);
            }
        }};
    console.log("just about to send 'data'");
    xhr.send(jsnoObj);
};

function appendCharArray (query){
    arrWithChars(charactersArray, query).then(function(resolved) {
        charactersArray=charactersArray.concat(resolved);
        dispNavigationData();
    })    
}

function fillCharArray (query){
    charactersArray=[];
    arrWithChars(charactersArray, query).then(function(resolved) {
        charactersArray=resolved;
        dispNavigationData();
    })   
}

//Setting up the event dependencies when the page is loaded
document.addEventListener('DOMContentLoaded', function(){
    console.log("I'm in DOMContentLoaded function.");
    //dispNavigationData();

    //adding attributes to DOMelements
    let formUser = document.getElementById("formAddNewUser");
    formUser.onsubmit = function(e) {
        e.preventDefault();
        postUser();
    }

    let formContact = document.getElementById("formAddNewContact");
    formContact.onsubmit = function(e) {
        e.preventDefault();
        postContact();
    }

    //setting up the filters for values put in the input boxes
    setInputFilter(document.getElementById("page"), function (value) {
        return /^\d+$/.test(value); // Allow digits only, using a RegExp
    });
    
    setInputFilter(document.getElementById("itemsOnPageId"), function (value) {
        return /^\d+$/.test(value); // Allow digits only, using a RegExp
    });

    //filling up the base values
    document.getElementById("itemsOnPageId").setAttribute("value", dispRange);

    //adding event handlers
    //document.getElementById("buttonAddUser").addEventListener("click", postUser);
    //document.getElementById("formAddNewUser").addEventListener("submit", postUser);
    document.getElementById("itemsOnPageId").addEventListener("change", setCharsOnPage);
    document.getElementById("page").addEventListener("change", displayRequest);
    document.getElementById("sortType").addEventListener("change", sortArr);
    document.getElementById("dispChangeButton").addEventListener("click", dispChange);
    document.getElementById("nameFilter").addEventListener("change", activateFilters)
    document.getElementById("prevPageButtonId").addEventListener("click", previousPage)
    document.getElementById("nextPageButtonId").addEventListener("click", nextPage)

}, false);

//setting up the limit of characters on single page
function setCharsOnPage(){    
    console.log("I'm in setCharsOnPage() function.");
    dispRange=Number(document.getElementById("itemsOnPageId").value);
    displayCards(charactersArray, indexStartDisp, dispRange);
    dispNavigationData();
}

//returns the Array of characters with endpoint "userQuery"
async function arrWithChars(oldCharactersArray, userQuery) {
    let dispReady = false,
    myObject,
    newCharactersArray=[];

    try {
        do {
            myObject = await fetchQuery(userQuery)
            if (myObject.results) {
                newCharactersArray = newCharactersArray.concat(myObject.results);
            }
            try {
                userQuery = myObject.info.next;
            } catch (error) {
                window.alert("Brak postaci do wyświetlenia\nSerwer nie odpowieada lub Zmień filtry");

                oldCharactersArray=oldCharactersArray.concat(newCharactersArray);
                newCharactersArray=[];
                displayCards(oldCharactersArray, indexStartDisp, dispRange);
                dispReady = true;
                break;
            }
            if (!dispReady && (userQuery === null || ((oldCharactersArray.length+newCharactersArray.length) >= (indexStartDisp + dispRange)))) {
                oldCharactersArray=oldCharactersArray.concat(newCharactersArray);
                newCharactersArray=[];
                displayCards(oldCharactersArray, indexStartDisp, dispRange);
                dispReady = true;
            }
        } while (userQuery != null)
    } catch (error) {
        window.alert(error);
        console.log(error);
        displayCards(oldCharactersArray, indexStartDisp, dispRange);
    }
    return oldCharactersArray.concat(newCharactersArray);
}

async function fetchQuery(query) {
    let myResponse, myObject;

    if (query.includes(baseUrl)) {
        myResponse = await fetch(query);
        console.log("fetching: " + query);

    } else {
        myResponse = await fetch(baseUrl + query);
        console.log("fetching: " + baseUrl + query);
    }
    myObject = await myResponse.json();

    return myObject;
}

function activateFilters() {
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

function sortByName() {
    charactersArray.sort(function (a, b) { return a.name.localeCompare(b.name) });
    displayCards(charactersArray, 0, dispRange);
    dispNavigationData();
}

function sortById() {
    charactersArray.sort(function (a, b) { return a.id - b.id })
    displayCards(charactersArray, 0, dispRange)
    dispNavigationData();
}

function sortByDate() {
    charactersArray.sort(function (a, b) {
        return (new Date(a.created) - new Date(b.created))
    });
    displayCards(charactersArray, 0, dispRange);
    dispNavigationData();
}

function sortByGender() {
    charactersArray.sort(function (a, b) { return a.gender.localeCompare(b.gender) });
    displayCards(charactersArray, 0, dispRange)
    dispNavigationData();
}

function sortArr() {
    let chosenType = document.getElementById("sortType").value;

    if (chosenType == "byId") {
        sortById();
    } else if (chosenType == "byName") {
        sortByName();
    } else if (chosenType == "byDate") {
        sortByDate();
    } else if (chosenType == "byGender") {
        sortByGender();
    } else {
        throw "wrong sort selection"
    }
}

function displayRequest() {
    displayCards(charactersArray, (document.getElementById("page").value - 1) * dispRange, dispRange)
    dispNavigationData();
}

function nextPage() {
    let currPage = Number(document.getElementById("page").value);
    if (currPage == Math.ceil(charactersArray.length / dispRange)) {
        return;
    } else {
        document.getElementById("page").value = parseInt(currPage) + 1;
        displayRequest();
    }
}

function previousPage() {
    let currPage = document.getElementById("page").value;

    if (currPage <= 1) {
        return;
    } else {
        document.getElementById("page").value = parseInt(currPage) - 1;
        displayRequest();
    }
}

//display - used to refresh the view everytime the content changes
function displayCards(locCharactersArray, startIndex, range) {
    
    console.log("function execution: display(" + startIndex + ", " + range + ")");
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

function dispNavigationData(){
    document.getElementById("charsNumber").innerHTML = charactersArray.length;
    document.getElementById('pageId').innerHTML =
    "/" + Math.ceil(charactersArray.length / dispRange) + " stron";
}

function dispChange() {
    console.log(document.styleSheets[0]);
    let secStyle = document.getElementById("secStyleId").getAttribute("href");

    if (secStyle) {
        document.getElementById("secStyleId").setAttribute("href", "")
    } else {
        document.getElementById("secStyleId").setAttribute("href", "./style-column.css")
    }
    console.log(document.styleSheets);
}

//Unused at the momeny
//the idea for more organized child append
//planned to use in displayCards() and showDetails()
function appendChildElement(parentId, childType, childID, childClass, childEventName, childEventValue) {
    let parentElement = document.getElementById(parentId);
    let childElement = document.createElement(childType);

    console.log("created childElement: " + childElement);

    let attrId = document.createAttribute("id");
    attrId.value = childID;

    let attrClass = document.createAttribute("class");
    attrClass.value = childClass;

    let attrEvent = document.createAttribute(childEventName);
    attrEvent.value = childEventValue;

    childElement.setAttributeNode(attrId);
    childElement.setAttributeNode(attrClass);
    childElement.setAttributeNode(attrEvent);

    console.log("final structure of childElement" + childElement);
    console.log("parentElement: " + parentElement);
    parentElement.appendChild(childElement);
}

//TODO: create nicer way to display character
// function createCharacterCard(id){

//     appendChildElement()
//     for (i = startIndex; i < (startIndex + range) && i < charactersArray.length; i++) {

//         charListContainer.innerHTML = charListContainer.innerHTML +
//             '<div class="singleCharacter" id="charIdCard'+charactersArray[i].id+'" onclick="testFunction('+charactersArray[i].id+ ')">' +
//             '<div class="charImageHolder">' +
//             '<img src="' + charactersArray[i].image + '"/>' +
//             '</div>' +
//             '<div class="charDescriptionHolder">' +
//             '<div class="charDescription">Name: ' + charactersArray[i].name + '</div>' +
//             '<div class="charDescription">Status: ' + charactersArray[i].status + '</div>' +
//             '<div class="charDescription">Gender: ' + charactersArray[i].gender + '</div>' +
//             '<div class="charDescription">Species: ' + charactersArray[i].species + '</div>' +
//             '</div>'
//     }
//     let divCharacterContainer=document.createElement("div");
//     let divCharacterContainerClass=document.createAttribute("class");

//     divCharacterContainerClass.value="singleCharacter";

//     charListContainer.appendChild("BUTTON")

// }

async function showDetails(id) {
    let parentId = "charIdCard" + id;
    let parentElement = document.getElementById(parentId);

    let charDetails = await fetchQuery("character/" + id);

    let x = document.getElementById("detailChar" + id + "Display");
    if (x) {
        x.remove();
    } else {
        parentElement.innerHTML += '<div class="detailCharDisplay" id="detailChar' + id + 'Display" onclick="hideDetails()">' +
            '<div class="imageHolder">' +
            '<image src="' + charDetails.image + '" width="400px"/> </div>' +
            '<div class="dataHolder">' +
            '<div class="charDetailDescription" id="charDetailsName">Name of the character: <u>' + charDetails.name + '</u></div>' +
            '<div class="charDetailDescription" id="charDetailsGender">Gender: <u>' + charDetails.gender + '</u></div>' +
            '<div class="charDetailDescription" id="charDetailsId">Id of the character: <u>' + charDetails.id + '</u></div>' +
            '<div class="charDetailDescription" id="charDetailsCreated">Date of creation: <u>' + new Date(charDetails.created) + '</u></div>' +
            '<div class="charDetailDescription" id="charDetailsOrigin">Origin of the character: <u>' + charDetails.origin.name + '</u></div>' +
            '<div class="charDetailDescription" id="charDetailsEpisodes">This character played in following number of Episodes: <u>' + charDetails.episode.length + '</u></div>' +
            '<div class="charDetailDescription" id="charDetailsLocations">This character currently is in location: <u>' + charDetails.location.name + '</u></div>' + '</div>' +
            '</div>'

    }
}

function hideDetails() {
    console.log("i'm hiding details");
    let x = document.getElementById("detailCharDisplay");
    if (x) {
        x.remove();
    }
}
function parseCharDetails(character) {
    let charParsed = "";


    return charParsed;
}

//unused
function testFunction() {
    appendChildElement("testDiv", "div", "Id-stworzonego-diva", "klasa-nowego-diva", "onclick", "jakasfunkcja");
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