package br.com.fatec.vortismobile.estoque.modelo;

import br.com.fatec.vortismobile.produto.modelo.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tb_estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(name = "custo_unitario", nullable = false)
    private BigDecimal custoUnitario;

    @Column(name = "preco_venda", nullable = false)
    private BigDecimal precoVenda;

    @Column(name = "fornecedor", nullable = false)
    private String fornecedor;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

}