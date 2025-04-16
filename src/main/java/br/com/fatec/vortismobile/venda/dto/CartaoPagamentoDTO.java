package br.com.fatec.vortismobile.venda.dto;

public class CartaoPagamentoDTO {

    private Long idCartao;
    private Double valor;

    public Long getIdCartao() {
        return idCartao;
    }

    public void setIdCartao(Long idCartao) {
        this.idCartao = idCartao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}