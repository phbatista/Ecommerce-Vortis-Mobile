package br.com.fatec.vortismobile.carrinho.servico;

import br.com.fatec.vortismobile.venda.dto.VendaDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FreteServico {

    public double calcularFrete(List<VendaDTO.ItemDTO> itens, String cepDestino) {
        double valorPorItem = 10.0;
        int totalItens = itens.stream().mapToInt(VendaDTO.ItemDTO::getQuantidade).sum();

        double fatorCep = cepDestino.startsWith("01") ? 1.0 : 1.5; // Exemplo

        return totalItens * valorPorItem * fatorCep;
    }
}
