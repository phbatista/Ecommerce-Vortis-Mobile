package br.com.fatec.vortismobile.produto.dto;

import java.util.List;

public class ProdutoComCategoriaDTO {
    private Long id;
    private String nome;
    private Double precoVenda;
    private String imagem;
    private List<String> categorias;

    public ProdutoComCategoriaDTO(Long id, String nome, Double precoVenda, String imagem, List<String> categorias) {
        this.id = id;
        this.nome = nome;
        this.precoVenda = precoVenda;
        this.imagem = imagem;
        this.categorias = categorias;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Double getPrecoVenda() { return precoVenda; }
    public String getImagem() { return imagem; }
    public List<String> getCategorias() { return categorias; }
}