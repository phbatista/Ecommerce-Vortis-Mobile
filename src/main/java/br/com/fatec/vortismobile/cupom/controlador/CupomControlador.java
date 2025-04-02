package br.com.fatec.vortismobile.cupom.controlador;

import br.com.fatec.vortismobile.cupom.modelo.Cupom;
import br.com.fatec.vortismobile.cupom.repositorio.CupomRepositorio;
import br.com.fatec.vortismobile.cupom.servico.CupomServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cupons")
@CrossOrigin(origins = "*")
public class CupomControlador {

    @Autowired
    private CupomServico cupomServico;

    @Autowired
    private CupomRepositorio cupomRepositorio;

    @GetMapping
    public List<Cupom> listar() {
        return cupomServico.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return cupomServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cupom> criar(@RequestBody Cupom cupom) {
        return ResponseEntity.ok(cupomServico.salvar(cupom));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Cupom novoCupom) {
        return cupomServico.buscarPorId(id).map(cupom -> {
            cupom.setCodigo(novoCupom.getCodigo());
            cupom.setValor(novoCupom.getValor());
            cupom.setTipo(novoCupom.getTipo());
            cupom.setAtivo(novoCupom.isAtivo());
            cupom.setDataValidade(novoCupom.getDataValidade());
            cupom.setCliente(novoCupom.getCliente());
            return ResponseEntity.ok(cupomServico.salvar(cupom));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        cupomServico.deletar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validar(@PathVariable String codigo, @RequestParam Long idCliente) {
        Optional<Cupom> cupom = cupomServico.validarCupom(codigo, idCliente);
        return cupom.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}