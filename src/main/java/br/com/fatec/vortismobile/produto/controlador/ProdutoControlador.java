package br.com.fatec.vortismobile.produto.controlador;

import br.com.fatec.vortismobile.produto.dto.AtualizacaoStatusDTO;
import br.com.fatec.vortismobile.produto.dto.ProdutoDTO;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import br.com.fatec.vortismobile.produto.servico.ProdutoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoControlador {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private ProdutoServico produtoServico;

    //RF0011
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> cadastrar(@ModelAttribute ProdutoDTO dto) throws IOException {
        produtoServico.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoRepositorio.findAll();
        return ResponseEntity.ok(produtos);
    }

    //RF0015
    @GetMapping("/filtro")
    public ResponseEntity<List<Produto>> filtrarProdutos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer disponibilidadeAno,
            @RequestParam(required = false) String armazenamento,
            @RequestParam(required = false) String status
    ) {
        List<Produto> filtrados = produtoServico.filtrar(nome, disponibilidadeAno, armazenamento, status);
        return ResponseEntity.ok(filtrados);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizacaoStatusDTO dto) {
        produtoServico.atualizarStatus(id, dto.getStatus(), dto.getMotivo(), dto.getJustificativa());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/imagem/{nome}")
    public ResponseEntity<Resource> servirImagem(@PathVariable String nome) throws IOException {
        Path caminho = Paths.get("src/main/resources/imagens/produtos/" + nome);
        if (!Files.exists(caminho)) return ResponseEntity.notFound().build();

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(caminho));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    //RF0014
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @ModelAttribute ProdutoDTO dto) {
        produtoServico.atualizar(id, dto);
        return ResponseEntity.ok().build();
    }
}