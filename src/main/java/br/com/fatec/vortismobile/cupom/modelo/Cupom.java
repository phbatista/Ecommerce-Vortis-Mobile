package br.com.fatec.vortismobile.cupom.modelo;

import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tb_cupom")
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false)
    private String tipo; // "PROMOCIONAL" ou "TROCA"

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // null para cupons promocionais

    private LocalDate dataValidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

}
