// Carrega a navbar dinamicamente
fetch('../navbar/navbar.html')
    .then(res => res.text())
    .then(data => {
        document.getElementById('navbar-placeholder').innerHTML = data;

        // Após carregar o HTML da navbar, atualiza menu com base no login
        const idCliente = sessionStorage.getItem("idCliente");
        const nomeCliente = sessionStorage.getItem("nomeCliente");
        const menuUsuario = document.getElementById("menuUsuario");

        if (menuUsuario) {
            if (idCliente && nomeCliente) {
                menuUsuario.innerHTML = `
          <li class="nav-item">
            <span class="nav-link text-white">Olá, ${nomeCliente}</span>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#" onclick="logout()">Sair</a>
          </li>
        `;
            } else {
                menuUsuario.innerHTML = `
          <li class="nav-item">
            <a class="nav-link" href="/clientes_login">Fazer Login</a>
          </li>
        `;
            }
        }
    });

function logout() {
    sessionStorage.clear();
    window.location.href = "/clientes_login";
}