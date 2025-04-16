package br.com.fatec.vortismobile.estoque.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstoqueViewControlador {

    @GetMapping("/estoque")
    public String estoquePage() {
        return "estoque/estoque";
    }

}
