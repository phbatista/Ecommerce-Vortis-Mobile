package br.com.fatec.vortismobile.cupom.servico;

import br.com.fatec.vortismobile.cupom.dto.CupomDTO;
import br.com.fatec.vortismobile.cupom.modelo.Cupom;
import br.com.fatec.vortismobile.cupom.repositorio.CupomRepositorio;
import br.com.fatec.vortismobile.venda.modelo.CupomTroca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CupomServico {

    @Autowired
    private CupomRepositorio cupomRepositorio;

    @Autowired
    private br.com.fatec.vortismobile.venda.repositorio.CupomTrocaRepositorio cupomTrocaRepositorio;


    public List<Cupom> listarTodos() {
        return cupomRepositorio.findAll();
    }

    public Optional<Cupom> buscarPorId(Long id) {
        return cupomRepositorio.findById(id);
    }

    public Cupom salvar(Cupom cupom) {
        return cupomRepositorio.save(cupom);
    }

    public void deletar(Long id) {
        cupomRepositorio.deleteById(id);
    }

    public Optional<CupomDTO> validarCupom(String codigo, Long idCliente) {
        Optional<Cupom> cupomOpt = cupomRepositorio.findByCodigoAndAtivoTrue(codigo);

        if (cupomOpt.isPresent()) {
            Cupom cupom = cupomOpt.get();

            if (cupom.getTipo().equalsIgnoreCase("TROCA")) {
                if (cupom.getCliente() == null || !cupom.getCliente().getId().equals(idCliente)) {
                    return Optional.empty();
                }
            }

            return Optional.of(new CupomDTO(
                    cupom.getCodigo(),
                    cupom.getValor(),
                    "PROMOCIONAL"
            ));
        }

        Optional<CupomTroca> trocaOpt = cupomTrocaRepositorio.findByCodigoIgnoreCase(codigo);

        if (trocaOpt.isPresent()) {
            CupomTroca troca = trocaOpt.get();

            if (!troca.isUsado()
                    && troca.getCliente().getId().equals(idCliente)
                    && troca.getDataValidade().toLocalDate().isAfter(LocalDate.now())) {

                return Optional.of(new CupomDTO(
                        troca.getCodigo(),
                        troca.getValor().doubleValue(),
                        "TROCA"
                ));
            }
        }

        System.out.println("Cupom de troca não válido para o cliente: " + codigo + ", ID cliente: " + idCliente);
        return Optional.empty();

    }
}