package br.com.fatec.vortismobile.venda.dto;

import java.util.List;

public class VendaDTO {
    private Long idCliente;
    private Long idEnderecoEntrega;
    private List<ItemDTO> itens;

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
}
