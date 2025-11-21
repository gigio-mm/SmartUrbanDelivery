package com.unifor;

import java.util.ArrayList;
import java.util.List;

import com.unifor.algorithm.Roteirizador;
import com.unifor.model.Central;
import com.unifor.model.Cliente;
import com.unifor.model.Ponto;
import com.unifor.model.Rota;
import com.unifor.model.Veiculo;

/**
 * Classe principal para execução e teste do sistema de logística de entregas inteligentes.
 */
public class Main {

    public static void main(String[] args) {
        // ===== SETUP DE DADOS =====
        
        // Criar Central na coordenada (0,0)
        Central central = new Central("Central de Distribuição UNIFOR");
        Ponto pontoCentral = central.getLocalizacao();
        
        // Criar Veículo com capacidade e autonomia definidas
        Veiculo veiculo = new Veiculo(
            100.0,  // capacidadeMaxima
            200.0,  // autonomiaMaxima
            pontoCentral,  // localizacaoAtual
            0.0,    // cargaAtual
            200.0   // autonomiaRestante
        );
        
        // Criar lista de clientes com dados variados
        List<Cliente> clientes = new ArrayList<>();
        
        // Cliente 1 - Alta prioridade, próximo, carga leve
        clientes.add(new Cliente(new Ponto(5.0, 5.0), 10.0, 10));
        
        // Cliente 2 - Alta prioridade, médio, carga média
        clientes.add(new Cliente(new Ponto(15.0, 8.0), 20.0, 9));
        
        // Cliente 3 - Média prioridade, distante, carga pesada
        clientes.add(new Cliente(new Ponto(30.0, 25.0), 35.0, 5));
        
        // Cliente 4 - Baixa prioridade, próximo, carga leve
        clientes.add(new Cliente(new Ponto(8.0, 12.0), 8.0, 3));
        
        // Cliente 5 - Alta prioridade, distante, carga média
        clientes.add(new Cliente(new Ponto(22.0, 18.0), 15.0, 8));
        
        // Cliente 6 - Média prioridade, médio, carga leve
        clientes.add(new Cliente(new Ponto(12.0, 15.0), 12.0, 6));
        
        // Cliente 7 - Baixa prioridade, muito distante, carga pesada
        clientes.add(new Cliente(new Ponto(45.0, 40.0), 40.0, 2));
        
        // Cliente 8 - Alta prioridade, próximo, carga muito leve
        clientes.add(new Cliente(new Ponto(7.0, 3.0), 5.0, 10));
        
        // Cliente 9 - Média prioridade, médio, carga média
        clientes.add(new Cliente(new Ponto(18.0, 20.0), 18.0, 7));
        
        // Cliente 10 - Baixa prioridade, distante, carga média
        clientes.add(new Cliente(new Ponto(35.0, 15.0), 22.0, 4));
        
        // ===== EXECUÇÃO =====
        
        // Instanciar Roteirizador
        Roteirizador roteirizador = new Roteirizador();
        
        // Capturar tempo inicial
        long tempoInicio = System.nanoTime();
        
        // Executar cálculo da rota
        Rota rota = roteirizador.calcularRota(clientes, veiculo, pontoCentral);
        
        // Capturar tempo final
        long tempoFim = System.nanoTime();
        
        // Calcular tempo de execução em milissegundos
        double tempoExecucao = (tempoFim - tempoInicio) / 1_000_000.0;
        
        // ===== SAÍDA FORMATADA =====
        
        System.out.println("=== ROTA OTIMIZADA (Heurística do Vizinho Mais Próximo) ===");
        System.out.printf("Tempo de Execução: %.3f ms%n", tempoExecucao);
        System.out.println();
        
        // Exibir partida da central
        System.out.printf("-> Veículo partindo da Central (%.1f, %.1f)%n", 
            pontoCentral.getX(), pontoCentral.getY());
        System.out.println();
        
        // Exibir clientes atendidos na rota
        List<Cliente> clientesAtendidos = rota.getPontos();
        Ponto pontoAnterior = pontoCentral;
        
        for (int i = 0; i < clientesAtendidos.size(); i++) {
            Cliente cliente = clientesAtendidos.get(i);
            Ponto locCliente = cliente.getLocalizacao();
            
            // Calcular distância do ponto anterior até este cliente
            double distancia = Math.sqrt(
                Math.pow(locCliente.getX() - pontoAnterior.getX(), 2) + 
                Math.pow(locCliente.getY() - pontoAnterior.getY(), 2)
            );
            
            System.out.printf("%d. Cliente [%d] | Prioridade: %d | Local: (%.1f, %.1f) | Carga: %.1f kg | Dist: %.2f km%n",
                i + 1,
                clientes.indexOf(cliente) + 1,  // ID baseado na posição original
                cliente.getPrioridade(),
                locCliente.getX(),
                locCliente.getY(),
                cliente.getDemandaCarga(),
                distancia
            );
            
            pontoAnterior = locCliente;
        }
        
        System.out.println();
        
        // Exibir retorno à central
        System.out.printf("-> Retorno à Central (%.1f, %.1f)%n", 
            pontoCentral.getX(), pontoCentral.getY());
        System.out.println();
        
        // ===== RESUMO DA OPERAÇÃO =====
        
        System.out.println("=== RESUMO DA OPERAÇÃO ===");
        System.out.printf("Distância Total Percorrida: %.2f km%n", rota.getDistanciaTotal());
        System.out.printf("Autonomia Restante: %.2f km%n", veiculo.getAutonomiaRestante());
        System.out.printf("Carga Total Entregue: %.2f kg%n", rota.getCargaTotalColetada());
        System.out.printf("Capacidade Restante: %.2f kg%n", 
            veiculo.getCapacidadeMaxima() - veiculo.getCargaAtual());
        System.out.printf("Clientes Atendidos: %d / %d%n", 
            clientesAtendidos.size(), clientes.size());
        System.out.println();
        
        // ===== CLIENTES NÃO ATENDIDOS =====
        
        // Identificar clientes não atendidos comparando lista original com rota
        List<Cliente> clientesNaoAtendidos = new ArrayList<>();
        for (Cliente cliente : clientes) {
            if (!clientesAtendidos.contains(cliente)) {
                clientesNaoAtendidos.add(cliente);
            }
        }
        
        if (!clientesNaoAtendidos.isEmpty()) {
            System.out.println("Clientes Não Atendidos (Fora da Rota):");
            for (Cliente cliente : clientesNaoAtendidos) {
                System.out.printf("  - Cliente [%d] (Prioridade: %d)%n",
                    clientes.indexOf(cliente) + 1,
                    cliente.getPrioridade()
                );
            }
        } else {
            System.out.println("✓ Todos os clientes foram atendidos!");
        }
        
        System.out.println();
        System.out.println("=== FIM DA EXECUÇÃO ===");
    }
}
