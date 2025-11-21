package com.unifor.model;

import java.util.Objects;

/**
 * Representa um cliente com localização, demanda de carga e prioridade.
 */
public class Cliente {
    private Ponto localizacao;
    private double demandaCarga;
    private int prioridade;

    /**
     * Construtor padrão.
     */
    public Cliente() {
    }

    /**
     * Construtor com parâmetros.
     * 
     * @param localizacao Localização do cliente
     * @param demandaCarga Demanda de carga do cliente
     * @param prioridade Prioridade do cliente (maior valor = maior prioridade)
     */
    public Cliente(Ponto localizacao, double demandaCarga, int prioridade) {
        this.localizacao = localizacao;
        this.demandaCarga = demandaCarga;
        this.prioridade = prioridade;
    }

    public Ponto getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Ponto localizacao) {
        this.localizacao = localizacao;
    }

    public double getDemandaCarga() {
        return demandaCarga;
    }

    public void setDemandaCarga(double demandaCarga) {
        this.demandaCarga = demandaCarga;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "localizacao=" + localizacao +
                ", demandaCarga=" + demandaCarga +
                ", prioridade=" + prioridade +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Double.compare(cliente.demandaCarga, demandaCarga) == 0 &&
                prioridade == cliente.prioridade &&
                Objects.equals(localizacao, cliente.localizacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localizacao, demandaCarga, prioridade);
    }
}
