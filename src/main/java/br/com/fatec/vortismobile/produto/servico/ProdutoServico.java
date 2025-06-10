package br.com.fatec.vortismobile.produto.servico;

import br.com.fatec.vortismobile.produto.dto.ProdutoDTO;
import br.com.fatec.vortismobile.produto.modelo.Categoria;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.CategoriaRepositorio;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoServico {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    public void atualizarStatus(Long id, String novoStatus, String motivo, String justificativa) {
        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setStatus(Produto.StatusProduto.valueOf(novoStatus.toUpperCase()));
        produto.setMotivo(motivo);
        produto.setJustificativa(justificativa);

        produtoRepositorio.save(produto);
    }

    public void salvar(ProdutoDTO dto) throws IOException {
        Produto produto = new Produto();

        produto.setNome(dto.getNome());
        produto.setDisponibilidadeAno(dto.getDisponibilidadeAno());
        produto.setChipset(dto.getChipset());
        produto.setSistemaOperacional(dto.getSistemaOperacional());
        produto.setBateria(dto.getBateria());
        produto.setResolucaoCamera(dto.getResolucaoCamera());
        produto.setGpu(dto.getGpu());
        produto.setTipoTela(dto.getTipoTela());
        produto.setTamanhoTela(dto.getTamanhoTela());
        produto.setPeso(dto.getPeso());
        produto.setAltura(dto.getAltura());
        produto.setLargura(dto.getLargura());
        produto.setProfundidade(dto.getProfundidade());
        produto.setMemoriaRam(dto.getMemoriaRam());
        produto.setArmazenamento(dto.getArmazenamento());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setGrupoPrecificacao(dto.getGrupoPrecificacao());
        produto.setStatus(Produto.StatusProduto.ATIVO);
        produto.setMotivo("CADASTRO");
        produto.setJustificativa("Novo Cadastro");

        MultipartFile imagem = dto.getImagem();
        if (imagem != null && !imagem.isEmpty()) {
            try {
                String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
                Path caminho = Paths.get("src/main/resources/imagens/produtos/" + nomeArquivo);
                Files.createDirectories(caminho.getParent());
                Files.write(caminho, imagem.getBytes());

                produto.setImagem(nomeArquivo); // salva só o nome no banco
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar imagem", e);
            }
        }

        List<Categoria> categorias = new ArrayList<>();
        if (dto.getNomesCategorias() != null) {
            categorias = dto.getNomesCategorias().stream()
                    .map(nome -> categoriaRepositorio.findByNome(nome)
                            .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + nome)))
                    .toList();
        }
        produto.setCategorias(categorias);

        produtoRepositorio.save(produto);

    }

    //RF0011
    public void atualizar(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(dto.getNome());
        produto.setDisponibilidadeAno(dto.getDisponibilidadeAno());
        produto.setChipset(dto.getChipset());
        produto.setSistemaOperacional(dto.getSistemaOperacional());
        produto.setBateria(dto.getBateria());
        produto.setResolucaoCamera(dto.getResolucaoCamera());
        produto.setGpu(dto.getGpu());
        produto.setTipoTela(dto.getTipoTela());
        produto.setTamanhoTela(dto.getTamanhoTela());
        produto.setPeso(dto.getPeso());
        produto.setAltura(dto.getAltura());
        produto.setLargura(dto.getLargura());
        produto.setProfundidade(dto.getProfundidade());
        produto.setMemoriaRam(dto.getMemoriaRam());
        produto.setArmazenamento(dto.getArmazenamento());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setGrupoPrecificacao(dto.getGrupoPrecificacao());
        produto.setStatus(Produto.StatusProduto.valueOf(dto.getStatus()));
        produto.setMotivo(dto.getMotivo());
        produto.setJustificativa(dto.getJustificativa());

        MultipartFile imagem = dto.getImagem();
        if (imagem != null && !imagem.isEmpty()) {
            try {
                String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
                Path caminho = Paths.get("src/main/resources/imagens/produtos/" + nomeArquivo);
                Files.createDirectories(caminho.getParent());
                Files.write(caminho, imagem.getBytes());

                produto.setImagem(nomeArquivo);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao atualizar imagem", e);
            }
        }

        List<Categoria> categorias = new ArrayList<>();
        if (dto.getNomesCategorias() != null) {
            categorias = dto.getNomesCategorias().stream()
                    .map(nome -> categoriaRepositorio.findByNome(nome)
                            .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + nome)))
                    .toList();
        }

        produto.setCategorias(new ArrayList<>());
        produto.getCategorias().addAll(categorias);
        produtoRepositorio.save(produto);
    }

    public List<Produto> filtrar(String nome, Integer ano, String armazenamento, String status) {
        return produtoRepositorio.findAll().stream()
                .filter(p -> nome == null || p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .filter(p -> ano == null || p.getDisponibilidadeAno().equals(ano))
                .filter(p -> armazenamento == null || p.getArmazenamento().equalsIgnoreCase(armazenamento))
                .filter(p -> status == null || p.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }
}
