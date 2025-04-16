package br.com.fatec.vortismobile.venda.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoRespostaDTO {

    private Long id;
    private LocalDateTime dataVenda;
    private String clienteNome;
    private String status;
    private List<ItemDTO> produtos = new ArrayList<>();
    private double frete;
    private double total;
    private String endereco;
    private List<CartaoDTO> cartoes = new ArrayList<>();
    private String cupomPromocional;
    private String cupomTroca;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ItemDTO> produtos) {
        this.produtos = produtos;
    }

    public double getFrete() {
        return frete;
    }

    public void setFrete(double frete) {
        this.frete = frete;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<CartaoDTO> getCartoes() {
        return cartoes;
    }

    public void setCartoes(List<CartaoDTO> cartoes) {
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

    // Subclasse ItemDTO
    public static class ItemDTO {
        private String produtoNome;
        private int quantidade;
        private double preco;
        private double subtotal;

        public String getProdutoNome() {
            return produtoNome;
        }

        public void setProdutoNome(String produtoNome) {
            this.produtoNome = produtoNome;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public double getPreco() {
            return preco;
        }

        public void setPreco(double preco) {
            this.preco = preco;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }
    }

    // Subclasse CartaoDTO
    public static class CartaoDTO {
        private String finalNumero;
        private double valor;

        public String getFinalNumero() {
            return finalNumero;
        }

        public void setFinalNumero(String finalNumero) {
            this.finalNumero = finalNumero;
        }

        public double getValor() {
            return valor;
        }

        public void setValor(double valor) {
            this.valor = valor;
        }
    }
}