package com.retailhub.model;

public class Categoria {
    private int id;
    private String nome;
    private String descricao;
    private int statusAtivo;
    private double taxaComissao;
    private int prazoEnvio;
    private String dataCadastro;

    public Categoria() {}

    public Categoria(int id, String nome, String descricao, int statusAtivo, double taxaComissao, int prazoEnvio, String dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.statusAtivo = statusAtivo;
        this.taxaComissao = taxaComissao;
        this.prazoEnvio = prazoEnvio;
        this.dataCadastro = dataCadastro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(int statusAtivo) { this.statusAtivo = statusAtivo; }

    public double getTaxaComissao() { return taxaComissao; }
    public void setTaxaComissao(double taxaComissao) { this.taxaComissao = taxaComissao; }

    public int getPrazoEnvio() { return prazoEnvio; }
    public void setPrazoEnvio(int prazoEnvio) { this.prazoEnvio = prazoEnvio; }

    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() {
        return this.nome; // Para aparecer corretamente no ComboBox
    }
}
