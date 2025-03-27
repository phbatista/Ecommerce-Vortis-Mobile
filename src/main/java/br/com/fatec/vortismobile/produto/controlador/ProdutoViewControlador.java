package br.com.fatec.vortismobile.produto.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProdutoViewControlador {

    @GetMapping("/produtos_cadastro")
    public String produtoCadastro() {
        return "produtos/produtos_cadastro";
    }

    @GetMapping("produtos_lista")
    public String produtoLista() {return "produtos/produtos_lista";}
}