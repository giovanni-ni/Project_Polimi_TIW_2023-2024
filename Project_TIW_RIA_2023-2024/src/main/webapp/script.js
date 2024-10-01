document.addEventListener('DOMContentLoaded', function() {
    // 获取所有文件夹的元素
    const folders = document.querySelectorAll('.folder');

    folders.forEach(folder => {
        // 创建第一个按钮
        const button1 = document.createElement('button');
        button1.textContent = 'aggiungi cartella';
		button1.onclick = () => openModal(folder);

        // 创建第二个按钮
        const button2 = document.createElement('button');
        button2.textContent = 'aggiungi file';
        button2.onclick = () => openModal_one(folder);
        // 将按钮添加到文件夹旁边
 		folder.insertBefore(button1, folder.firstChild);
        folder.insertBefore(button2, button1.nextSibling); 
    });
});

		function openModal(parentFolder) {
            currentParentFolder = parentFolder; 
            //window.alert(parentFolder.dataset.folderId);
            const modal = document.getElementById("myModal");
            modal.style.display = "block";
        }

        function closeModal() {
            const modal = document.getElementById("myModal");
            modal.style.display = "none";
        }

        document.querySelector(".close").onclick = closeModal;

        window.onclick = function(event) {
            const modal = document.getElementById("myModal");
            if (event.target === modal) {
                closeModal();
            }
        };
        
        
       
	 document.getElementById('folderForm').onsubmit = function(event) {
	    event.preventDefault(); // 防止页面刷新
	    var form = event.target.closest("form");
	    
	    if (form.checkValidity()) {
			
			var folderName = document.getElementById('folderName').value;
        	var url;
        	if(currentParentFolder != null) {
				url = 'AddFolder?id=' + currentParentFolder.dataset.folderId + '&name=' + encodeURIComponent(folderName);
			}else {
				url = 'AddFolder?id=null' + '&name=' + encodeURIComponent(folderName);
			}
        
        // Costruisce la URL con l'ID della cartella e il nome della nuova cartella
        	//var url = 'AddFolder?id=' + currentParentFolder.dataset.folderId + '&name=' + encodeURIComponent(folderName);
			
	        makeCall("POST", url, form, // 确保使用正确的 URL
	            function(x) {
	                if (x.readyState == XMLHttpRequest.DONE) {
	                    var message = x.responseText;
	                    switch (x.status) {
	                        case 200: // 成功
	                            window.location.href = "HomePage"; // 成功后重定向
	                            break;
	                        case 400: // bad request
	                            document.getElementById("errormessage").textContent = message;
	                            break;
	                        case 401: // unauthorized
	                            document.getElementById("errormessage").textContent = message;
	                            break;
	                        case 500: // server error
	                            document.getElementById("errormessage").textContent = message;
	                            break;
	                    }
	                }
	            }
	        );
	    } else {
	        form.reportValidity(); // 提示表单验证错误
	    }
	}

function openModal_one(parentFolder) {
            currentParentFolder = parentFolder; 
            //window.alert(parentFolder.dataset.folderId);
            const modal = document.getElementById("myModal1");
            modal.style.display = "block";
}

function closeModal_one() {
    const modal = document.getElementById("myModal1");
    modal.style.display = "none";
}

document.querySelector(".close1").onclick = closeModal_one;

window.onclick = function(event) {
    const modal = document.getElementById("myModal1");
    if (event.target === modal) {
        closeModal_one();
    }
};
        
        

document.getElementById('fileForm').onsubmit = function(event) {
	    event.preventDefault(); // 防止页面刷新
	    var form = event.target.closest("form");
	    
	    if (form.checkValidity()) {
			
			var fileName = document.getElementById('fileName').value;
        	var fileType = document.getElementById('fileType').value;
        	var fileSummary = document.getElementById('fileSummary').value;
        // Costruisce la URL con l'ID della cartella e il nome della nuova cartella
        	var url = 'AddFile?id=' + currentParentFolder.dataset.folderId + '&name=' + encodeURIComponent(fileName)
        					+'&type='+encodeURIComponent(fileType)+ '&summary='+encodeURIComponent(fileSummary);
			
	        makeCall("POST", url, form, // 确保使用正确的 URL
	            function(x) {
	                if (x.readyState == XMLHttpRequest.DONE) {
	                    var message = x.responseText;
	                    switch (x.status) {
	                        case 200: // 成功
	                            window.location.href = "HomePage"; // 成功后重定向
	                            break;
	                        case 400: // bad request
	                            document.getElementById("errorfilemessage").textContent = message;
	                            break;
	                        case 401: // unauthorized
	                            document.getElementById("errorfilemessage").textContent = message;
	                            //console.log("sono qui");
	                            break;
	                        case 500: // server error
	                            document.getElementById("errorfilemessage").textContent = message;
	                            break;
	                    }
	                }
	            }
	        );
	    } else {
	        form.reportValidity(); // 提示表单验证错误
	    }
	}


function makeCall(method, url, data, callback, reset = true) {
    /*var x = new XMLHttpRequest();
    x.open(method, url, true);
    x.onreadystatechange = function() {
        callback(x); // 调用回调函数并传递 XMLHttpRequest 对象
    };
    if (method === 'POST') {
        x.send(data);
    } else { // per GET o altre richieste senza dati nel corpo
        x.send();
    }*/
     var req = new XMLHttpRequest(); // visible by closure
	    req.onreadystatechange = function() {
	      callback(req)
	    }; // closure
	    req.open(method, url);
	    if (data == null) {
	      req.send();
	    } else {
	      req.send(new FormData(data));
	    }
	    if (data !== null && reset === true) {
	      data.reset();
	    }
}

