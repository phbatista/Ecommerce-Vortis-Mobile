package br.com.fatec.vortismobile.estoque.controlador;

import br.com.fatec.vortismobile.estoque.dto.EstoqueDTO;
import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.estoque.servico.EstoqueServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
public class EstoqueControlador {

    @Autowired
    private EstoqueServico estoqueServico;

    @PostMapping
    public ResponseEntity<Estoque> salvar(@RequestBody EstoqueDTO dto) {
        Estoque estoque = estoqueServico.salvar(dto);
        return ResponseEntity.ok(estoque);
    }

    @GetMapping
    public List<Estoque> listarTodos() {
        return estoqueServico.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estoque> buscarPorId(@PathVariable Long id) {
        return estoqueServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        estoqueServico.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/produto/{produtoId}")
    public List<Estoque> buscarPorProdutoId(@PathVariable Long produtoId) {
        return estoqueServico.buscarPorProdutoId(produtoId);
    }
}