package br.com.fatec.vortismobile.carrinho.controlador;

import br.com.fatec.vortismobile.carrinho.dto.FreteRequestDTO;
import br.com.fatec.vortismobile.carrinho.servico.FreteServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/frete")
@CrossOrigin(origins = "*")
public class FreteControlador {

    @Autowired
    private FreteServico freteServico;

    @PostMapping("/calcular")
    public ResponseEntity<Double> calcular(@RequestBody FreteRequestDTO dto) {
        double frete = freteServico.calcularFrete(dto.getItens(), dto.getCepEntrega());
        return ResponseEntity.ok(frete);
    }
}
