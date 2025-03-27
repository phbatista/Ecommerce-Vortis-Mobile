package br.com.fatec.vortismobile.venda.controlador;

import br.com.fatec.vortismobile.venda.dto.VendaDTO;
import br.com.fatec.vortismobile.venda.servico.VendaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaControlador {

    @Autowired
    private VendaServico vendaServico;

    @PostMapping
    public ResponseEntity<?> criarVenda(@RequestBody VendaDTO dto) {
        vendaServico.criarVenda(dto);
        return ResponseEntity.ok().build();
    }
}