package br.com.fatec.vortismobile.estoque.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EstoqueDTO {

    private Long idProduto;
    private LocalDate dataEntrada;
    private String fornecedor;
    private Double custoUnitario;
    private Integer quantidade;
    private double precoVenda;
}