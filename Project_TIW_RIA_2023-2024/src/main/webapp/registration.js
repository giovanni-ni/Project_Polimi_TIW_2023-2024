
  document.getElementById("submitbutton").addEventListener('click', (e) => {
     e.preventDefault();
    var form = e.target.closest("form");
    var password = document.getElementById("password").value;
    var repeatPassword = document.getElementById("Rpassword").value;
    const email = document.getElementById("email").value;
    var errorMessage = document.getElementById("errormessage");
    
     errorMessage.textContent = '';
    
    var emailPattern = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/i;
    var passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    
    
    // Validate email format
    if (!emailPattern.test(email)) {
      errorMessage.textContent = "Inserisci un'email valida.";
      return;
    }
    
    if (!passwordPattern.test(password)) {
	  errorMessage.textContent = "La password deve contenere almeno 8 caratteri, una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.";
	  return;
	}	
    
    if (password !== repeatPassword) {
      errorMessage.textContent = "Le password non coincidono.";
      return;
    }
    
    if (form.checkValidity()) {
		
		const username = document.getElementById("username").value;
		
		var url = "register?username="+username+"&email="+email+"&password="+password;
      makeCall("POST", url, e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
                window.location.href = "index.html";
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
    	 form.reportValidity();
    }
  });
  
  function makeCall(method, url, data, callback) {
    var x = new XMLHttpRequest();
    x.open(method, url, true);
    x.onreadystatechange = function() {
        callback(x); // 调用回调函数并传递 XMLHttpRequest 对象
    };
    if (method === 'POST') {
        x.send(data);
    } else { // per GET o altre richieste senza dati nel corpo
        x.send();
    }
}
