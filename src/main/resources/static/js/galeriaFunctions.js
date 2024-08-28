
window.addEventListener("load", (event) => {
    loadSeccionCarpetasImagenes("");
    document.getElementById("filterForName").addEventListener("keyup", event => {
        loadSeccionCarpetasImagenes(event.target.value)
    })

    menuIco();

    const observer = new ResizeObserver(function () {
        document.getElementById("imgDirSection").style.marginTop = nav.offsetHeight + 20 + "px";
    });

    const child = document.querySelector("nav");
    observer.observe(child);
});

function menuIco() {
    /* Para mantener un margin al nav automático, al estar fixed, a veces algun elemento puede varias.
    *  En este caso, controlamor imgDirSection, que es dónde se previsualizan las imágenes.
    */

    icoMenu.addEventListener("click", event => {
        let menuOpen = document.getElementById("menuContainer");
        const icoMenu = document.getElementById("icoMenu");
        if (!menuOpen) {
            const menuDesplegable = document.getElementById("menuDesplegable");
            const div = document.createElement("div")
            div.setAttribute("id", "menuContainer");
            menuDesplegable.append(div)
            menuOpen = document.getElementById("menuContainer");
            console.log(roleType + " - " + actualUsername)
            menuOpen.innerHTML += `<div>
            <p>Usuario: ${actualUsername}</p>
            <p>ROL: ${roleType}</p>
            </div>`
            const btnLogout = document.createElement("button")
            btnLogout.setAttribute("id", "btnLogout");
            btnLogout.innerHTML = "LOGOUT";
            btnLogout.setAttribute("onclick", "logout()")
            div.append(btnLogout)
            if (menuOpen) {
                menuOpen.setAttribute('tabindex', '0');
                btnLogout.focus();
            }
            btnLogout.addEventListener("focusout", event => {
                const menuOpen = document.getElementById("menuContainer");
                menuOpen.remove();
            })
        }

    })
}


// CERRAR CAJA AVISO ELIMINACIÓN DIRECTORIO O ARCHIVO
function cerrarCajaDel() {
    respMsg.innerHTML = "";
}



// INICIO MENÚ DEL BOTÓN  -- BIBLIOTECAS -- ####################################
// PARA GESTIONAR LOS DIRECTORIOS CONFIGURADOS.
function editPathFolders() {
    const div = document.createElement("div");
    div.setAttribute("id", "editPathFolders-container");
    div.setAttribute("class", "ventana-emergente");
    document.getElementById("header-container").appendChild(div);
    editpathcontainer = document.getElementById("editPathFolders-container");

    fetch(`/openConfigDirectory`)
        .then(res => res.text())
        .then(response => {
            jsonPath = JSON.parse(response);
            console.log(jsonPath)

            let tbodyTr = "";
            for (i = 0; i < jsonPath.configDirs.length; i++) {
                tbodyTr += `<tr id="${jsonPath.configDirs[i]}">
                                <td>${jsonPath.configDirs[i]}</td>
                                <td><button onclick="deletePath(event)" value="${jsonPath.configDirs[i]}">ELIMINAR</button></td>
                                </tr>`;
            }


            let html = `<div id="editPathFolders" class="contenido-emergente">
                                    <h1>CONFIGURACIÓN DE BIBLIOTECAS</h1>
                                        <div class="menu-container">
                                            <button onclick="addNewPath()" class="boton">AGREGAR</button>
                                            <button onclick="closeEditPathFolders()" class="boton">CANCELAR</button>
                                        </div>
                                        <div id="pathFoldSection">
                                            <section>
                                                <table>
                                                    <thead>
                                                        <th>RUTA</th>
                                                        <th>ACCION</th>
                                                    </thead>

                                                    <tbody>
                                                            ${tbodyTr}
                                                    </tbody>
                                                </table>
                                            </section>
                                        </div>
                                    <div id="newPathContainer"></div>
                    </div>`;
            editpathcontainer.innerHTML = html;
        })
}


