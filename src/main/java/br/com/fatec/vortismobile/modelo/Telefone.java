package br.com.fatec.vortismobile.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Telefone {

    @Column(name = "telefone_tipo", nullable = false, length = 10)
    private String tipo;

    @Column(name = "telefone_ddd", nullable = false, length = 3)
    private String ddd;

    @Column(name = "telefone_numero", nullable = false, length = 10)
    private String numero;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}

