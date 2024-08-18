
window.addEventListener("load", (event) => {
    loadSeccionCarpetasImagenes();
});


// CERRAR CAJA AVISO ELIMINACIÓN DIRECTORIO O ARCHIVO
function cerrarCajaDel() {
    respMsg.innerHTML = "";
}



// INICIO MENÚ DEL BOTÓN  -- GESTION CARPETAS -- ####################################
// PARA GESTIONAR LOS DIRECTORIOS CONFIGURADOS.
function editPathFolders() {
    document.getElementById("nav").innerHTML += `<div id="editPathFolders-container" class="ventana-emergente"></div>`;
    editpathcontainer = document.getElementById("editPathFolders-container");
    fetch(`/openConfigDirectory`)
        .then(res => res.text())
        .then(response => {
            jsonPath = JSON.parse(response);

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
                                            <button onclick="addNewPath()" class="boton">Agregar nueva ruta</button>
                                            <button onclick="closeEditPathFolders()" class="boton">CERRAR</button>
                                        </div>
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
                                    <div id="newPathContainer"></div>
                    </div>`;
            editpathcontainer.innerHTML = html;
        })
}


function addNewPath() {
    const formData = new FormData();
    formData.append("path", "rootUnits");

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
                                <button onclick="closeNewPathDiv()">CANCELAR</button>
                                <button onclick="confirmNewPath()">ACEPTAR</button>
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
                    loadSeccionCarpetasImagenes();
                }
            })
    }
}

function closeNewPathDiv() {
    document.getElementById("newPathDiv").remove();
}

let pathStatus = false;

function confirmNewPath() {

    if (document.getElementById("dirRaizTrueFalse").value === "false") {
        alert("No se pueden añadir directorios raiz. Debe seleccionar una subcarpeta.")
        return;
    }

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
                loadSeccionCarpetasImagenes();
            } else {
                alert("No se pudo añadir el directorio seleccionado.")
            }
        })
}

function actionDirFunc(event) {

    if (event.target.id !== "raiz") {
        document.getElementById("dirRaizTrueFalse").value = "true";
    } else {
        document.getElementById("dirRaizTrueFalse").value = "false";
    }
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

// FINAL MENÚ DEL BOTÓN  -- GESTION CARPETAS -- ####################################

// INICIO MENÚ DEL BOTÓN  -- NUEVA CARPETA -- ####################################

function menuMkDir() {

    if (window.location.pathname === "/galeria") {
        alert("En esta ubicación, no puede crear carpetas.")
    } else {
        const node = document.createElement("div");
        node.setAttribute("id", "mkdir-container");
        node.setAttribute("class", "ventana-emergente");
        document.getElementById("nav").appendChild(node);

        mkdircontainer = document.getElementById("mkdir-container");
        mkdircontainer.innerHTML = `<div id="mkdirsection" class="contenido-emergente">
                            <h1>CREAR NUEVA CARPETA</h1>
                            <input id="dirNameNewPath" type="text" />
                            <button onclick="acceptNewFolder()">ACEPTAR</button>
                            <button onclick="cancelNewFolder()">CERRAR</button>
                            <p id="msgNewFolder"></p>
                        </div>`
        document.getElementById("dirNameNewPath").focus();
    }




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
                    loadSeccionCarpetasImagenes();
                    document.getElementById("dirNameNewPath").value = "";
                }
            })
    } else {
        msgNewFolder.innerHTML = "Debe introducir algún nombre para el nuevo directorio";
    }
}

function cancelNewFolder() {
    document.getElementById("mkdir-container").remove();
}
// FINAL MENÚ DEL BOTÓN  -- NUEVA CARPETA -- ####################################


// INICIO ZONA CARPETAS E CARGA IMÁGENES -- ####################################
function loadSeccionCarpetasImagenes() {
    var pathname = window.location.pathname;

    fetch(`/cargaContenido?uri=${pathname}`)
        .then(res => res.text())
        .then(response => {
            let json = JSON.parse(response)

            document.getElementById("header-container").innerHTML = `<h1>${json.username}</h1>`

            let verifyEmptyFolder = json.folderStatus;
            let emptyFolder = "";
            let dirSection = "";
            let fileSection = "";
            if (verifyEmptyFolder === 'empty') {
                emptyFolder = `<h3>La carpeta está vacia.</h3>`;
            } else {
                //AÑADIR CARPETAS
                for (i = 0; i < json.dirList.length; i++) {
                    dirSection += `<article id="${json.dirList[i].name}" class="dirContainer">
                            <a href="${json.dirList[i].src}"><img name="${json.dirList[i].name}" src="/images/carpeta.png"></a>
                            <p id="showName${json.dirList[i].name}">${json.dirList[i].name}</p>`

                    if (json.uriUbicacion.length > 1) {
                        dirSection +=
                            `<button id="${json.dirList[i].name}" value="${json.dirList[i].src}" onclick="delFolImg(event)">Eliminar</button>
                                 <input id="newName${json.dirList[i].name}" name="newName" value="${json.dirList[i].name}" type="hidden" />
                                 <button value="${json.dirList[i].name}" type="button" onclick="renFolImg(event)">Renombrar</button>`;
                    }
                    dirSection += ` </article>`;

                }
                //AÑADIR ARCHIVOS
                for (i = 0; i < json.fileList.length; i++) {
                    fileSection += `<article id="${json.fileList[i].name}" class="imgContainer" name="${json.fileList[i].name}">
                        <a onclick="getInfoImg('${json.fileList[i].name}','${json.fileList[i].src}')">
                            <img id="${json.fileList[i].id}" name="${json.fileList[i].name}" src="${json.fileList[i].src}">
                        </a>
                        <p id="showName${json.fileList[i].name}">${json.fileList[i].name}</p>
                        <button id="${json.fileList[i].name}" value="${json.fileList[i].src}" onclick="delFolImg(event)">Eliminar</button>
                        <input id="newName${json.fileList[i].name}" name="newName" value="${json.fileList[i].name}" type="hidden" />
                        <button value="${json.fileList[i].name}" type="button" onclick="renFolImg(event)">Renombrar</button>
                        </article>`
                }

            }
            let uriUbica = "";

            for (i = 0; i < json.uriUbicacion.length; i++) {
                uriUbica += `<a id="${json.uriUbicacion[i].uriTotal}" name="uriUbicaList" class="pointer" style="display: inline;">${json.uriUbicacion[i].carpeta}</a>`;
            }
            document.getElementById("rutaPath").innerHTML = uriUbica
            let uriUbicaList = document.getElementsByName("uriUbicaList");

            for (i = 0; i < uriUbicaList.length; i++) {
                uriUbicaList[i].addEventListener("click", event => {
                    window.location.pathname = event.target.id;
                })
            }

            let html =
                `<div id="seccionCarpetasImagenes-container">
                        <!-- APARTADO DE CONTENIDO DE DIRECTORIOS -->
                        ${emptyFolder}
                        ${dirSection}
                        ${fileSection}
                   </div>`;
            document.getElementById("seccionCarpetasImagenes").innerHTML = html;
        })
}

function delFolImg(event) {

    let confirma = confirm(`¿Confirma que desea eliminar el elemento?"${event.target.id}"
                    Aceptar o Cancelar.`);
    if (confirma) {
        fetch("/delImgOrDirectory?path=" + event.target.value)
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
                    document.getElementById(event.target.id).remove();
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

function renFolImg(event) {

    let boton = event.target;
    inputNewName = document.getElementById("newName" + boton.value);
    if (boton.innerHTML == "Aceptar") {
        nombre2 = inputNewName.value;
        if (nombre !== nombre2) {
            fetch(`/rename?name=${nombre}&newName=${nombre2}`)
                .then(res => res.text())
                .then(response => {
                    let json = JSON.parse(response);
                    if (json.response = true) {
                        location.reload();
                    }
                })
        }
        inputNewName.type = 'hidden';
        boton.innerHTML = "Renombrar";
    } else {
        nombre = inputNewName.value;
        inputNewName.type = 'visible';
        boton.innerHTML = "Aceptar";
    }
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

            infoImgBox.innerHTML += `<button onclick="cerrarBox()"" id="closeImgBox"> X</button>
        <img src="` + urlImg + `"/>
        <table id='infoImgTable'>
        <thead><th>PROPIEDAD</th><th>VALOR</th></thead >
        <tbody>
        <tr>
        <td>HEIGHT</td>
        <td>${json.height}</td>
        </tr>
        <tr>
        <td>WIDTH</td>
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
    } else {
        const node = document.createElement("div");
        node.setAttribute("id", "newImage-container");
        node.setAttribute("class", "ventana-emergente");
        document.getElementById("nav").appendChild(node);

        mkdircontainer = document.getElementById("newImage-container");
        mkdircontainer.innerHTML = `<div id="newImgSection" class="contenido-emergente">
                <h1>SUBIR IMÁGENES</h1>
                <input id="imgFile" name="imgFile" type="file" value="Subir imagen" formmethod="post" accept="image/*"multiple />
                <button onclick="acceptNewImg()">ACEPTAR</button>
                <button onclick="cancelNewImg()">CALCELAR</button>
                <div id="listaImagenes"></div>
            </div>`

        document.getElementById("imgFile").addEventListener("change", event => {
            imgFilesList = event.target.files;
            showListImgSelected();
        });
    }
}


function showListImgSelected() {
    let htmlListaImagenes = `<p style="margin-left: 40px;">Click en la imágenes, para eliminarla.</p>`;
    htmlListaImagenes += `<ul>`;
    for (i = 0; i < imgFilesList.length; i++) {
        htmlListaImagenes += `<li><button onclick="rmImgFromList(event)" class="botonList">${imgFilesList[i].name}</button></li>`;
    }
    htmlListaImagenes += `</ul>`;
    document.getElementById("listaImagenes").innerHTML = htmlListaImagenes;
}

function rmImgFromList(event) {
    let newFileList = Array.from(imgFilesList);
    for (i = 0; i < imgFilesList.length; i++) {
        if (imgFilesList[i].name === event.target.innerHTML) {
            newFileList.splice(i, 1);
        }
    }
    imgFilesList = newFileList;
    imgFilesList.val = "22";
    showListImgSelected();
}

function acceptNewImg() {
    if (imgFilesList !== null && imgFilesList.length > 0) {
        const inputImgList = document.getElementById("imgFile")
        const formData = new FormData();

        for (i = 0; i < imgFilesList.length; i++) {
            formData.append("inputImgList", imgFilesList[i]);
        }

        fetch('/uploadImg', {
            method: 'POST',
            body: formData
        })
            .then(res => res.text())
            .then(response => {
                loadSeccionCarpetasImagenes();
                cancelNewImg();
            })

    } else {
        alert("Debe añadir algún archivo antes de aceptar.")
    }
}

function cancelNewImg() {
    document.getElementById("newImage-container").remove();
}


// FINAL MENÚ DEL BOTÓN  -- SUBIR IMÁGENES -- ####################################

//INICIO MENÚ DEL BOTÓN  -- GESTIÓN USUARIOS -- ####################################

function userManagement() {

    const node = document.createElement("div");
    node.setAttribute("id", "userManagement-container");
    node.setAttribute("class", "ventana-emergente");
    document.getElementById("nav").appendChild(node);

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
                </tr>`;
            }
            let htmlTable = `    <table class="tableUSerManagement">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>USERNAME</th>
                                            <th>ACTIVADA</th>
                                            <th>NO EXPIRA</th>
                                            <th>NO CERRADO</th>
                                            <th>CREDENCIALES NO EXPIRAN</th>
                                            <th>ROL ASIGNADO</th>
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
                                    <button onclick="cancelUserManagement()">CERRAR</button>
                                    ${htmlTable}
                                </div>`
        })
        .then(final => {

            const rows = document.getElementsByName("trUser");
            for (i = 0; i < rows.length; i++) {
                rows[i].addEventListener("change", event => {
                    const tr = event.target.closest("tr");
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
                                userManagement();
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
}


function delUserManagement() {
    // Eliminaremos usuario, solicitaremos confirmación
}

//FINAL MENÚ DEL BOTÓN  -- INFO USUARIO -- ####################################

//INICIO MENÚ DEL BOTÓN  -- GESTIÓN USUARIOS -- ####################################

function userInfo() {
    // Para la información del usuario que usa la aplicacióon.
    console.log("userInfo")
}

//FINAL MENÚ DEL BOTÓN  -- INFO USUARIO -- ####################################