function addNewPath(typeFunction) {
    const formData = new FormData();
    formData.append("path", "rootUnits");
    let buttonTypeAceptar = "confirmNewPath()";

    if (typeFunction === "mvDirFile") {
        buttonTypeAceptar = "mvImgDirFunction()"
    } else {

    }

    let options = {
        method: "POST",
        body: formData
    }

    fetch("/editDirectory", options)
        .then(res => res.text())
        .then(response => {
            let json = JSON.parse(response)
            let dirList = "";

            for (const [key, value] of Object.entries(json.dirList)) {
                dirList += `<li class="pointer"><button id="raiz" onclick="actionDirFunc(event)" class="botonList" value="${value}">${key}</button></li>`
            }
            document.getElementById("newPathContainer").innerHTML = `
                        <div id="newPathDiv" class="newPathDiv">
                            <div class="newPathNav">
                                <button id="newPathButton" onclick="${buttonTypeAceptar}">ACEPTAR</button>
                                <button onclick="closeNewPathDiv()">CANCELAR</button>
                            </div>
                            <div>
                                <input id="absolutPath" type="text" value=""/>
                                <input id="dirRaizTrueFalse" value="false" type="text" style="visibility:hidden"/>
                            </div>
                            <div class="newPathContainer">
                                <div class="home-directory">
                                    <ul class="dir-list">
                                        ${dirList}
                                    </ul>
                                </div>
                                <div class="file-directory">
                                    <ul id="contentPath" class="dir-list">
                                    </ul>
                                </div>
                            </div>
                            </div>`;
        })
}

function closeEditPathFolders() {
    document.getElementById("editPathFolders-container").remove();
    if (pathStatus) location.href = "/galeria";
}

function deletePath(event) {

    if (confirm("¿Esta seguro de eliminar el directorio seleccionada?")) {

        let path = event.target.value;
        const formData = new FormData();
        formData.append("path", path)
        let options = {
            method: "POST",
            body: formData
        }

        fetch(`/delDirectory`, options)
            .then(res => res.text())
            .then(response => {

                if (response === "true") {
                    document.getElementById(path).remove();
                    pathStatus = true;
                    loadSeccionCarpetasImagenes("");
                }
            })
    }
}

function closeNewPathDiv() {
    document.getElementById("newPathDiv").remove();
    document.getElementById("editPathFolders").style.visibility = "hidden"
}

let pathStatus = false;

function confirmNewPath() {

    let newFolderPath = document.getElementById("absolutPath").value;
    const formData = new FormData();
    formData.append("newFolderParh", newFolderPath)
    let options = {
        method: "POST",
        body: formData
    }
    fetch(`/confirmNewPath`, options)
        .then(res => res.text())
        .then(response => {
            if (response === "true") {
                alert("Directorio añadido satisfactoriamente.")
                editPathFolders();
                pathStatus = true;
                loadSeccionCarpetasImagenes("");
            } else {
                alert("No se pudo añadir el directorio seleccionado.")
            }
        })
}

function actionDirFunc(event) {
    let path = event.target.value;
    const formData = new FormData();
    formData.append("path", path);
    let options = {
        method: "POST",
        body: formData
    }
    fetch(`/editDirectory`, options)
        .then(res => res.text())
        .then(response => {
            let json = JSON.parse(response)
            let contentDir = "";
            let contentFiles = "";

            contentDir += `<li class="pointer"><button id="subdir" onclick="actionDirFunc(event)" class="botonList" value="${json.pathFirst}">../</button></li>`
            for (const [key, value] of Object.entries(json.dirList)) {
                contentDir += `<li class="pointer"><button id="subdir" onclick="actionDirFunc(event)" class="botonList" value="${value}">${key}</button></li>`
            }
            document.getElementById("contentPath").innerHTML = contentDir + contentFiles;
            const actionDirBtn = document.getElementsByName("actionDir");
            document.getElementById("absolutPath").value = path;
        })
}

// FINAL MENÚ DEL BOTÓN  -- BIBLIOTECAS -- ####################################

// INICIO MENÚ DEL BOTÓN  -- NUEVA CARPETA -- ####################################

function menuMkDir() {

    if (window.location.pathname === "/galeria") {
        alert("En esta ubicación, no puede crear carpetas.")
        return;
    }
    const node = document.createElement("div");
    node.setAttribute("id", "mkdir-container");
    node.setAttribute("class", "ventana-emergente");
    document.getElementById("header-container").appendChild(node);

    mkdircontainer = document.getElementById("mkdir-container");
    mkdircontainer.innerHTML = `<div id="mkdirsection" class="contenido-emergente">
                            <h1>CREAR NUEVA CARPETA</h1>
                            <input id="dirNameNewPath" type="text" />
                            <button onclick="acceptNewFolder()">ACEPTAR</button>
                            <button onclick="cancelNewFolder()">CANCELAR</button>
                            <p id="msgNewFolder"></p>
                        </div>`
    document.getElementById("dirNameNewPath").focus();
}

