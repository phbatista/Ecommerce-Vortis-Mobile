package br.com.fatec.vortismobile.venda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaResumoDTO {

    private String nomeProdutoOuCategoria;
    private LocalDate dataVenda;
    private int quantidadeVendida;
}