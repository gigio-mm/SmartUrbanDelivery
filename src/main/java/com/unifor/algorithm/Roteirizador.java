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
 * 
 * REFATORADO: Agora garante que TODOS os clientes sejam atendidos através de múltiplas viagens.
 * O veículo retorna à central para descarregar e recarregar quando necessário.
 */
public class Roteirizador {

    /**
     * Construtor padrão.
     */
    public Roteirizador() {
    }

    /**
     * Calcula as rotas de entrega otimizadas utilizando algoritmo guloso.
     * 
     * NOVO COMPORTAMENTO:
     * - Garante que TODOS os clientes sejam atendidos
     * - Quando o veículo não pode atender mais clientes (capacidade/autonomia),
     *   retorna à central, reseta e inicia uma nova viagem
     * - Retorna uma lista de rotas representando todas as viagens do dia
     * 
     * @param clientes Lista de clientes a serem atendidos
     * @param veiculo Veículo que realizará as entregas
     * @param central Ponto da central de distribuição
     * @return Lista de Rotas calculadas (múltiplas viagens)
     * @throws RuntimeException se algum cliente for inviável (demanda > capacidade ou fora da autonomia)
     */
    public List<Rota> calcularRotas(List<Cliente> clientes, Veiculo veiculo, Ponto central) {
        // Validações de entrada
        if (clientes == null || veiculo == null || central == null) {
            return new ArrayList<>();
        }

        if (clientes.isEmpty()) {
            return new ArrayList<>();
        }

        // VALIDAÇÃO CRÍTICA: Verificar se todos os clientes são viáveis
        validarViabilidadeClientes(clientes, veiculo, central);

        // Lista de rotas (múltiplas viagens)
        List<Rota> rotas = new ArrayList<>();

        // Criar cópia da lista de clientes para não modificar a original
        List<Cliente> naoVisitados = new ArrayList<>(clientes);

        // ORDENAÇÃO INICIAL: Ordenar por prioridade decrescente (REQUISITO)
        Ordenacao.quickSort(naoVisitados);

        // LOOP PRINCIPAL: Enquanto houver clientes não atendidos
        while (!naoVisitados.isEmpty()) {
            // Iniciar nova viagem
            Rota rotaAtual = new Rota();
            
            // Resetar veículo para nova viagem
            veiculo.setCargaAtual(0.0);
            veiculo.setAutonomiaRestante(veiculo.getAutonomiaMaxima());
            veiculo.setLocalizacaoAtual(central);
            
            Ponto localAtual = central;

            // LOOP GULOSO: Processar clientes até que não haja mais candidatos viáveis nesta viagem
            while (!naoVisitados.isEmpty()) {
                // Encontrar o próximo cliente mais próximo que satisfaz as restrições
                Cliente proximoCliente = encontrarVizinhoMaisProximo(localAtual, naoVisitados, veiculo, central);

                // Se nenhum cliente for viável nesta viagem, encerrar e iniciar nova
                if (proximoCliente == null) {
                    break;
                }

                // Calcular distância até o próximo cliente
                double distanciaPercorrida = Distancia.calcularDistanciaEuclidiana(
                    localAtual, 
                    proximoCliente.getLocalizacao()
                );

                // Adicionar cliente à rota
                rotaAtual.adicionarCliente(proximoCliente);

                // Atualizar veículo: aumentar carga e diminuir autonomia
                veiculo.adicionarCarga(proximoCliente.getDemandaCarga());
                veiculo.consumirAutonomia(distanciaPercorrida);

                // Atualizar distância total da rota
                rotaAtual.setDistanciaTotal(rotaAtual.getDistanciaTotal() + distanciaPercorrida);

                // Atualizar localização atual
                localAtual = proximoCliente.getLocalizacao();

                // Remover cliente da lista de não visitados
                naoVisitados.remove(proximoCliente);
            }

            // RETORNO À BASE: Calcular trajeto de volta para a central
            if (!rotaAtual.getPontos().isEmpty()) {
                double distanciaRetorno = Distancia.calcularDistanciaEuclidiana(localAtual, central);
                
                // Atualizar autonomia do veículo com o retorno
                veiculo.consumirAutonomia(distanciaRetorno);
                
                // Atualizar distância total da rota com o retorno
                rotaAtual.setDistanciaTotal(rotaAtual.getDistanciaTotal() + distanciaRetorno);

                // Adicionar rota à lista de rotas
                rotas.add(rotaAtual);
            }
        }

        return rotas;
    }

    /**
     * Método legado para compatibilidade - retorna uma única rota.
     * 
     * @deprecated Use {@link #calcularRotas(List, Veiculo, Ponto)} para garantir atendimento completo.
     * @param clientes Lista de clientes a serem atendidos
     * @param veiculo Veículo que realizará as entregas
     * @param central Ponto da central de distribuição
     * @return Primeira rota calculada (pode não atender todos os clientes)
     */
    @Deprecated
    public Rota calcularRota(List<Cliente> clientes, Veiculo veiculo, Ponto central) {
        List<Rota> rotas = calcularRotas(clientes, veiculo, central);
        return rotas.isEmpty() ? new Rota() : rotas.get(0);
    }

    /**
     * Valida se todos os clientes são viáveis para atendimento.
     * 
     * Um cliente é INVIÁVEL se:
     * 1. Sua demanda excede a capacidade máxima do veículo
     * 2. A distância ida+volta (central->cliente->central) excede a autonomia máxima
     * 
     * @param clientes Lista de clientes a validar
     * @param veiculo Veículo com as restrições
     * @param central Ponto da central
     * @throws RuntimeException se algum cliente for inviável
     */
    private void validarViabilidadeClientes(List<Cliente> clientes, Veiculo veiculo, Ponto central) {
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            
            if (cliente == null || cliente.getLocalizacao() == null) {
                continue;
            }

            // Validação 1: Demanda vs Capacidade
            if (cliente.getDemandaCarga() > veiculo.getCapacidadeMaxima()) {
                throw new RuntimeException(String.format(
                    "CLIENTE INVIÁVEL: Cliente [%d] com demanda de %.2f kg excede a capacidade máxima do veículo (%.2f kg). " +
                    "Impossível atender este cliente com o veículo atual.",
                    i + 1,
                    cliente.getDemandaCarga(),
                    veiculo.getCapacidadeMaxima()
                ));
            }

            // Validação 2: Distância ida+volta vs Autonomia
            double distanciaIdaVolta = 2 * Distancia.calcularDistanciaEuclidiana(
                central, 
                cliente.getLocalizacao()
            );

            if (distanciaIdaVolta > veiculo.getAutonomiaMaxima()) {
                throw new RuntimeException(String.format(
                    "CLIENTE INVIÁVEL: Cliente [%d] na posição (%.2f, %.2f) está a %.2f km da central. " +
                    "A distância ida+volta (%.2f km) excede a autonomia máxima do veículo (%.2f km). " +
                    "Impossível atender este cliente com o veículo atual.",
                    i + 1,
                    cliente.getLocalizacao().getX(),
                    cliente.getLocalizacao().getY(),
                    distanciaIdaVolta / 2,
                    distanciaIdaVolta,
                    veiculo.getAutonomiaMaxima()
                ));
            }
        }
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
