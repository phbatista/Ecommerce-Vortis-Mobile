package br.com.fatec.vortismobile.estoque.servico;

import br.com.fatec.vortismobile.estoque.dto.EstoqueDTO;
import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.estoque.repositorio.EstoqueRepositorio;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueServico {

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    public Estoque salvar(EstoqueDTO dto) {
        Produto produto = produtoRepositorio.findById(dto.getIdProduto())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setDataEntrada(dto.getDataEntrada());
        estoque.setFornecedor(dto.getFornecedor());
        estoque.setCustoUnitario(dto.getCustoUnitario());
        estoque.setQuantidade(dto.getQuantidade());

        return estoqueRepositorio.save(estoque);
    }

    public List<Estoque> listarTodos() {
        return estoqueRepositorio.findAll();
    }

    public Optional<Estoque> buscarPorId(Long id) {
        return estoqueRepositorio.findById(id);
    }

    public void deletar(Long id) {
        estoqueRepositorio.deleteById(id);
    }

    public List<Estoque> buscarPorProdutoId(Long produtoId) {
        return estoqueRepositorio.findByProdutoId(produtoId);
    }
}