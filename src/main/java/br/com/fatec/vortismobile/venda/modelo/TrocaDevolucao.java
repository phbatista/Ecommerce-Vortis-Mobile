package br.com.fatec.vortismobile.venda.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "tb_troca_devolucao")
public class TrocaDevolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // "TROCA" ou "DEVOLUCAO"
    private String status; // "TROCA SOLICITADA", "TROCA CONCLUIDA", etc.
    private LocalDateTime dataSolicitacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @JsonIgnoreProperties({"venda"})
    @OneToMany(mappedBy = "trocaDevolucao", cascade = CascadeType.ALL)
    private List<ItemTrocaDevolucao> itens;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public List<ItemTrocaDevolucao> getItens() {
        return itens;
    }

    public void setItens(List<ItemTrocaDevolucao> itens) {
        this.itens = itens;
    }
}
