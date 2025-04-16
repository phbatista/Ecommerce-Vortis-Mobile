package br.com.fatec.vortismobile.venda.dto;

import java.util.List;

public class VendaDTO {

    private Long idCliente;
    private Long idEnderecoEntrega;
    private List<ItemDTO> itens;
    private List<CartaoPagamentoDTO> cartoes;
    private String cupomPromocional;
    private String cupomTroca;

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdEnderecoEntrega() {
        return idEnderecoEntrega;
    }

    public void setIdEnderecoEntrega(Long idEnderecoEntrega) {
        this.idEnderecoEntrega = idEnderecoEntrega;
    }

    public List<ItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemDTO> itens) {
        this.itens = itens;
    }

    public List<CartaoPagamentoDTO> getCartoes() {
        return cartoes;
    }

    public void setCartoes(List<CartaoPagamentoDTO> cartoes) {
        this.cartoes = cartoes;
    }

    public String getCupomPromocional() {
        return cupomPromocional;
    }

    public void setCupomPromocional(String cupomPromocional) {
        this.cupomPromocional = cupomPromocional;
    }

    public String getCupomTroca() {
        return cupomTroca;
    }

    public void setCupomTroca(String cupomTroca) {
        this.cupomTroca = cupomTroca;
    }

    // DTO interno para itens
    public static class ItemDTO {
        private Long idProduto;
        private int quantidade;

        public Long getIdProduto() {
            return idProduto;
        }

        public void setIdProduto(Long idProduto) {
            this.idProduto = idProduto;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    }

    // DTO interno para cartões
    public static class CartaoPagamentoDTO {
        private Long idCartao;
        private double valor;

        public Long getIdCartao() {
            return idCartao;
        }

        public void setIdCartao(Long idCartao) {
            this.idCartao = idCartao;
        }

        public double getValor() {
            return valor;
        }

        public void setValor(double valor) {
            this.valor = valor;
        }
    }
}