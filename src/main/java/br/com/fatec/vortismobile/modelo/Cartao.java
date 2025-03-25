package br.com.fatec.vortismobile.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cartao")
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cartao")
    private Long id;

    @Column(name = "bandeira", nullable = false)
    private String cartaoBandeira;

    @Column(name = "numero_cartao", nullable = false)
    private String cartaoNumero;

    @Column(name = "validade", nullable = false)
    private String cartaoValidade;

    @Column(name = "cvv", nullable = false)
    private String cartaoCVV;

    @Column(name = "nome_cartao", nullable = false)
    private String nomeCartao;

    @Column(name = "principal", nullable = false)
    private boolean cartaoPrincipal;

    @Column(name = "insert_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime insertDate;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonBackReference
    private Cliente cliente;

    public Cartao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCartaoBandeira() {
        return cartaoBandeira;
    }

    public void setCartaoBandeira(String cartaoBandeira) {
        this.cartaoBandeira = cartaoBandeira;
    }

    public String getCartaoNumero() {
        return cartaoNumero;
    }

    public void setCartaoNumero(String cartaoNumero) {
        this.cartaoNumero = cartaoNumero;
    }

    public String getCartaoValidade() {
        return cartaoValidade;
    }

    public void setCartaoValidade(String cartaoValidade) {
        this.cartaoValidade = cartaoValidade;
    }

    public String getCartaoCVV() {
        return cartaoCVV;
    }

    public void setCartaoCVV(String cartaoCVV) {
        this.cartaoCVV = cartaoCVV;
    }

    public String getNomeCartao() {
        return nomeCartao;
    }

    public void setNomeCartao(String nomeCartao) {
        this.nomeCartao = nomeCartao;
    }

    public boolean isCartaoPrincipal() {
        return cartaoPrincipal;
    }

    public void setCartaoPrincipal(boolean cartaoPrincipal) {
        this.cartaoPrincipal = cartaoPrincipal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getInsertDate() {
        return insertDate;
    }
}
