package br.com.fatec.vortismobile.cupom.servico;

import br.com.fatec.vortismobile.cupom.modelo.Cupom;
import br.com.fatec.vortismobile.cupom.repositorio.CupomRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CupomServico {

    @Autowired
    private CupomRepositorio cupomRepositorio;

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

    public Optional<Cupom> validarCupom(String codigo, Long idCliente) {
        Optional<Cupom> cupomOpt = cupomRepositorio.findByCodigoAndAtivoTrue(codigo);

        if (cupomOpt.isEmpty()) return Optional.empty();

        Cupom cupom = cupomOpt.get();

        if (cupom.getTipo().equalsIgnoreCase("TROCA")) {
            if (cupom.getCliente() == null || !cupom.getCliente().getId().equals(idCliente)) {
                return Optional.empty();
            }
        }

        return Optional.of(cupom);
    }
}
