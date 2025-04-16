package br.com.fatec.vortismobile.cliente.dto;

public class ClienteDTO {
    private Long id;
    private String nome;

    public ClienteDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}