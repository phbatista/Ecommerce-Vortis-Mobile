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
    public ResponseEntity<Long> criarVenda(@RequestBody VendaDTO dto) {
        Long idVenda = vendaServico.criarVenda(dto); // retorna o ID
        return ResponseEntity.ok(idVenda); // envia o ID de volta pro front
    }
}