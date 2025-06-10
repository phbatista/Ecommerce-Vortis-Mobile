package br.com.fatec.vortismobile.estoque.controlador;

import br.com.fatec.vortismobile.estoque.dto.EstoqueDTO;
import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.estoque.repositorio.EstoqueRepositorio;
import br.com.fatec.vortismobile.estoque.servico.EstoqueServico;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
public class EstoqueControlador {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    @Autowired
    private EstoqueServico estoqueServico;

    @PostMapping
    public ResponseEntity<Estoque> salvar(@RequestBody EstoqueDTO dto) {
        Estoque estoque = estoqueServico.salvar(dto);
        return ResponseEntity.ok(estoque);
    }

    @GetMapping("/produto/{idProduto}")
    public List<Estoque> listarPorProduto(@PathVariable Long idProduto) {
        Produto produto = produtoRepositorio.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return estoqueRepositorio.findByProdutoOrderByDataEntradaAsc(produto);
    }

}