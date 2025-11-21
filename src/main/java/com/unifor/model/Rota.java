package com.unifor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma rota de entrega com pontos de parada, distância total e carga coletada.
 */
public class Rota {
    private List<Cliente> pontos;
    private double distanciaTotal;
    private double cargaTotalColetada;

    /**
     * Construtor padrão.
     */
    public Rota() {
        this.pontos = new ArrayList<>();
        this.distanciaTotal = 0.0;
        this.cargaTotalColetada = 0.0;
    }

    /**
     * Construtor com parâmetros.
     * 
     * @param pontos Lista de clientes na rota
     * @param distanciaTotal Distância total da rota
     * @param cargaTotalColetada Carga total coletada na rota
     */
    public Rota(List<Cliente> pontos, double distanciaTotal, double cargaTotalColetada) {
        this.pontos = pontos != null ? pontos : new ArrayList<>();
        this.distanciaTotal = distanciaTotal;
        this.cargaTotalColetada = cargaTotalColetada;
    }

    public List<Cliente> getPontos() {
        return pontos;
    }

    public void setPontos(List<Cliente> pontos) {
        this.pontos = pontos;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(double distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }

    public double getCargaTotalColetada() {
        return cargaTotalColetada;
    }

    public void setCargaTotalColetada(double cargaTotalColetada) {
        this.cargaTotalColetada = cargaTotalColetada;
    }

    /**
     * Adiciona um cliente à rota.
     * 
     * @param cliente Cliente a ser adicionado
     */
    public void adicionarCliente(Cliente cliente) {
        if (cliente != null) {
            this.pontos.add(cliente);
            this.cargaTotalColetada += cliente.getDemandaCarga();
        }
    }

    /**
     * Remove um cliente da rota.
     * 
     * @param cliente Cliente a ser removido
     * @return true se o cliente foi removido, false caso contrário
     */
    public boolean removerCliente(Cliente cliente) {
        if (cliente != null && this.pontos.remove(cliente)) {
            this.cargaTotalColetada -= cliente.getDemandaCarga();
            return true;
        }
        return false;
    }

    /**
     * Retorna o número de clientes na rota.
     * 
     * @return Número de clientes
     */
    public int getNumeroClientes() {
        return pontos.size();
    }

    @Override
    public String toString() {
        return "Rota{" +
                "numeroClientes=" + pontos.size() +
                ", distanciaTotal=" + distanciaTotal +
                ", cargaTotalColetada=" + cargaTotalColetada +
                '}';
    }
}
