package com.unifor.model;

/**
 * Representa a central de distribuição (depósito) localizada na origem (0, 0).
 */
public class Central {
    private final Ponto localizacao;
    private String nome;

    /**
     * Construtor padrão - inicializa a central na origem (0, 0).
     */
    public Central() {
        this.localizacao = new Ponto(0.0, 0.0);
        this.nome = "Central de Distribuição";
    }

    /**
     * Construtor com nome personalizado.
     * 
     * @param nome Nome da central
     */
    public Central(String nome) {
        this.localizacao = new Ponto(0.0, 0.0);
        this.nome = nome;
    }

    public Ponto getLocalizacao() {
        return localizacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Central{" +
                "nome='" + nome + '\'' +
                ", localizacao=" + localizacao +
                '}';
    }
}
