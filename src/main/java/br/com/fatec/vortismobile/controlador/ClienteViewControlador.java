package br.com.fatec.vortismobile.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteViewControlador {

    @GetMapping("/clientes_cadastro")
    public String clientesPage() {
        return "clientes_cadastro";
    }

    @GetMapping("/clientes_lista")
    public String clientesListaPage() {
        return "clientes_lista";
    }

    @GetMapping("/clientes_editar")
    public String clientesEditPage() {
        return "clientes_editar";
    }

    @GetMapping("/clientes_editar_dados")
    public String clientesEditDadosPage() {
        return "clientes_editar_dados";
    }

    @GetMapping("clientes_editar_enderecos")
    public String clientesEditarEnderecoPage() {return "clientes_editar_enderecos";}

    @GetMapping("clientes_editar_cartoes")
    public String clientesEditarCartoesPage() {return "clientes_editar_cartoes";}

    @GetMapping("clientes_editar_senha")
    public String clientesEditarSenhaPage() {return "clientes_editar_senha";}

}