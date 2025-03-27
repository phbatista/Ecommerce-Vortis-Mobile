package br.com.fatec.vortismobile.produto.modelo;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_produto_categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToMany(mappedBy = "categorias")
    private List<Produto> produtos;

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria(String cat, Produto produto) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }
}
