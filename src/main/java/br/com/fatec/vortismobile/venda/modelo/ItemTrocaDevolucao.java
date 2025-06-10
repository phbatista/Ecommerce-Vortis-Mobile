package br.com.fatec.vortismobile.venda.modelo;

import br.com.fatec.vortismobile.produto.modelo.Produto;
import jakarta.persistence.*;

@Entity
@Table(name="tb_item_troca_devolucao")
public class ItemTrocaDevolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantidade;

    private String motivo;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "troca_devolucao_id")
    private TrocaDevolucao trocaDevolucao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public TrocaDevolucao getTrocaDevolucao() {
        return trocaDevolucao;
    }

    public void setTrocaDevolucao(TrocaDevolucao trocaDevolucao) {
        this.trocaDevolucao = trocaDevolucao;
    }
}
