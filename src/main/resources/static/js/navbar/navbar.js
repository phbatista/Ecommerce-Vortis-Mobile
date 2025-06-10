// Carrega o HTML da navbar
fetch('/navbar/navbar.html') // ajuste o caminho se necessário
    .then(res => res.text())
    .then(data => {
        document.getElementById('navbar-placeholder').innerHTML = data;

        // Dados da sessão
        const idCliente = sessionStorage.getItem("idCliente");
        const nomeCliente = sessionStorage.getItem("nomeCliente");
        const emailCliente = sessionStorage.getItem("emailCliente");

        const menuUsuario = document.getElementById("menuUsuario");
        const menuAdmin = document.getElementById("menuAdmin");

        // Preenche o menu do usuário
        if (menuUsuario) {
            if (idCliente && nomeCliente) {
                menuUsuario.innerHTML = `
          <li class="nav-item">
            <span class="nav-link text-white">Olá, ${nomeCliente}</span>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#" id="btnLogout">Sair</a>
          </li>
        `;

                // Exibe o menu admin somente para o e-mail do admin
                if (emailCliente === "admin@vortismobile.com" && menuAdmin) {
                    menuAdmin.style.display = "block";
                }

                // Registra ação do botão Sair
                document.getElementById("btnLogout").onclick = function () {
                    sessionStorage.clear();
                    window.location.href = "/clientes_login";
                };

            } else {
                menuUsuario.innerHTML = `
          <li class="nav-item">
            <a class="nav-link" href="/clientes_login">Fazer Login</a>
          </li>
        `;
            }
        }
    });