function acceptNewFolder() {
    let inputNewName = document.getElementById("dirNameNewPath").value;
    // var pathname = window.location.pathname;
    let msgNewFolder = document.getElementById("msgNewFolder");
    if (inputNewName !== "") {
        fetch(`/mkDir?dirName=${inputNewName}`)
            .then(res => res.text())
            .then(response => {
                let json = JSON.parse(response)
                msgNewFolder.innerHTML = json.respuesta
                if (json.respuesta === "Carpeta creada satisfactoriamente.") {
                    loadSeccionCarpetasImagenes("");
                }
            })
        cancelNewFolder();
    } else {
        msgNewFolder.innerHTML = "Debe introducir algún nombre para el nuevo directorio";
    }
}

function cancelNewFolder() {
    document.getElementById("mkdir-container").remove();
}
// FINAL MENÚ DEL BOTÓN  -- NUEVA CARPETA -- ####################################

// INICIO ZONA CARPETAS E CARGA IMÁGENES -- ####################################


let actualUsername;
let roleType;

function loadSeccionCarpetasImagenes(filter) {
    var pathname = window.location.pathname;
    console.log("FILTER --> " + filter)
    fetch(`/cargaContenido?uri=${pathname}&filter=${filter}`)
        .then(res => res.text())
        .then(response => {
            let json = JSON.parse(response)
            actualUsername = json.username;
            roleType = json.roleType;
            let verifyEmptyFolder = json.folderStatus;
            let emptyFolder = "";
            let dirSection = "";
            let fileSection = "";
            if (verifyEmptyFolder === 'empty') {
                emptyFolder = `<h3>La carpeta está vacia.</h3>`;
            } else {
                //AÑADIR CARPETAS
                for (i = 0; i < json.dirList.length; i++) {

                    if (json.dirList[i].name !== "imagePreview") {
                        dirSection += `<article id="${json.dirList[i].name}" class="dirContainer">
                        <div>
                            <input id="${json.dirList[i].name}" type="checkbox" class="mvDirFile"/>
                        </div>
                        <a href="${json.dirList[i].src}"><img name="${json.dirList[i].name}" src="/images/carpeta.png"></a>
                        <p id="showName${json.dirList[i].name}" name="rename" class="pointer" title="Pulsa para renombrar.">${json.dirList[i].name}</p>`

                        if (json.uriUbicacion.length > 1) {
                            dirSection +=
                                `   <input id="newName${json.dirList[i].name}" name="newName" value="${json.dirList[i].name}" type="hidden" />`;
                            // <button value="${json.dirList[i].name}" type="button" onclick="renFolImg(event)">Renombrar</button>`
                        }
                        dirSection += ` </article>`;
                    }

                }
                //AÑADIR ARCHIVOS
                for (i = 0; i < json.fileList.length; i++) {

                    const fileName = json.fileList[i].name.replace("PREVI_", "");

                    fileSection += `<article id="${json.fileList[i].name}" class="imgContainer" name="${json.fileList[i].name}">
                        <div>
                            <input id="${json.fileList[i].name}" type="checkbox" class="mvDirFile"/>
                        </div>
                        <a onclick="getInfoImg('${json.fileList[i].name}','${json.fileList[i].src}')">
                             <img id="${json.fileList[i].id}" name="${json.fileList[i].name}" src="${json.fileList[i].src}" href="${json.fileList[i].href}">
                        </a>
                        <p id="showName${fileName}" name="rename" class="pointer" title="Pulsa para renombrar.">${fileName}</p>
                        <input id="${fileName}" name="${fileName}" value="${fileName}" type="hidden"/>
                        </article>`

                }

            }
            let uriUbica = "";

            for (i = 0; i < json.uriUbicacion.length; i++) {
                uriUbica += `<a onclick="uriChangePath(event)" id="${json.uriUbicacion[i].uriTotal}" name="uriUbicaList" class="pointer" style="display: inline;">${json.uriUbicacion[i].carpeta}</a>`;
            }
            const ubicaText = "<div><p>UBICACIÓN: </p></div>"
            document.getElementById("rutaPath").innerHTML = `${ubicaText} <div> ${uriUbica} </div>`;
            let uriUbicaList = document.getElementsByName("uriUbicaList");

            let html =
                `<div id="seccionCarpetasImagenes-container">
                        <!-- APARTADO DE CONTENIDO DE DIRECTORIOS -->
                        ${emptyFolder}
                        ${dirSection}
                        ${fileSection}
                   </div>`;
            document.getElementById("seccionCarpetasImagenes").innerHTML = html;

            const renameP = document.getElementsByName("rename");

            for (i = 0; i < renameP.length; i++) {
                renameP[i].addEventListener("click", event => {
                    const father = event.target.parentElement;
                    const inputNewName = father.lastElementChild
                    let nombre = inputNewName.value
                    inputNewName.type = "visible";
                    const end = inputNewName.value.length
                    inputNewName.setSelectionRange(0, end - 4);
                    inputNewName.focus()

                    inputNewName.addEventListener("keypress", event => {
                        console.log(inputNewName.fo)
                        if (event.key === 'Enter' | event.key === 'Escape') {
                            inputNewName.type = "hidden";
                        }
                    })

                    inputNewName.addEventListener("focusout", event => {
                        const nombre2 = inputNewName.value
                        if (nombre !== nombre2) {
                            renFolImg(nombre, nombre2)
                        }
                        inputNewName.type = "hidden";
                    })


                })
            }

        })
}


