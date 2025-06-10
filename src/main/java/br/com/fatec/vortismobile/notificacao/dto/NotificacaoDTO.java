package br.com.fatec.vortismobile.notificacao.dto;

import java.time.LocalDateTime;

public class NotificacaoDTO {
    private String tipo;
    private String mensagem;
    private LocalDateTime data;

    public NotificacaoDTO(String tipo, String mensagem, LocalDateTime data) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.data = data;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
