package com.retailhub.model;

public class ProdutoFisico extends Produto {
    private double peso;
    private double frete;

    public ProdutoFisico() {
        super();
    }

    public ProdutoFisico(int id, String nome, String sku, String descricao, double preco, double precoCusto, Categoria categoria, double peso, double frete, int quantidadeEstoque, String imagemPath) {
        super(id, nome, sku, descricao, preco, precoCusto, categoria, quantidadeEstoque, imagemPath);
        this.peso = peso;
        this.frete = frete;
    }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getFrete() { return frete; }
    public void setFrete(double frete) { this.frete = frete; }

    @Override
    public double calcularTaxa() {
        // Exemplo: 10% do valor + frete
        return (getPreco() * 0.10) + frete;
    }
}