function renFolImg(nombre, nombre2) {
    console.log("NOMBRE: " + nombre)
    console.log("NOMBRE2: " + nombre2)
    console.log("EN REN FOL IMG")
    fetch(`/rename?name=${nombre}&newName=${nombre2}`)
        .then(res => res.text())
        .then(response => {
            let json = JSON.parse(response);
            if (json.response = true) {
                location.reload();
            }
        })
    loadSeccionCarpetasImagenes("");
}

function uriChangePath(event) {
    window.location.pathname = event.target.id;
}



function downOriginalImg(path, urlImg) {
    console.log(path + " - " + urlImg)
    const link = document.createElement("a");
    link.setAttribute('href', urlImg);
    link.setAttribute('download', urlImg);
    link.click();
}


// PARA OBTENER LA INFORMACIÓN DE UNA IMAGEN.
let infoImgBox = null;

function getInfoImg(path, urlImg) {
    const options = {
        method: 'GET'
    };
    fetch("/imgProperties?imgName=" + path, options)
        .then(res => res.text())
        .then(response => {
            if (infoImgBox !== null) {
                infoImgBox.remove();
            }
            document.getElementById("nav").innerHTML += `<div id="infoImgBox-container" class="ventana-emergente"></div>`;
            document.getElementById("infoImgBox-container").innerHTML += `<div id="infoImgBox" class="contenido-emergente"></div>`;
            infoImgBox = document.getElementById("infoImgBox");
            const json = JSON.parse(response);

            console.log("PATH --> " + path)
            console.log("URL IMG --> " + urlImg)



            infoImgBox.innerHTML += `<button onclick="cerrarBox()"" id="closeImgBox"> X</button>
        <img src="${urlImg}" href="${urlImg}"/>
        <table id='infoImgTable'>
        <thead><th>PROPIEDAD</th><th>VALOR</th></thead >
        <tbody>
        <tr>
        <td>ALTURA</td>
        <td>${json.height}</td>
        </tr>
        <tr>
        <td>ANCHO</td>
        <td>${json.width}</td>
        </tr>
        <tr>
        <td>TRANSPARENCIA</td>
        <td>${json.transparencia}</td>
        </tr>
        <tr>
        <td>TIPO</td>
        <td>${json.type}</td>
        </tr>
        <tr>
        <td>RUTA</td>
        <td>${json.rutaAbsoluta}</td>
        </tr>
        </tbody>
        </table>`;
        });
}

// CERRAR CAJA DE INFO DE IMAGEN
function cerrarBox() {
    infoImgBox.remove();
}
// FINAL ZONA CARPETAS E CARGA IMÁGENES -- ####################################


// INICIO MENÚ DEL BOTÓN  -- SUBIR IMÁGENES -- ####################################


let imgFilesList = null;
function menuNewImages(event) {

    if (window.location.pathname === "/galeria") {
        alert("En esta ubicación, no puede subir imágenes.")
        return;
    }
    const node = document.createElement("div");
    node.setAttribute("id", "newImage-container");
    node.setAttribute("class", "ventana-emergente");
    document.getElementById("header-container").appendChild(node);

    mkdircontainer = document.getElementById("newImage-container");
    mkdircontainer.innerHTML = `<div id="newImgSection" class="contenido-emergente">
                <div id="menuNewImgSection">
                    <h1>SUBIR IMÁGENES</h1>
                    <input id="imgFile" name="imgFile" type="file" value="Subir imagen" formmethod="post" accept="image/*"multiple />
                    <button onclick="acceptNewImg()">ACEPTAR</button>
                    <button onclick="cancelNewImg()">CALCELAR</button>
                </div>
                <div id="listaImagenes">
            </div>`

    document.getElementById("imgFile").addEventListener("change", event => {
        imgFilesList = event.target.files;

        for (i = 0; i < imgFilesList.length; i++) {
            const imgPreviewFile = document.createElement("img")
            imgPreviewFile.className = "pointer"
            imgPreviewFile.id = imgFilesList[i].name

            document.getElementById("listaImagenes").appendChild(imgPreviewFile)

            var reader = new FileReader();
            reader.onload = function (e) {
                imgPreviewFile.src = e.target.result;
            }

            reader.readAsDataURL(imgFilesList[i]);

            imgPreviewFile.addEventListener("click", event => {
                rmImgFromList(event);
            })
        }
    });
}

