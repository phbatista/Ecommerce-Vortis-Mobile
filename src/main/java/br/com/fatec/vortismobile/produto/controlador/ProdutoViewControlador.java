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

    @GetMapping("catalogo")
    public String catalogo() {return "catalogo/catalogo";}

    @GetMapping("produtos_edicao")
    public String produtoEdicao() {return "produtos/produtos_edicao";}

    @GetMapping("/produto.html")
    public String produtoPage() {return "produtos/produto";}

}