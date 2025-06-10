package br.com.fatec.vortismobile.notificacao.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificacaoViewControlador {

    @GetMapping("/notificacoes")
    public String notificacoes() {return "notificacao/notificacao";}
}