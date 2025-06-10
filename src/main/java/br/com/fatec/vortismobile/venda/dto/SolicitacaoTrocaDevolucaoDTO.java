package br.com.fatec.vortismobile.venda.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitacaoTrocaDevolucaoDTO {

    private Long idPedido;
    private String tipo; // "TROCA" ou "DEVOLUCAO"
    private List<ItemTrocaDevolucaoDTO> itens;

    public static class ItemTrocaDevolucaoDTO {
        private Long idProduto;
        private int quantidade;
        private String motivo;

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

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<ItemTrocaDevolucaoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemTrocaDevolucaoDTO> itens) {
        this.itens = itens;
    }
}
