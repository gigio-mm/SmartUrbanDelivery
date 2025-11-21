package com.unifor.model;

/**
 * Representa um ponto geográfico no plano cartesiano.
 */
public class Ponto {
    private double x;
    private double y;

    /**
     * Construtor padrão.
     */
    public Ponto() {
    }

    /**
     * Construtor com parâmetros.
     * 
     * @param x Coordenada X
     * @param y Coordenada Y
     */
    public Ponto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Ponto{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ponto ponto = (Ponto) o;
        return Double.compare(ponto.x, x) == 0 && Double.compare(ponto.y, y) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(x);
        int result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