const folders = document.querySelectorAll('.folder');
const trashBin = document.getElementById('trashBin');
const files = document.querySelectorAll('.file');

folders.forEach(folder => {
    folder.addEventListener('dragstart', (event) => {
        event.dataTransfer.setData('text/plain', folder.getAttribute('data-folder-id'));
        //console.log(folder.getAttribute('data-folder-id'));
        event.dataTransfer.effectAllowed = 'move';
        event.stopPropagation();
    });
    
    folder.addEventListener('dragover', (event) => {
        event.preventDefault(); // Necessario per consentire il drop
    });

    folder.addEventListener('drop', (event) => {
        event.preventDefault(); // Prevenzione del comportamento predefinito

        const fileId = event.dataTransfer.getData('text/plain'); // Ottieni l'ID del file trascinato
        const newParentId = folder.getAttribute('data-folder-id'); // Ottieni l'ID della cartella di destinazione

        // Chiama il servlet per aggiornare il parent_id del file
        moveFileToFolder(fileId, newParentId);
        
        event.stopPropagation();
    });
});

files.forEach(file => {
    file.addEventListener('dragstart', (event) => {
        event.dataTransfer.setData('text/plain', file.getAttribute('data-file-id'));
        event.dataTransfer.effectAllowed = 'move';
        event.stopPropagation();
    });
});

	trashBin.addEventListener('dragover', (event) => {
	    event.preventDefault(); // 允许放置
	});
	
	trashBin.addEventListener('drop', (event) => {
	    event.preventDefault(); // 防止默认行为
	
	    const Id = event.dataTransfer.getData('text/plain');
	     
			//console.log("stai cancellando"+ Id);
            //deleteFolder(Id); // 调用删除文件夹的处理函数
            showConfirmDeleteModal(Id);
       });
       
    let idToDelete = null; // Variabile per conservare l'elemento da cancellare

	function showConfirmDeleteModal(itemId) {
	    idToDelete = itemId;
	    const modal = document.getElementById('confirmDeleteModal');
	    modal.style.display = 'block';
	}   
	
	function closeConfirmDeleteModal() {
	    const modal = document.getElementById('confirmDeleteModal');
	    modal.style.display = 'none';
	}
	
	document.getElementById('confirmDeleteBtn').onclick = function() {
    	
    	deleteFolder(idToDelete);
	   	closeConfirmDeleteModal();
	};

document.getElementById('cancelDeleteBtn').onclick = function() {
    closeConfirmDeleteModal();
};

document.querySelector('.closeConfirmDelete').onclick = closeConfirmDeleteModal;
	
	function deleteFolder(Id) {
		
		//const data = "folderId=" + encodeURIComponent(Id); // 准备数据
		var url = "Delete?id=" + encodeURIComponent(Id);
	        makeCall('POST', url, null, function(xhr) {
	            if (xhr.readyState === XMLHttpRequest.DONE) {
	                if (xhr.status === 200) {
	                    alert("Folder deleted successfully!");
	                    window.location.href = "HomePage";
	                } else {
	                    alert("Error deleting folder: " + xhr.responseText);
	                }
	            }
	        });
	}
	
	
	document.addEventListener('DOMContentLoaded', function() {
    const visitButtons = document.querySelectorAll('.visit-button');

    visitButtons.forEach(button => {
        button.addEventListener('click', function() {
            const fileId = this.getAttribute('data-file-id');
            openFileDetailsModal(fileId);
        });
    });

    function openFileDetailsModal(fileId) {
        const modal = document.getElementById("viewFileModal");
        modal.style.display = "block";

        // Fai una chiamata alla servlet per ottenere i dettagli del file
        var url = 'VisitaFile?id=' + encodeURIComponent(fileId);
        makeCall('GET', url, null, function(xhr) {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    //document.getElementById("fileDetails").innerHTML = xhr.responseText;
                    const fileDetails = JSON.parse(xhr.responseText);
                    document.getElementById("proprietario").textContent = `Proprietario: ${fileDetails.proprietario}`;
                    document.getElementById("nome").textContent = `Nome: ${fileDetails.nome}`;
                    document.getElementById("data creazione").textContent = `Data Creazione: ${fileDetails.dataCreazione}`;
                    document.getElementById("tipo").textContent = `Tipo: ${fileDetails.tipo}`;
                    document.getElementById("sommario").textContent = `Sommario: ${fileDetails.sommario}`;
                } else {
                    document.getElementById("errorMsg").textContent = "Errore nel recupero dei dettagli del file.";
                }
            }
        });
    }

    function closeFileModal() {
        const modal = document.getElementById("viewFileModal");
        modal.style.display = "none";
    }

    document.querySelector(".close2").onclick = closeFileModal;

    window.onclick = function(event) {
        const modal = document.getElementById("viewFileModal");
        if (event.target === modal) {
            closeFileModal();
        }
    };
});

const rootButton = document.getElementById("createRootFolder");
rootButton.onclick = () =>openModal(null);

function moveFileToFolder(fileId, newParentId) {
    
    var url = 'MoveFile?id=' + fileId + '&parentId='+ newParentId;

    makeCall('POST', url, null, function(xhr) {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                alert("File spostato con successo!");
                window.location.reload(); // Ricarica la pagina per riflettere i cambiamenti
            } else {
                alert("Errore nello spostamento del file: " + xhr.responseText);
            }
        }
    });
}

