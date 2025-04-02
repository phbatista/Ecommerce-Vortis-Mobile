package br.com.fatec.vortismobile.cupom.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CupomViewControlador {

    @GetMapping("/cupons")
    public String cupom() {return "cupons/cupons";}
}