function rmImgFromList(event) {
    let newFileList = Array.from(imgFilesList);
    for (i = 0; i < imgFilesList.length; i++) {
        if (imgFilesList[i].name === event.target.id) {
            newFileList.splice(i, 1);
        }
    }
    imgFilesList = newFileList;
    event.target.remove();
}

function acceptNewImg() {
    if (imgFilesList !== null && imgFilesList.length > 0) {
        const inputImgList = document.getElementById("imgFile")
        const formData = new FormData();
        const actualImgsArt = document.getElementsByTagName("article")
        let repeatImgName = false;
        for (i = 0; i < imgFilesList.length; i++) {
            for (j = 0; j < actualImgsArt.length; j++) {
                const img = actualImgsArt[j].querySelector("img");
                if (imgFilesList[i].name === img.id) {
                    repeatImgName = true;
                    alert("El archivo " + img.id + " ya existe en la ubicación de destino.")
                    break;
                }
            }
            if (repeatImgName) break;
            formData.append("inputImgList", imgFilesList[i]);
        }
        if (!repeatImgName) {
            uploadingImg();
            fetch('/uploadImg', {
                method: 'POST',
                body: formData
            })
                .then(res => res.text())
                .then(response => {
                    imgFilesList = null;
                    cancelNewImg();
                    finishUploadImg()
                    loadSeccionCarpetasImagenes("");

                })
        }

    } else {
        alert("Debe añadir algún archivo antes de aceptar.")
    }
}

function cancelNewImg() {
    document.getElementById("newImage-container").remove();
}



function uploadingImg() {

    document.getElementById("respMsg").innerHTML =
        `<div id="uploadingImg-container" class="ventana-emergente">
            <div id="uploadingImg" class="contenido-emergente">
            <span class="loader"></span>
            </div>
        </div>`

}

function finishUploadImg() {
    document.getElementById("uploadingImg-container").remove();
    console.log("remove")
}


// FINAL MENÚ DEL BOTÓN  -- SUBIR IMÁGENES -- ####################################



// INICIO MENÚ DEL BOTÓN  -- GESTIÓN IMÁGENES/DIR -- ####################################


let mvDelDirFileList = [];

function menuManageImgDir() {


    if (window.location.pathname === "/galeria") {
        alert("En esta ubicación, no puede mover carpetas.")
        return;
    }
    const mvDirFileListCheckBox = document.getElementsByClassName("mvDirFile");
    const mvDirFileContainer = document.getElementById("mvDirFileContainer");

    if (mvDirFileContainer != null) {
        return;
    }

    for (i = 0; i < mvDirFileListCheckBox.length; i++) {
        mvDirFileListCheckBox[i].style.visibility = "visible";
        mvDirFileListCheckBox[i].addEventListener("click", event => {
            const estado = event.target.checked;

            if (estado) {
                mvDelDirFileList.push(event.target.id)
            } else {

                for (i = 0; i < mvDelDirFileList.length; i++) {
                    if (event.target.id === mvDelDirFileList[i]) {
                        mvDirFmvDelDirFileListileList.splice(i, 1);
                    }
                }
            }
        })
    }
    const nav = document.getElementById("nav");
    nav.innerHTML +=
        `<div id="mvDirFileContainer">
            <div class="linea"></div>
            <button onclick="confirmMvDirFile()">MOVER</button>
            <button onclick="delFolImg(event)">ELIMINAR</button>
            <button onclick="cancelMvDirFile()">CANCELAR</button>
            </div>
        </div>`
    document.getElementById("imgDirSection").style.marginTop = nav.offsetHeight + "px";
    menuIco();
}


function confirmMvDirFile() {

    if (mvDelDirFileList.length === 0 | mvDelDirFileList === null) {
        alert("No ha seleccionado ningún archivo o carpeta para mover.")
        return
    }
    document.getElementById("respMsg").innerHTML =
        `<div id="editPathFolders" class="ventana-emergente" style="visibility: hidden">
        <div id="editPathFolders" class="contenido-emergente">
            <div id="newPathContainer"></div>
     </div>`


    const node = document.createElement("div");
    node.setAttribute("id", "mvDirFile-container");
    node.setAttribute("class", "ventana-emergente");
    document.getElementById("mvDirFileContainer").appendChild(node);
    document.getElementById("editPathFolders").style.visibility = "visible"
    addNewPath("mvDirFile");
}

