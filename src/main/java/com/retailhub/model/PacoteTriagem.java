package com.retailhub.model;

public class PacoteTriagem {
    private String codigoPedido;
    private double peso;
    private String tipoEmbalagem; // Saco Plástico / Caixa de Papelão
    private boolean precisaPlasticoBolha;
    private String operador;
    private String destinatario;
    private String endereco;

    public PacoteTriagem() {}

    public PacoteTriagem(String codigoPedido, double peso, String tipoEmbalagem, boolean precisaPlasticoBolha, String operador, String destinatario, String endereco) {
        this.codigoPedido = codigoPedido;
        this.peso = peso;
        this.tipoEmbalagem = tipoEmbalagem;
        this.precisaPlasticoBolha = precisaPlasticoBolha;
        this.operador = operador;
        this.destinatario = destinatario;
        this.endereco = endereco;
    }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCodigoPedido() { return codigoPedido; }
    public void setCodigoPedido(String codigoPedido) { this.codigoPedido = codigoPedido; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getTipoEmbalagem() { return tipoEmbalagem; }
    public void setTipoEmbalagem(String tipoEmbalagem) { this.tipoEmbalagem = tipoEmbalagem; }

    public boolean isPrecisaPlasticoBolha() { return precisaPlasticoBolha; }
    public void setPrecisaPlasticoBolha(boolean precisaPlasticoBolha) { this.precisaPlasticoBolha = precisaPlasticoBolha; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }
}
