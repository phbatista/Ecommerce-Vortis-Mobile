package br.com.fatec.vortismobile.venda.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendaViewControlador {

    @GetMapping("/carrinho")
    public String vendaPage() {
        return "carrinho/carrinho";
    }
}
