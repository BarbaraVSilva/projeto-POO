package com.retailhub.model;

public class SimuladorItem {
    private String nomeProduto;
    private double valorProduto;
    private double comissaoShopeePercentual;
    
    public SimuladorItem(String nomeProduto, double valorProduto, double comissaoShopeePercentual) {
        this.nomeProduto = nomeProduto;
        this.valorProduto = valorProduto;
        this.comissaoShopeePercentual = comissaoShopeePercentual;
    }
    
    public String getNomeProduto() { return nomeProduto; }
    public double getValorProduto() { return valorProduto; }
    public double getComissaoShopeePercentual() { return comissaoShopeePercentual; }
    
    public double getValorComissao() {
        return valorProduto * (comissaoShopeePercentual / 100.0);
    }
    
    public double getLucroLiquido() {
        return valorProduto - getValorComissao();
    }
}
