package br.com.fatec.vortismobile.cliente.controlador;

import br.com.fatec.vortismobile.cliente.modelo.Cartao;
import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import br.com.fatec.vortismobile.cliente.modelo.Endereco;
import br.com.fatec.vortismobile.cliente.servico.ClienteServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteControlador {

    @Autowired
    private ClienteServico clienteServico;

    //listar todos os clientes
    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteServico.listarTodos();
    }

    //listar um cliente
    @GetMapping("/{id}")
    public Optional<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteServico.buscarPorId(id);
    }

    //cadastrar cliente
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Cliente cliente) {
        Cliente clienteSalvo = clienteServico.salvar(cliente);
        return ResponseEntity.ok(clienteSalvo);
    }

    //deletar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            clienteServico.deletar(id);
            return ResponseEntity.ok().body("{\"mensagem\": \"Cliente excluído com sucesso!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }

    //editar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id,
                                                    @RequestBody Cliente clienteAtualizado) {
        Optional<Cliente> clienteExistente = clienteServico.buscarPorId(id);

        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();

            cliente.setNome(clienteAtualizado.getNome());
            cliente.setDataNascimento(clienteAtualizado.getDataNascimento());
            cliente.setGenero(clienteAtualizado.getGenero());
            cliente.setEmail(clienteAtualizado.getEmail());
            cliente.setTelefone(clienteAtualizado.getTelefone());

            Cliente clienteSalvo = clienteServico.salvar(cliente);
            return ResponseEntity.ok(clienteSalvo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //editar endereco
    @PutMapping("/{id}/enderecos")
    public ResponseEntity<?> atualizarEnderecos(@PathVariable Long id, @RequestBody List<Endereco> novosEnderecos) {
        try {
            Cliente cliente = clienteServico.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

            List<Endereco> enderecosAtualizados = new ArrayList<>();

            for (Endereco novoEndereco : novosEnderecos) {
                novoEndereco.setCliente(cliente); //garantir cliente
                enderecosAtualizados.add(novoEndereco);
            }

            cliente.getEnderecos().clear();// remove os antigos
            cliente.getEnderecos().addAll(enderecosAtualizados);// add os novos

            clienteServico.salvar(cliente);
            return ResponseEntity.ok("Endereços atualizados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar endereços: " + e.getMessage());
        }
    }

    //listar endereços
    @GetMapping("/{id}/enderecos")
    public ResponseEntity<?> buscarEnderecosDoCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteServico.buscarPorId(id);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return ResponseEntity.ok(cliente.getEnderecos());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
    }

    //listar cartao
    @GetMapping("/{id}/cartoes")
    public ResponseEntity<List<Cartao>> listarCartoes(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteServico.buscarPorId(id);

        return cliente.map(value
                ->ResponseEntity.ok(value.getCartoes())).orElseGet(()
                -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    //editar cartao
    @PutMapping("/{id}/cartoes")
    public ResponseEntity<String> atualizarCartoes(@PathVariable Long id, @RequestBody List<Cartao> novosCartoes) {
        Optional<Cliente> clienteOpt = clienteServico.buscarPorId(id);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            cliente.getCartoes().clear();
            for (Cartao cartao : novosCartoes) {
                cartao.setCliente(cliente);
                cliente.getCartoes().add(cartao);
            }

            clienteServico.salvar(cliente);
            return ResponseEntity.ok("Cartões atualizados com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        }
    }

    //criptografia senha
    @Autowired
    private PasswordEncoder passwordEncoder;

    //alterar senha
    @PutMapping("/{id}/alterar-senha")
    public ResponseEntity<?> alterarSenha(@PathVariable Long id, @RequestBody Map<String, String> senhaRequest) {
        Optional<Cliente> clienteOpt = clienteServico.buscarPorId(id);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            String senhaAtual = senhaRequest.get("senhaAtual");
            String novaSenha = senhaRequest.get("novaSenha");

            if (!passwordEncoder.matches(senhaAtual, cliente.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha atual incorreta.");
            }

            cliente.setSenha(passwordEncoder.encode(novaSenha));
            clienteServico.salvar(cliente);

            return ResponseEntity.ok("Senha alterada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        }
    }

    //alterar status cliente
    @PutMapping("/{id}/status")
    public ResponseEntity<String> alterarStatusCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteServico.buscarPorId(id);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setStatus(cliente.getStatus().equals("1") ? "0" : "1");
            clienteServico.salvar(cliente);
            return ResponseEntity.ok("Status do cliente atualizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        }
    }
}