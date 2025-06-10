package br.com.fatec.vortismobile.produto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizacaoStatusDTO {
    private String status;
    private String motivo;
    private String justificativa;

}
