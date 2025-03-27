package br.com.fatec.vortismobile.carrinho.dto;

import br.com.fatec.vortismobile.venda.dto.VendaDTO;

import java.util.List;

public class FreteRequestDTO {
    private List<VendaDTO.ItemDTO> itens;
    private String cepEntrega;

    public List<VendaDTO.ItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<VendaDTO.ItemDTO> itens) {
        this.itens = itens;
    }

    public String getCepEntrega() {
        return cepEntrega;
    }

    public void setCepEntrega(String cepEntrega) {
        this.cepEntrega = cepEntrega;
    }
}
