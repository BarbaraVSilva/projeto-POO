package com.retailhub.model;

public class Cupom {
    private int id;
    private String codigo;
    private double descontoPercentual;
    private int statusAtivo;

    public Cupom() {}

    public Cupom(int id, String codigo, double descontoPercentual, int statusAtivo) {
        this.id = id;
        this.codigo = codigo;
        this.descontoPercentual = descontoPercentual;
        this.statusAtivo = statusAtivo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public double getDescontoPercentual() { return descontoPercentual; }
    public void setDescontoPercentual(double descontoPercentual) { this.descontoPercentual = descontoPercentual; }

    public int getStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(int statusAtivo) { this.statusAtivo = statusAtivo; }

    @Override
    public String toString() {
        return codigo + " (" + descontoPercentual + "%)";
    }
}
