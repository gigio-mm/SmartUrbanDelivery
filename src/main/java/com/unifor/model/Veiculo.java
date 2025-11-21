package com.unifor.model;

/**
 * Representa um veículo de entrega com capacidade, autonomia e localização.
 */
public class Veiculo {
    private double capacidadeMaxima;
    private double autonomiaMaxima;
    private Ponto localizacaoAtual;
    private double cargaAtual;
    private double autonomiaRestante;

    /**
     * Construtor padrão.
     */
    public Veiculo() {
    }

    /**
     * Construtor com parâmetros.
     * 
     * @param capacidadeMaxima Capacidade máxima de carga do veículo
     * @param autonomiaMaxima Autonomia máxima do veículo
     * @param localizacaoAtual Localização atual do veículo
     * @param cargaAtual Carga atual do veículo
     * @param autonomiaRestante Autonomia restante do veículo
     */
    public Veiculo(double capacidadeMaxima, double autonomiaMaxima, Ponto localizacaoAtual, 
                   double cargaAtual, double autonomiaRestante) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.autonomiaMaxima = autonomiaMaxima;
        this.localizacaoAtual = localizacaoAtual;
        this.cargaAtual = cargaAtual;
        this.autonomiaRestante = autonomiaRestante;
    }

    public double getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(double capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public double getAutonomiaMaxima() {
        return autonomiaMaxima;
    }

    public void setAutonomiaMaxima(double autonomiaMaxima) {
        this.autonomiaMaxima = autonomiaMaxima;
    }

    public Ponto getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public void setLocalizacaoAtual(Ponto localizacaoAtual) {
        this.localizacaoAtual = localizacaoAtual;
    }

    public double getCargaAtual() {
        return cargaAtual;
    }

    public void setCargaAtual(double cargaAtual) {
        this.cargaAtual = cargaAtual;
    }

    public double getAutonomiaRestante() {
        return autonomiaRestante;
    }

    public void setAutonomiaRestante(double autonomiaRestante) {
        this.autonomiaRestante = autonomiaRestante;
    }

    /**
     * Verifica se o veículo pode carregar uma determinada quantidade de carga.
     * 
     * @param carga Quantidade de carga a ser verificada
     * @return true se a carga pode ser adicionada, false caso contrário
     */
    public boolean podeCarregar(double carga) {
        return (cargaAtual + carga) <= capacidadeMaxima;
    }

    /**
     * Adiciona carga ao veículo.
     * 
     * @param carga Quantidade de carga a ser adicionada
     */
    public void adicionarCarga(double carga) {
        if (podeCarregar(carga)) {
            this.cargaAtual += carga;
        }
    }

    /**
     * Verifica se o veículo tem autonomia suficiente para percorrer uma distância.
     * 
     * @param distancia Distância a ser percorrida
     * @return true se tem autonomia suficiente, false caso contrário
     */
    public boolean temAutonomia(double distancia) {
        return autonomiaRestante >= distancia;
    }

    /**
     * Consome autonomia ao percorrer uma distância.
     * 
     * @param distancia Distância percorrida
     */
    public void consumirAutonomia(double distancia) {
        if (temAutonomia(distancia)) {
            this.autonomiaRestante -= distancia;
        }
    }

    @Override
    public String toString() {
        return "Veiculo{" +
                "capacidadeMaxima=" + capacidadeMaxima +
                ", autonomiaMaxima=" + autonomiaMaxima +
                ", localizacaoAtual=" + localizacaoAtual +
                ", cargaAtual=" + cargaAtual +
                ", autonomiaRestante=" + autonomiaRestante +
                '}';
    }
}
