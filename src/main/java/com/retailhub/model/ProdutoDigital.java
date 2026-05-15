package com.retailhub.model;

public class ProdutoDigital extends Produto {

    public ProdutoDigital() {
        super();
    }

    public ProdutoDigital(int id, String nome, String sku, String descricao, double preco, double precoCusto, Categoria categoria, int quantidadeEstoque, String imagemPath) {
        super(id, nome, sku, descricao, preco, precoCusto, categoria, quantidadeEstoque, imagemPath);
    }

    @Override
    public double calcularTaxa() {
        // Exemplo: Produto digital não tem frete, apenas taxa fixa de 15%
        return getPreco() * 0.15;
    }
}
