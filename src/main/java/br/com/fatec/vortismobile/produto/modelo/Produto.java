package br.com.fatec.vortismobile.produto.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long id;

    private String nome;
    private Integer disponibilidadeAno;
    private String chipset;
    private String sistemaOperacional;
    private String bateria;
    private String resolucaoCamera;
    private String gpu;
    private String tipoTela;
    private Double tamanhoTela;
    private Double peso;
    private Double altura;
    private Double largura;
    private Double profundidade;
    private String memoriaRam;
    private String armazenamento;

    private String grupoPrecificacao;

    @Column(unique = true)
    private String codigoBarras;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProduto status;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private String justificativa;

    @Column(name = "imagem")
    private String imagem; // salvar apenas o nome ou caminho do arquivo

    @ManyToMany
    @JoinTable(name = "tb_categoria_produto",
            joinColumns = @JoinColumn(name = "id_produto"),
            inverseJoinColumns = @JoinColumn(name = "id_categoria"))
    private List<Categoria> categorias;

    public enum StatusProduto {
        ATIVO, INATIVO, FORA_DE_MERCADO
    }

}