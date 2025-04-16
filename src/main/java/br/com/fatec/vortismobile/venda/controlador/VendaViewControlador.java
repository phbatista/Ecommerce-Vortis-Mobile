package br.com.fatec.vortismobile.venda.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendaViewControlador {

    @GetMapping("/carrinho")
    public String vendaPage() {
        return "carrinho/carrinho";
    }

    @GetMapping("/pedidos")
    public String pedidosPage() {return "pedidos/pedidos";}

    @GetMapping("/pedidos_admin")
    public String pedidosAdminPage() {return "pedidos/pedidos_admin";}

    @GetMapping("/trocas")
    public String trocasPage() {return "trocas/trocas";}
}