function mvImgDirFunction() {
    const newFolder = document.getElementById("absolutPath")
    if (newFolder.value === "") {
        alert("Debe seleccionar una nueva ubicación.")
        newFolder.focus();
        return
    }
    //CODIGO FETCH, RECORDAR TRABAJAAR CON EL RESULTADO DEL ARRAY, SI FUNCIONA TODO RESET, SI NO, NO.

    let formData = new FormData();

    formData.append("newFolder", newFolder.value);
    formData.append("fileDirList", mvDelDirFileList);

    const options = {
        method: "POST",
        body: formData
    }

    fetch("/mvDirFiles", options)
        .then(res => res.text())
        .then(response => {
            const json = JSON.parse(response);
            closeNewPathDiv();
            cancelMvDirFile();
            loadSeccionCarpetasImagenes("");

            let listaErrores = "";

            for (i = 0; i < json.errors.length; i++) {
                console.log("ERRORES: " + json.errors[i])
                listaErrores += `<li>${json.errors[i]}</li>`;
            }

            if (json.errors.length > 0) {
                respMsg = document.getElementById("respMsg");
                respMsg.innerHTML =
                    `<div id="cajaDel" role="alert" style="background: rgba(255,0,0,0.5);" >
                        <button id="respuestaDela" onclick="cerrarCajaDel()">X</button>
                        <h3>LISTA DE ARCHIVOS QUE YA EXISTÍAN EN LA NUEVA UBICACIÓN.</h3>
                        <ol>
                            ${listaErrores}
                        </ol>
                   </div > `;
            }
        })

    mvDelDirFileList = [];
}

function delFolImg() {

    let confirma = confirm(`¿Confirma que desea eliminar los elementos seleccionados?`);
    if (confirma) {

        let formData = new FormData();
        formData.append("list", mvDelDirFileList);

        const options = {
            method: "POST",
            body: formData
        }

        fetch("/delImgOrDirectory", options)
            .then(res => res.text())
            .then(response => {
                let json = JSON.parse(response);
                respMsg = document.getElementById("respMsg");
                if (json.respuesta === "ok") {
                    respMsg.innerHTML =
                        `<div id="cajaDel" role="alert" style="background: rgb(68, 236, 54)">
            <p id="respuestaDel">${json.delMsg}</p>
            <button id="respuestaDela" onclick="cerrarCajaDel()">X</button>
            </div > `;
                    mvDelDirFileList = [];
                    cancelMvDirFile();
                    loadSeccionCarpetasImagenes("");
                } else {
                    respMsg.innerHTML =
                        `<div id="cajaDel" role="alert" style="background: rgba(255,0,0,0.5);" >
            <p id="respuestaDel">${json.delMsg}</p>
            <button id="respuestaDela" onclick="cerrarCajaDel()">X</button>
            </div > `;
                }
            })
    }
}


function cancelMvDirFile() {
    document.getElementById("mvDirFileContainer").remove();
    const mvDirFileListCheckBox = document.getElementsByClassName("mvDirFile");
    for (i = 0; i < mvDirFileListCheckBox.length; i++) {
        mvDirFileListCheckBox[i].style.visibility = "hidden";
        mvDirFileListCheckBox[i].checked = false;
    }
    document.getElementById("imgDirSection").style.marginTop = nav.offsetHeight + "px";
    mvDelDirFileList = [];

}

// FINAL MENÚ DEL BOTÓN  -- GESTIÓN IMÁGENES/DIR -- ####################################





//INICIO MENÚ DEL BOTÓN  -- GESTIÓN USUARIOS -- ####################################

