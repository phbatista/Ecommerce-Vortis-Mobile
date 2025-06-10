package br.com.fatec.vortismobile.cupom.dto;

public class CupomDTO {
    private String codigo;
    private double valor;
    private String tipo;

    public CupomDTO(String codigo, double valor, String tipo) {
        this.codigo = codigo;
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }
}