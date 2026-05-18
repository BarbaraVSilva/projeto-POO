package com.retailhub.model;

import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int id;
    private Cliente cliente;
    private Cupom cupom;
    private Usuario vendedor;
    private String dataVenda;
    private String statusVenda;
    private String statusLogistica; // A Enviar, Enviado, Entregue
    private String metodoPagamento;
    private List<ItemVenda> itens;

    public Venda() {
        this.itens = new ArrayList<>();
        this.statusVenda = "CONCLUIDA";
        this.statusLogistica = "A Enviar";
        this.metodoPagamento = "DINHEIRO";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Cupom getCupom() { return cupom; }
    public void setCupom(Cupom cupom) { this.cupom = cupom; }
    
    public Usuario getVendedor() { return vendedor; }
    public void setVendedor(Usuario vendedor) { this.vendedor = vendedor; }

    public String getStatusVenda() { return statusVenda; }
    public void setStatusVenda(String statusVenda) { this.statusVenda = statusVenda; }

    public String getStatusLogistica() { return statusLogistica; }
    public void setStatusLogistica(String statusLogistica) { this.statusLogistica = statusLogistica; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }
    
    public void adicionarItem(ItemVenda item) {
        this.itens.add(item);
    }

    public String getDataVenda() { return dataVenda; }
    public void setDataVenda(String dataVenda) { this.dataVenda = dataVenda; }
    
    public double getTotalBruto() {
        double subtotal = 0;
        for (ItemVenda item : itens) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double getTotalComDesconto() {
        double subtotal = getTotalBruto();
        
        if (cupom != null) {
            return subtotal - (subtotal * (cupom.getDescontoPercentual() / 100.0));
        }
        return subtotal;
    }
}