function userManagement() {

    const node = document.createElement("div");
    node.setAttribute("id", "userManagement-container");
    node.setAttribute("class", "ventana-emergente");
    document.getElementById("header-container").appendChild(node);
    let getUserName;
    fetch(`/getAllUsersManagement`)
        .then(res => res.text())
        .then(response => {

            let json = JSON.parse(response);

            let userInfo = '';
            for (i = 0; i < json.userList.length; i++) {
                // for(a = 0; a < json.userList.roles.length)
                const id = json.userList[i].id;
                let enableSelected = "selected";


                userInfo +=
                    `<tr id="${id}" name="trUser">
                    <td>
                        <span id="id${id}" name="${json.userList[i].id}">${json.userList[i].id}</span>
                    </td>
                    <td>
                        <input id="username" name="${json.userList[i].username}" value="${json.userList[i].username}"></input>
                    </td>
                    <td>
                            <select id="enabled" name="${json.userList[i].enabled}">
                            <option value="true">True</option>
                            <option value="false">False</option>
                        </select>
                    </td>
                    <td>
                        <select id="accountNoExpired" name="${json.userList[i].accountNoExpired}">
                            <option value="true">True</option>
                            <option value="false">False</option>
                        </select>
                    </td>
                    <td>
                            <select id="accountNoLocked" name="${json.userList[i].accountNoLocked}">
                            <option value="true">True</option>
                            <option value="false">False</option>
                        </select>
                    </td>
                    <td>
                            <select id="credentialNoExpired" name="${json.userList[i].credentialNoExpired}">
                            <option value="true">True</option>
                            <option value="false">False</option>
                        </select>
                    </td>
                    <td>
                            <select id="roleEnum" name="${json.userList[i].roles[0].roleEnum}">
                            <option value="ADMIN">ADMIN</option>
                            <option value="USER">USER</option>
                            <option value="INVITED">INVITED</option>
                            <option value="DEVELOPER">DEVELOPER</option>
                        </select>
                    </td>
                    <td>
                        <button id="${id}" onclick="delUser(event)">Eliminar</button>
                    </td>
                </tr>`;
            }
            let htmlTable = `    <table class="tableUSerManagement">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>USERNAME</th>
                                            <th>ACTIVADO</th>
                                            <th>NO EXPIRA</th>
                                            <th>NO CERRADO</th>
                                            <th>CREDENCIALES NO EXPIRAN</th>
                                            <th>ROL ASIGNADO</th>
                                            <th>ELIMINAR USUARIO</th>
                                        </tr>
                                    </thead>
        
                                    <tbody>
                                        ${userInfo}
                                    </tbody>
        
                                </table>
                                `;

            userManaContainer = document.getElementById("userManagement-container");
            userManaContainer.innerHTML = `<div id="userManaContainer" class="contenido-emergente">
                                    <h1>GESTIÓN DE USUARIOS</h1>
                                    <div>
                                        <button onclick="newUserManagement()">NUEVO USUARIO</button>
                                        <button onclick="cancelUserManagement()">CANCELAR</button>
                                    </div>
                                    ${htmlTable}
                                </div>`
        })
        .then(final => {
            const rows = document.getElementsByName("trUser");
            for (i = 0; i < rows.length; i++) {
                let userActual = false;
                rows[i].addEventListener("click", event => {
                    const tr = event.target.closest("tr");
                    actualUsername: tr.querySelector("#username").value
                    undoUsername = tr.querySelector("#username").value;
                    let clickUserGet = event.target.value;
                    if (actualUsername === clickUserGet) {
                        userActual = true;
                        console.log("USUARIO ACTUAL. " + actualUsername + " - " + clickUserGet)
                    } else {
                        console.log("USUARIO NO ACTUAL.")
                        userActual = false;
                    }
                })

                const trList = document.getElementsByName("trUser");
                rows[i].addEventListener("change", event => {

                    const tr = event.target.closest("tr");
                    let newUsername = tr.querySelector("#username").value
                    let userAlreadyExist = false;

                    trList.forEach(event => {
                        console.log(event.querySelector("#username").value)
                        let usernameFromList = event.querySelector("#username").name;

                        if (newUsername === usernameFromList) {
                            userAlreadyExist = true;
                        }
                    })

                    if (tr.querySelector("select").type === "select-one") {
                        if (!confirm(`¿Está seguro de cambiar la información del usuario?`)) {
                            userManagement();
                            return false;
                        }
                    }else{
                        if (userAlreadyExist) {
                            alert("El nombre de usuario que ha elegido, ya existe. Elija otro.")
                            userManagement();
                            return false;
                        }

                        if (!confirm(`¿Está seguro de cambiar el nombre de usuario de ${undoUsername} a ${newUsername}?`)) {
                            userManagement();
                            return false;
                        }
                    }

                    let formData = JSON.stringify({
                        id: tr.id,
                        username: tr.querySelector("#username").value,
                        enabled: tr.querySelector("#enabled").value,
                        accountNoExpired: tr.querySelector("#accountNoExpired").value,
                        accountNoLocked: tr.querySelector("#accountNoLocked").value,
                        credentialNoExpired: tr.querySelector("#credentialNoExpired").value,
                        roleEnum: tr.querySelector("#roleEnum").value
                    })
                    let options = {
                        method: 'POST',
                        body: formData
                    };
                    fetch(`/editUser`, options)
                        .then(res => res.text()
                            .then(response => {
                                if (userActual) {
                                    alert("Ha cambiado el nombre de usuario actual, vuelva a hacer login.")
                                    location.href = "/logout";
                                } else {
                                    userManagement();
                                }
                            }))

                })
            }

            //ASIGNAMOS LOS VALORES REALES A LOS SELECTS
            const selects = document.getElementsByTagName("select");
            for (i = 0; i < selects.length; i++) {
                for (j = 0; j < selects[i].length; j++) {
                    if (selects[i].name === selects[i][j].value) {
                        selects[i].value = selects[i][j].value;
                    }
                }
            }
        })
}


