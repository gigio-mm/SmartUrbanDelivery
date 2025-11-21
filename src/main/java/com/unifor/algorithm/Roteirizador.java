package com.unifor.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.unifor.model.Cliente;
import com.unifor.model.Ponto;
import com.unifor.model.Rota;
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
     * Calcula a rota de entrega otimizada utilizando algoritmo guloso.
     * 
     * O algoritmo segue estas etapas:
     * 1. Ordena os clientes por prioridade (decrescente) usando QuickSort
     * 2. Seleciona iterativamente o vizinho mais próximo que satisfaz as restrições
     * 3. Atualiza o veículo (carga e autonomia) a cada parada
     * 4. Adiciona o retorno à central ao final da rota
     * 
     * @param clientes Lista de clientes a serem atendidos
     * @param veiculo Veículo que realizará as entregas
     * @param central Ponto da central de distribuição
     * @return Rota calculada com os clientes visitados, distância total e carga coletada
     */
    public Rota calcularRota(List<Cliente> clientes, Veiculo veiculo, Ponto central) {
        // Validações de entrada
        if (clientes == null || veiculo == null || central == null) {
            return new Rota();
        }

        // PREPARAÇÃO: Criar nova rota e configurar estado inicial
        Rota rota = new Rota();
        Ponto localAtual = central;
        
        // Criar cópia da lista de clientes para não modificar a original
        List<Cliente> naoVisitados = new ArrayList<>(clientes);

        // ORDENAÇÃO INICIAL: Ordenar por prioridade decrescente (REQUISITO)
        Ordenacao.quickSort(naoVisitados);

        // LOOP GULOSO: Processar clientes até que não haja mais candidatos viáveis
        while (!naoVisitados.isEmpty()) {
            // Encontrar o próximo cliente mais próximo que satisfaz as restrições
            Cliente proximoCliente = encontrarVizinhoMaisProximo(localAtual, naoVisitados, veiculo, central);

            // Se nenhum cliente for viável, interromper o loop
            if (proximoCliente == null) {
                break;
            }

            // Calcular distância até o próximo cliente
            double distanciaPercorrida = Distancia.calcularDistanciaEuclidiana(
                localAtual, 
                proximoCliente.getLocalizacao()
            );

            // Adicionar cliente à rota
            rota.adicionarCliente(proximoCliente);

            // Atualizar veículo: aumentar carga e diminuir autonomia
            veiculo.adicionarCarga(proximoCliente.getDemandaCarga());
            veiculo.consumirAutonomia(distanciaPercorrida);

            // Atualizar distância total da rota
            rota.setDistanciaTotal(rota.getDistanciaTotal() + distanciaPercorrida);

            // Atualizar localização atual
            localAtual = proximoCliente.getLocalizacao();

            // Remover cliente da lista de não visitados
            naoVisitados.remove(proximoCliente);
        }

        // RETORNO À BASE: Calcular trajeto de volta para a central
        double distanciaRetorno = Distancia.calcularDistanciaEuclidiana(localAtual, central);
        
        // Atualizar autonomia do veículo com o retorno
        veiculo.consumirAutonomia(distanciaRetorno);
        
        // Atualizar distância total da rota com o retorno
        rota.setDistanciaTotal(rota.getDistanciaTotal() + distanciaRetorno);

        // Retornar rota completa
        return rota;
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
    private Cliente encontrarVizinhoMaisProximo(Ponto localAtual, List<Cliente> naoVisitados, Veiculo veiculo, Ponto central) {
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
