package com.unifor.algorithm;

import java.util.List;

import com.unifor.model.Cliente;
import com.unifor.model.Ponto;
import com.unifor.model.Veiculo;
import com.unifor.util.Distancia;

/**
 * Classe responsável pela roteirização de entregas utilizando heurísticas gulosas.
 */
public class Roteirizador {

    /**
     * Construtor padrão.
     */
    public Roteirizador() {
    }

    /**
     * Encontra o vizinho mais próximo viável considerando restrições de capacidade e autonomia.
     * 
     * Heurística Gulosa: Seleciona o cliente não visitado com menor distância Euclidiana
     * em relação à localização atual, respeitando as seguintes restrições:
     * 
     * 1. Capacidade: A demanda do cliente deve couber na capacidade restante do veículo
     * 2. Autonomia: O veículo deve ter autonomia suficiente para ir até o cliente E retornar à central
     * 
     * @param localAtual Localização atual (ponto de partida)
     * @param naoVisitados Lista de clientes ainda não visitados
     * @param veiculo Veículo que realizará a entrega
     * @param central Ponto da central de distribuição (para cálculo de retorno)
     * @return O cliente mais próximo que satisfaz todas as restrições, ou null se nenhum for viável
     */
    private Cliente encontrarVizinhoMaisProximo(Ponto localAtual, List<Cliente> naoVisitados, 
                                                 Veiculo veiculo, Ponto central) {
        if (localAtual == null || naoVisitados == null || veiculo == null || central == null) {
            return null;
        }

        if (naoVisitados.isEmpty()) {
            return null;
        }

        Cliente melhorCliente = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Cliente cliente : naoVisitados) {
            if (cliente == null || cliente.getLocalizacao() == null) {
                continue;
            }

            // Calcular distância da localização atual até o cliente
            double distanciaAteCliente = Distancia.calcularDistanciaEuclidiana(
                localAtual, 
                cliente.getLocalizacao()
            );

            // Calcular distância do cliente até a central (para retorno)
            double distanciaClienteParaCentral = Distancia.calcularDistanciaEuclidiana(
                cliente.getLocalizacao(), 
                central
            );

            // VALIDAÇÃO 1: Verificar se a demanda do cliente cabe no veículo
            boolean capacidadeSuficiente = (veiculo.getCargaAtual() + cliente.getDemandaCarga()) 
                                            <= veiculo.getCapacidadeMaxima();

            // VALIDAÇÃO 2: Verificar se há autonomia para ir até o cliente E voltar para a central
            double autonomiaNecessaria = distanciaAteCliente + distanciaClienteParaCentral;
            boolean autonomiaSuficiente = autonomiaNecessaria <= veiculo.getAutonomiaRestante();

            // Se o cliente atende ambas as restrições e é o mais próximo até agora
            if (capacidadeSuficiente && autonomiaSuficiente && distanciaAteCliente < menorDistancia) {
                menorDistancia = distanciaAteCliente;
                melhorCliente = cliente;
            }
        }

        return melhorCliente;
    }
}