function cancelUserManagement() {
    document.getElementById("userManagement-container").remove();
}

function newUserManagement() {
    // Abriremos una pequeña ventana emergente para añadir el usuario. 
    // Después refrescaremos la lista entera, si se acepta, si se cancela, no haremos nada
    const node = document.createElement("div");
    node.setAttribute("id", "newUser-Container");
    node.setAttribute("class", "subventana-emergente");
    document.getElementById("nav").appendChild(node);
    const newUser_container = document.getElementById("newUser-Container");
    newUser_container.innerHTML = `<div id="newUserContainer" class="contenido-emergente">
                                        <button onclick="newUserConfirm(event)">ACEPTAR</button>
                                        <button onclick="newUserCancel(event)">CANCELAR</button>
                                        <table class="tableUSerManagement">
                                                <thead>
                                                    <tr>
                                                        <th>USERNAME</th>
                                                        <th>PASSWORD</th>
                                                        <th>ACTIVADA</th>
                                                        <th>NO EXPIRA</th>
                                                        <th>NO CERRADO</th>
                                                        <th>CREDENCIALES NO EXPIRAN</th>
                                                        <th>ROL ASIGNADO</th>
                                                    </tr>
                                                </thead>
                    
                                                <tbody>
                                                    <tr id="newUserTr" name="trUser">
                                                        <td>
                                                            <input id="username"></input>
                                                        </td>
                                                        <td>
                                                            <input id="password"></input>
                                                        </td>
                                                        <td>
                                                            <select id="enabled">
                                                            <option value="true">True</option>
                                                            <option value="false">False</option>
                                                          </select>
                                                        </td>
                                                        <td>
                                                            <select id="accountNoExpired">
                                                                <option value="true">True</option>
                                                                <option value="false">False</option>
                                                            </select>
                                                        </td>
                                                        <td>
                                                                <select id="accountNoLocked">
                                                                <option value="true">True</option>
                                                                <option value="false">False</option>
                                                            </select>
                                                        </td>
                                                        <td>
                                                                <select id="credentialNoExpired">
                                                                <option value="true">True</option>
                                                                <option value="false">False</option>
                                                            </select>
                                                        </td>
                                                        <td>
                                                                <select id="roleEnum">
                                                                <option value="ADMIN">ADMIN</option>
                                                                <option value="USER">USER</option>
                                                                <option value="INVITED">INVITED</option>
                                                                <option value="DEVELOPER">DEVELOPER</option>
                                                            </select>
                                                        </td>
                                                </tbody>
                    
                                            </table>
                                        </div>`;
}

function newUserCancel() {
    document.getElementById("newUser-Container").remove();
}

function newUserConfirm(event) {

    const tr = document.getElementById("newUserTr");
    newUsername = tr.querySelector("#username").value
    let formData = JSON.stringify({
        username: tr.querySelector("#username").value,
        password: tr.querySelector("#password").value,
        enabled: tr.querySelector("#enabled").value,
        accountNoExpired: tr.querySelector("#accountNoExpired").value,
        accountNoLocked: tr.querySelector("#accountNoLocked").value,
        credentialNoExpired: tr.querySelector("#credentialNoExpired").value,
        roleEnum: tr.querySelector("#roleEnum").value
    })
    let options = {
        method: 'POST',
        body: formData
    };

    fetch(`/newUser`, options)
        .then(res => res.text()
            .then(response => {
                console.log("RESPUESTA NEW USER: " + response)
                if (response === "true") {
                    newUserCancel();
                    userManagement();

                }
            }))
}

function delUser(event) {

    if (confirm("¿Va a eliminar el usuario con ID:" + event.target.id + ", ¿Desea continuar?")) {
        console.log("ID --> " + event.target.id)
        let formData = new FormData();
        formData.append("id", event.target.id);
        const options = {
            method: "POST",
            body: formData
        }
        fetch(`/delUser`, options)
            .then(res => res.text()
                .then(response => {

                    if (response === "true") {
                        userManagement();
                    } else {
                        alert("Ha ocurrido algún problemas al eliminar el usuario");
                    }

                }))
    }
}

//FINAL MENÚ DEL BOTÓN  -- GESTIÓN USUARIOS -- ####################################

//INICIO MENÚ DEL BOTÓN  -- LOGOUT -- ####################################
function logout() {
    location.href = "/logout";
}
//FINAL MENÚ DEL BOTÓN  -- LOGOUT -- ####################################