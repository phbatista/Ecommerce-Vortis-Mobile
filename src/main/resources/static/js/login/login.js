document.addEventListener("DOMContentLoaded", () => {
    // Login
    const btnLogin = document.getElementById("btnLogin");
    if (btnLogin) {
        btnLogin.addEventListener("click", async () => {
            const email = document.getElementById("email").value;
            const senha = document.getElementById("senha").value;

            try {
                const resp = await fetch("http://localhost:8080/api/clientes/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, senha })
                });

                if (resp.ok) {
                    const cliente = await resp.json();
                    sessionStorage.setItem("idCliente", cliente.id);
                    window.location.href = "/catalogo";
                } else {
                    document.getElementById("erroLogin").textContent = "E-mail ou senha inválidos.";
                    document.getElementById("erroLogin").style.display = "block";
                }
            } catch (e) {
                console.error(e);
                alert("Erro ao fazer login.");
            }
        });
    }

    // Menu Login/Logout
    const idCliente = sessionStorage.getItem("idCliente");
    const menu = document.getElementById("menuUsuario");

    if (!menu) return;

    menu.innerHTML = idCliente ? `
            <li class="nav-item">
                <a class="nav-link" href="#" onclick="logout()">Sair</a>
            </li>
        ` : `
            <li class="nav-item">
                <a class="nav-link" href="/clientes_login">Login</a>
            </li>
        `;
});

function logout() {
    sessionStorage.clear();
    window.location.href = "/clientes_login";
}