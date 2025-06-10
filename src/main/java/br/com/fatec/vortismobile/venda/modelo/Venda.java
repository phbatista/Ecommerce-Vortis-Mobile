package br.com.fatec.vortismobile.venda.modelo;

import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import br.com.fatec.vortismobile.cliente.modelo.Endereco;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    private double frete;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "endereco_entrega_id", nullable = false)
    private Endereco enderecoEntrega;

    @JsonIgnoreProperties({"itens", "cartoes", "cliente", "enderecoEntrega"})

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    private List<ItemVenda> itens;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    private List<VendaCartao> cartoes;

    @Column(name = "status")
    private String status;

    @Column(name = "cupom_promocional")
    private String cupomPromocional;

    @Column(name = "cupom_troca")
    private String cupomTroca;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public double getFrete() {
        return frete;
    }

    public void setFrete(double frete) {
        this.frete = frete;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public List<VendaCartao> getCartoes() {
        return cartoes;
    }

    public void setCartoes(List<VendaCartao> cartoes) {
        this.cartoes = cartoes;
    }

    public String getCupomPromocional() {
        return cupomPromocional;
    }

    public void setCupomPromocional(String cupomPromocional) {
        this.cupomPromocional = cupomPromocional;
    }

    public String getCupomTroca() {
        return cupomTroca;
    }

    public void setCupomTroca(String cupomTroca) {
        this.cupomTroca = cupomTroca;
    }

    private Double total;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

}