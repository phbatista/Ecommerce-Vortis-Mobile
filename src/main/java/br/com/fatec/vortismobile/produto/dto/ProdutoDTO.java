package br.com.fatec.vortismobile.produto.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProdutoDTO {

    private String nome;
    private Integer disponibilidadeAno;
    private String chipset;
    private String sistemaOperacional;
    private String bateria;
    private String resolucaoCamera;
    private String gpu;
    private String tipoTela;
    private Double tamanhoTela;
    private Double peso;
    private Double altura;
    private Double largura;
    private Double profundidade;
    private String memoriaRam;
    private String armazenamento;
    private String codigoBarras;
    private String grupoPrecificacao;
    private MultipartFile imagem;
    private String status;
    private String motivo;
    private String justificativa;
    private List<String> nomesCategorias;

    public MultipartFile getImagem() { return imagem; }
    public void setImagem(MultipartFile imagem) { this.imagem = imagem; }
}