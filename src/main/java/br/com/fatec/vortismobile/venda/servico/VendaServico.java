package br.com.fatec.vortismobile.venda.servico;

import br.com.fatec.vortismobile.cliente.modelo.Cartao;
import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import br.com.fatec.vortismobile.cliente.modelo.Endereco;
import br.com.fatec.vortismobile.cliente.repositorio.ClienteRepositorio;
import br.com.fatec.vortismobile.cupom.repositorio.CupomRepositorio;
import br.com.fatec.vortismobile.cupom.servico.CupomServico;
import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.estoque.repositorio.EstoqueRepositorio;
import br.com.fatec.vortismobile.notificacao.servico.NotificacaoServico;
import br.com.fatec.vortismobile.produto.modelo.Categoria;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import br.com.fatec.vortismobile.venda.dto.PedidoRespostaDTO;
import br.com.fatec.vortismobile.venda.dto.VendaDTO;
import br.com.fatec.vortismobile.venda.dto.VendaResumoDTO;
import br.com.fatec.vortismobile.venda.modelo.CupomTroca;
import br.com.fatec.vortismobile.venda.modelo.ItemVenda;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.modelo.VendaCartao;
import br.com.fatec.vortismobile.venda.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendaServico {

    @Autowired
    private NotificacaoServico notificacaoServico;

    @Autowired
    private CupomServico cupomServico;

    @Autowired
    private CupomRepositorio cupomRepositorio;

    @Autowired
    private CupomTrocaRepositorio cupomTrocaRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private VendaRepositorio vendaRepositorio;

    @Autowired
    private ItemVendaRepositorio itemVendaRepositorio;

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    @Autowired
    private CartaoRepositorio cartaoRepositorio;

    @Autowired
    private VendaCartaoRepositorio vendaCartaoRepositorio;

    public Long criarVenda(VendaDTO dto) {
        Cliente cliente = clienteRepositorio.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Endereco enderecoEntrega = cliente.getEnderecos().stream()
                .filter(e -> "Entrega".equalsIgnoreCase(e.getTipoEndereco()))
                .filter(e -> e.getId().equals(dto.getIdEnderecoEntrega()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Endereço de entrega inválido"));

        int totalItens = dto.getItens().stream().mapToInt(VendaDTO.ItemDTO::getQuantidade).sum();
        double frete = totalItens * 7.90;

        double totalProdutos = 0.0;
        for (VendaDTO.ItemDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepositorio.findById(itemDTO.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            int totalEmEstoque = estoqueRepositorio.findByProduto(produto).stream()
                    .mapToInt(Estoque::getQuantidade).sum();

            if (itemDTO.getQuantidade() > totalEmEstoque) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            Estoque estoque = estoqueRepositorio.findTopByProdutoOrderByDataEntradaDesc(produto)
                    .orElseThrow(() -> new RuntimeException("Estoque não encontrado para o produto: " + produto.getNome()));
            totalProdutos += estoque.getPrecoVenda().doubleValue() * itemDTO.getQuantidade();
        }

        double descontoPromo = 0.0;

        if (dto.getCupomPromocional() != null && !dto.getCupomPromocional().isBlank()) {
            var cupomOpt = cupomServico.validarCupom(dto.getCupomPromocional(), cliente.getId());
            if (cupomOpt.isPresent()) {
                var cupom = cupomOpt.get();
                if ("PROMOCIONAL".equalsIgnoreCase(cupom.getTipo())) {
                    descontoPromo = cupom.getValor();
                    System.out.println("Cupom aplicado: " + cupom.getCodigo() + " | Valor: " + cupom.getValor());
                } else {
                    throw new RuntimeException("Cupom promocional inválido.");
                }
            } else {
                throw new RuntimeException("Cupom promocional não encontrado.");
            }
        }

        double descontoTroca = 0.0;

        // Cupom de troca
        if (dto.getCupomTroca() != null && !dto.getCupomTroca().isBlank()) {
            CupomTroca cupom = cupomTrocaRepositorio.findByCodigoIgnoreCase(dto.getCupomTroca())
                    .orElseThrow(() -> new RuntimeException("Cupom de troca inválido."));

            if (!cupom.getCliente().getId().equals(cliente.getId())) {
                throw new RuntimeException("Este cupom não pertence ao cliente.");
            }

            if (cupom.isUsado()) {
                throw new RuntimeException("Este cupom já foi utilizado.");
            }

            if (cupom.getDataValidade().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Cupom expirado.");
            }

            descontoTroca = cupom.getValor().doubleValue();
            cupom.setUsado(true);
            cupomTrocaRepositorio.save(cupom);
        }

        double totalFinal = Math.max(totalProdutos + frete - descontoPromo - descontoTroca, 0);

        BigDecimal totalEsperado = BigDecimal.valueOf(totalFinal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal somaCartoes = dto.getCartoes().stream()
                .map(c -> BigDecimal.valueOf(c.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (somaCartoes.compareTo(totalEsperado) != 0) {
            System.out.println("DEBUG >>> Esperado: " + totalEsperado + " | Recebido: " + somaCartoes);
            throw new RuntimeException("A soma dos valores dos cartões não corresponde ao total da compra.");
        }

        for (VendaDTO.CartaoPagamentoDTO c : dto.getCartoes()) {
            if (c.getValor() < 10.0) {
                throw new RuntimeException("Valor mínimo por cartão é R$ 10,00.");
            }
        }

        Venda venda = new Venda();
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatus("EM PROCESSAMENTO");
        venda.setFrete(frete);
        venda.setCliente(cliente);
        venda.setEnderecoEntrega(enderecoEntrega);
        venda.setCupomPromocional(dto.getCupomPromocional());
        venda.setCupomTroca(dto.getCupomTroca());
        venda.setTotal(totalFinal);
        venda = vendaRepositorio.save(venda);

        for (VendaDTO.ItemDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepositorio.findById(itemDTO.getIdProduto()).get();

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            Estoque estoqueItem = estoqueRepositorio.findTopByProdutoOrderByDataEntradaDesc(produto)
                    .orElseThrow(() -> new RuntimeException("Estoque não encontrado para o produto: " + produto.getNome()));
            item.setPrecoUnitario(estoqueItem.getPrecoVenda().doubleValue());
            item.setVenda(venda);
            itemVendaRepositorio.save(item);

            List<Estoque> estoques = estoqueRepositorio.findByProdutoOrderByDataEntradaAsc(produto);
            int restante = itemDTO.getQuantidade();
            for (Estoque e : estoques) {
                if (e.getQuantidade() == 0) continue;
                int usado = Math.min(restante, e.getQuantidade());
                e.setQuantidade(e.getQuantidade() - usado);
                restante -= usado;
                estoqueRepositorio.save(e);
                if (restante == 0) break;
            }

            if (restante > 0) {
                throw new RuntimeException("Erro ao dar baixa no estoque do produto: " + produto.getNome());
            }
        }

        // Salva os cartões usados na venda
        for (VendaDTO.CartaoPagamentoDTO c : dto.getCartoes()) {
            Cartao cartao = cartaoRepositorio.findById(c.getIdCartao())
                    .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

            VendaCartao vendaCartao = new VendaCartao();
            vendaCartao.setVenda(venda);
            vendaCartao.setCartao(cartao);
            vendaCartao.setValor(c.getValor());

            vendaCartaoRepositorio.save(vendaCartao);
        }

        notificacaoServico.gerarNotificacaoCompraRealizada(cliente, venda.getId());

        return venda.getId();

    }

    public List<PedidoRespostaDTO> listarTodosPedidos() {
        List<Venda> vendas = vendaRepositorio.findAll();
        List<PedidoRespostaDTO> lista = new ArrayList<>();

        for (Venda venda : vendas) {
            PedidoRespostaDTO dto = new PedidoRespostaDTO();
            dto.setId(venda.getId());
            dto.setDataVenda(venda.getDataVenda());
            dto.setStatus("EM PROCESSAMENTO");

            // Cliente
            Cliente cliente = venda.getCliente();
            dto.setClienteNome(cliente != null ? cliente.getNome() : "Desconhecido");

            // Produtos
            List<PedidoRespostaDTO.ItemDTO> itensDTO = new ArrayList<>();
            List<ItemVenda> itensVenda = itemVendaRepositorio.findByVenda(venda);
            double total = 0;

            for (ItemVenda item : itensVenda) {
                PedidoRespostaDTO.ItemDTO i = new PedidoRespostaDTO.ItemDTO();
                i.setProdutoNome(item.getProduto().getNome());
                i.setQuantidade(item.getQuantidade());
                i.setPreco(item.getPrecoUnitario());
                i.setSubtotal(item.getPrecoUnitario() * item.getQuantidade());
                itensDTO.add(i);
                total += i.getSubtotal();
            }

            dto.setProdutos(itensDTO);

            // Endereço
            Endereco e = venda.getEnderecoEntrega();
            if (e != null) {
                dto.setEndereco(String.format("%s %s, %s - %s, %s",
                        e.getTipoLogradouro(), e.getLogradouro(), e.getNumero(),
                        e.getBairro(), e.getCidade()));
            } else {
                dto.setEndereco("Endereço não informado");
            }

            // Cartões
            List<PedidoRespostaDTO.CartaoDTO> listaCartoes = new ArrayList<>();
            List<VendaCartao> cartoes = vendaCartaoRepositorio.findByVenda(venda);
            for (VendaCartao v : cartoes) {
                PedidoRespostaDTO.CartaoDTO c = new PedidoRespostaDTO.CartaoDTO();
                String numero = v.getCartao().getCartaoNumero();
                c.setFinalNumero(numero != null && numero.length() >= 4
                        ? "**** " + numero.substring(numero.length() - 4)
                        : "****");
                c.setValor(v.getValor());
                listaCartoes.add(c);
            }

            dto.setCartoes(listaCartoes);
            dto.setStatus(venda.getStatus());
            dto.setCupomPromocional(venda.getCupomPromocional());
            dto.setCupomTroca(venda.getCupomTroca());
            dto.setFrete(venda.getFrete());
            dto.setTotal(total + venda.getFrete());
            lista.add(dto);
        }

        return lista;
    }

    public void atualizarStatus(Long idVenda, String novoStatus) {
        Venda venda = vendaRepositorio.findById(idVenda)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        venda.setStatus(novoStatus);
        notificacaoServico.gerarNotificacaoStatusAtualizado(venda.getCliente(), venda.getId(), novoStatus);
        vendaRepositorio.save(venda);
    }

    private List<PedidoRespostaDTO> mapearParaDTO(List<Venda> vendas) {
        List<PedidoRespostaDTO> lista = new ArrayList<>();

        for (Venda venda : vendas) {
            PedidoRespostaDTO dto = new PedidoRespostaDTO();
            dto.setId(venda.getId());
            dto.setDataVenda(venda.getDataVenda());
            dto.setStatus(venda.getStatus());

            Cliente cliente = venda.getCliente();
            dto.setClienteNome(cliente.getNome());

            // Produtos
            List<PedidoRespostaDTO.ItemDTO> itensDTO = new ArrayList<>();
            List<ItemVenda> itensVenda = itemVendaRepositorio.findByVenda(venda);
            double total = 0;

            for (ItemVenda item : itensVenda) {
                PedidoRespostaDTO.ItemDTO i = new PedidoRespostaDTO.ItemDTO();
                i.setProdutoNome(item.getProduto().getNome());
                i.setQuantidade(item.getQuantidade());
                i.setPreco(item.getPrecoUnitario());
                i.setSubtotal(item.getPrecoUnitario() * item.getQuantidade());
                itensDTO.add(i);
                total += i.getSubtotal();
            }

            dto.setProdutos(itensDTO);
            dto.setFrete(venda.getFrete());

            double descontoPromo = 0.0;
            if (venda.getCupomPromocional() != null && !venda.getCupomPromocional().isBlank()) {
                descontoPromo = cupomRepositorio.findByCodigoAndAtivoTrue(venda.getCupomPromocional())
                        .map(c -> c.getValor())
                        .orElse(0.0);
            }

            double descontoTroca = 0.0;
            if (venda.getCupomTroca() != null && !venda.getCupomTroca().isBlank()) {
                descontoTroca = cupomTrocaRepositorio.findByCodigoIgnoreCase(venda.getCupomTroca())
                        .map(c -> c.getValor().doubleValue())
                        .orElse(0.0);
            }

            dto.setTotal(total + venda.getFrete() - descontoPromo - descontoTroca);

            // Endereço
            Endereco e = venda.getEnderecoEntrega();
            dto.setEndereco(String.format("%s %s, %s - %s, %s",
                    e.getTipoLogradouro(), e.getLogradouro(), e.getNumero(),
                    e.getBairro(), e.getCidade()));

            // Cartões
            List<VendaCartao> cartoes = vendaCartaoRepositorio.findByVenda(venda);
            List<PedidoRespostaDTO.CartaoDTO> listaCartoes = new ArrayList<>();
            for (VendaCartao v : cartoes) {
                PedidoRespostaDTO.CartaoDTO c = new PedidoRespostaDTO.CartaoDTO();
                c.setFinalNumero("**** " + v.getCartao().getCartaoNumero().substring(v.getCartao().getCartaoNumero().length() - 4));
                c.setValor(v.getValor());
                listaCartoes.add(c);
            }
            dto.setCartoes(listaCartoes);

            dto.setCupomPromocional(venda.getCupomPromocional());
            dto.setCupomTroca(venda.getCupomTroca());

            lista.add(dto);
        }

        return lista;
    }

    public List<PedidoRespostaDTO> listarPedidosDoCliente(Long idCliente) {
        List<Venda> vendas = vendaRepositorio.findByClienteId(idCliente);
        return mapearParaDTO(vendas); // reutilize a lógica do listarTodosPedidos()
    }

    //RNF0043 RF0055
    public List<VendaResumoDTO> resumirVendasPorPeriodo(LocalDate inicio, LocalDate fim, String tipo) {
        List<Venda> vendas = vendaRepositorio.findByDataVendaBetween(inicio.atStartOfDay(), fim.atTime(23, 59));
        List<VendaResumoDTO> resultado = new ArrayList<>();

        for (Venda v : vendas) {
            for (ItemVenda item : v.getItens()) {

                if ("categoria".equalsIgnoreCase(tipo)) {
                    for (Categoria c : item.getProduto().getCategorias()) {
                        VendaResumoDTO dto = new VendaResumoDTO();
                        dto.setNomeProdutoOuCategoria(c.getNome());
                        dto.setDataVenda(v.getDataVenda().toLocalDate());
                        dto.setQuantidadeVendida(item.getQuantidade());
                        resultado.add(dto);
                    }
                } else {
                    VendaResumoDTO dto = new VendaResumoDTO();
                    dto.setNomeProdutoOuCategoria(item.getProduto().getNome());
                    dto.setDataVenda(v.getDataVenda().toLocalDate());
                    dto.setQuantidadeVendida(item.getQuantidade());
                    resultado.add(dto);
                }

            }
        }

        return resultado;
    }
}