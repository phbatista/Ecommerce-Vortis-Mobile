package br.com.fatec.vortismobile.produto.controlador;

import br.com.fatec.vortismobile.produto.dto.ProdutoDTO;
import br.com.fatec.vortismobile.produto.modelo.Categoria;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.CategoriaRepositorio;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoControlador {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @PostMapping
    public ResponseEntity<?>
    criarProduto(@RequestParam("imagem") MultipartFile imagem,
                 @RequestParam("nome") String nome,
                 @RequestParam("disponibilidadeAno") Integer disponibilidadeAno,
                 @RequestParam("chipset") String chipset,
                 @RequestParam("sistemaOperacional") String sistemaOperacional,
                 @RequestParam("bateria") String bateria,
                 @RequestParam("resolucaoCamera") String resolucaoCamera,
                 @RequestParam("gpu") String gpu,
                 @RequestParam("tipoTela") String tipoTela,
                 @RequestParam("tamanhoTela") Double tamanhoTela,
                 @RequestParam("peso") Double peso,
                 @RequestParam("altura") Double altura,
                 @RequestParam("largura") Double largura,
                 @RequestParam("profundidade") Double profundidade,
                 @RequestParam("memoriaRam") String memoriaRam,
                 @RequestParam("armazenamento") String armazenamento,
                 @RequestParam("codigoBarras") String codigoBarras,
                 @RequestParam("fabricante") String fabricante,
                 @RequestParam("grupoPrecificacao") String grupoPrecificacao,
                 @RequestParam("precoCusto") Double precoCusto,
                 @RequestParam("precoVenda") Double precoVenda,
                 @RequestParam("status") String status,
                 @RequestParam("motivo") String motivo,
                 @RequestParam("justificativa") String justificativa,
                 @RequestParam("categorias") List<String> categorias) {

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDisponibilidadeAno(disponibilidadeAno);
        produto.setChipset(chipset);
        produto.setSistemaOperacional(sistemaOperacional);
        produto.setBateria(bateria);
        produto.setResolucaoCamera(resolucaoCamera);
        produto.setGpu(gpu);
        produto.setTipoTela(tipoTela);
        produto.setTamanhoTela(tamanhoTela);
        produto.setPeso(peso);
        produto.setAltura(altura);
        produto.setLargura(largura);
        produto.setProfundidade(profundidade);
        produto.setMemoriaRam(memoriaRam);
        produto.setArmazenamento(armazenamento);
        produto.setCodigoBarras(codigoBarras);
        produto.setFabricante(fabricante);
        produto.setGrupoPrecificacao(grupoPrecificacao);
        produto.setPrecoCusto(precoCusto);
        produto.setPrecoVenda(precoVenda);
        produto.setStatus(status);
        produto.setMotivo(motivo);
        produto.setJustificativa(justificativa);

        // salvar imagem (se quiser posso te ajudar com isso também)
        // salvar produto
        Produto salvo = produtoRepositorio.save(produto);

        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public List<Produto> listarProdutos() {
        return produtoRepositorio.findAll();
    }
}
