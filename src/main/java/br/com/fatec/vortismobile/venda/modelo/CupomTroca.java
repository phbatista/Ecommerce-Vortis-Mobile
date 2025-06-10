package br.com.fatec.vortismobile.venda.modelo;

import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="tb_cupom_troca")
public class CupomTroca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnore
    private Cliente cliente;

    private BigDecimal valor;
    private boolean usado;
    private LocalDateTime dataGeracao = LocalDateTime.now();
    private LocalDateTime dataValidade = LocalDateTime.now().plusMonths(6);

}