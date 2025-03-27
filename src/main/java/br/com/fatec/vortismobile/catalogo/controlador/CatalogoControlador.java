package br.com.fatec.vortismobile.catalogo.controlador;

import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.servico.ProdutoServico;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CatalogoControlador {

    @Autowired
    private ProdutoServico produtoServico;

    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model) {
        List<Produto> produtos = produtoServico.listarTodos();
        model.addAttribute("produtos", produtos);
        return "catalogo/catalogo";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionarAoCarrinho(@RequestParam Long produtoId, HttpSession session) {
        Produto produto = produtoServico.buscarPorId(produtoId);
        List<Produto> carrinho = (List<Produto>) session.getAttribute("carrinho");
        if (carrinho == null) carrinho = new ArrayList<>();
        carrinho.add(produto);
        session.setAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }
}
