package com.retailhub.model;

public abstract class Produto {
    protected int id;
    protected String nome;
    protected String sku;
    protected String descricao;
    protected double preco;
    protected double precoCusto;
    protected Categoria categoria;
    protected int quantidadeEstoque;
    protected String imagemPath;

    public Produto() {}

    public Produto(int id, String nome, String sku, String descricao, double preco, double precoCusto, Categoria categoria, int quantidadeEstoque, String imagemPath) {
        this.id = id;
        this.nome = nome;
        this.sku = sku;
        this.descricao = descricao;
        this.preco = preco;
        this.precoCusto = precoCusto;
        this.categoria = categoria;
        this.quantidadeEstoque = quantidadeEstoque;
        this.imagemPath = imagemPath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double precoCusto) { this.precoCusto = precoCusto; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }
    
    // Método abstrato que será sobrescrito pelas filhas (Polimorfismo)
    public abstract double calcularTaxa();
}
