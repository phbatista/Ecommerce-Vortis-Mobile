package br.com.fatec.vortismobile.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Long id;

    @Column(name = "tipo_residencia", length = 20, nullable = false)
    private String tipoResidencia;

    @Column(name = "tipo_endereco", length = 20, nullable = false)
    private String tipoEndereco;

    @Column(name = "tipo_logradouro", length = 20, nullable = false)
    private String tipoLogradouro;

    @Column(name = "logradouro", length = 100, nullable = false)
    private String logradouro;

    @Column(name = "numero", length = 10, nullable = false)
    private String numero;

    @Column(name = "cep", length = 10, nullable = false)
    private String cep;

    @Column(name = "bairro", length = 50, nullable = false)
    private String bairro;

    @Column(name = "cidade", length = 50, nullable = false)
    private String cidade;

    @Column(name = "estado", length = 2, nullable = false)
    private String estado;

    @Column(name = "pais", length = 50, nullable = false)
    private String pais;

    @Column(name = "insert_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime insertDate;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonBackReference
    private Cliente cliente;

    public Endereco(String tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    public Endereco() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(String tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(String tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    public LocalDateTime getInsertDate() {
        return insertDate;
    }
}