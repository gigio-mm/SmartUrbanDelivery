package com.unifor.util;

import com.unifor.model.Ponto;

/**
 * Classe utilitária para cálculos de distância.
 */
public class Distancia {

    /**
     * Construtor privado para evitar instanciação.
     */
    private Distancia() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    /**
     * Calcula a distância Euclidiana entre dois pontos.
     * 
     * A fórmula utilizada é: d = √[(x2 - x1)² + (y2 - y1)²]
     * 
     * @param p1 Primeiro ponto
     * @param p2 Segundo ponto
     * @return Distância Euclidiana entre os pontos
     * @throws IllegalArgumentException se algum dos pontos for nulo
     */
    public static double calcularDistanciaEuclidiana(Ponto p1, Ponto p2) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Os pontos não podem ser nulos");
        }

        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Calcula a distância Euclidiana entre coordenadas.
     * 
     * @param x1 Coordenada X do primeiro ponto
     * @param y1 Coordenada Y do primeiro ponto
     * @param x2 Coordenada X do segundo ponto
     * @param y2 Coordenada Y do segundo ponto
     * @return Distância Euclidiana entre os pontos
     */
    public static double calcularDistanciaEuclidiana(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
