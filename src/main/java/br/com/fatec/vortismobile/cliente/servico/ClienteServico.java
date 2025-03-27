package br.com.fatec.vortismobile.cliente.servico;

import br.com.fatec.vortismobile.cliente.modelo.Cartao;
import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import br.com.fatec.vortismobile.cliente.modelo.Endereco;
import br.com.fatec.vortismobile.cliente.repositorio.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServico {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    private final PasswordEncoder passwordEncoder;

    public ClienteServico(ClienteRepositorio clienteRepositorio, PasswordEncoder passwordEncoder) {
        this.clienteRepositorio = clienteRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    //listar clientes
    public List<Cliente> listarTodos() {
        return clienteRepositorio.findAll();
    }

    //listar cliente edições
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepositorio.findById(id);
    }

    //salvar clientes
    public Cliente salvar(Cliente cliente) {

        //criptografar senha
        if (!cliente.getSenha().startsWith("$2a$")) {
            cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        }

        //endereço nao pode ser nulo
        if (cliente.getEnderecos() != null) {
            for (Endereco endereco : cliente.getEnderecos()) {
                endereco.setCliente(cliente);
            }
        }

        //cartão nao pode ser nulo
        if (cliente.getCartoes() != null) {
            for (Cartao cartao : cliente.getCartoes()) {
                cartao.setCliente(cliente);
            }
        }

        return clienteRepositorio.save(cliente);
    }

    //deleter clientes
    public void deletar(Long id) {
        Optional<Cliente> cliente = clienteRepositorio.findById(id);
        if (cliente.isPresent()) {
            clienteRepositorio.deleteById(id);
        } else {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }
    }
}
