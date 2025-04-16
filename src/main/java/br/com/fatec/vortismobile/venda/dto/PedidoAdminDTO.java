package br.com.fatec.vortismobile.venda.dto;

import java.util.List;

public class PedidoAdminDTO {
    private Long id;
    private String clienteNome;
    private String data;
    private String enderecoEntrega;
    private double frete;
    private double total;
    private List<ItemDTO> itens;
    private List<CartaoDTO> cartoes;
    private String cupomPromocional;
    private String cupomTroca;

    // inner DTOs
    public static class ItemDTO {
        private String nomeProduto;
        private int quantidade;
        private double precoUnitario;
    }

    public static class CartaoDTO {
        private String bandeira;
        private String finalCartao;
        private double valor;
    }
}
