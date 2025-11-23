package com.unifor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.unifor.algorithm.Roteirizador;
import com.unifor.model.Central;
import com.unifor.model.Cliente;
import com.unifor.model.Ponto;
import com.unifor.model.Rota;
import com.unifor.model.Veiculo;

/**
 * Classe para análise experimental do desempenho do algoritmo de roteirização.
 * 
 * Objetivos:
 * - Medir o tempo de execução para diferentes quantidades de clientes
 * - Gerar dados para análise da complexidade do algoritmo
 * - Produzir tabela formatada para inclusão em relatório técnico
 */
public class TesteDesempenho {
    
    private static final Random random = new Random(42); // Seed fixa para reprodutibilidade
    private static final double RAIO_GERACAO = 100.0;
    private static final int REPETICOES = 5; // Número de execuções por tamanho para média
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   ANÁLISE EXPERIMENTAL - SMART URBAN DELIVERY             ║");
        System.out.println("║   Teste de Desempenho do Algoritmo de Roteirização        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Array de tamanhos de teste
        int[] tamanhos = {10, 50, 100, 500, 1000, 5000};
        
        System.out.println("Configurações do Teste:");
        System.out.println("- Repetições por tamanho: " + REPETICOES);
        System.out.println("- Raio de geração: " + RAIO_GERACAO + " unidades");
        System.out.println("- Capacidade do veículo: INFINITA (sem restrição)");
        System.out.println("- Autonomia do veículo: INFINITA (sem restrição)");
        System.out.println("- Seed aleatória: 42 (para reprodutibilidade)");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        
        // Cabeçalho da tabela em formato Markdown
        System.out.println("### Resultados da Análise Experimental");
        System.out.println();
        System.out.println("| N (Clientes) | Tempo Médio (ms) | Tempo Mín (ms) | Tempo Máx (ms) | Desvio Padrão (ms) |");
        System.out.println("|--------------|------------------|----------------|----------------|--------------------|");
        
        // Execução dos testes
        for (int n : tamanhos) {
            ResultadoTeste resultado = executarTeste(n);
            System.out.printf("| %,12d | %,16.2f | %,14.2f | %,14.2f | %,18.2f |%n",
                n, 
                resultado.tempoMedio,
                resultado.tempoMinimo,
                resultado.tempoMaximo,
                resultado.desvioPadrao
            );
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("### Dados em formato CSV (para Excel/Google Sheets):");
        System.out.println();
        System.out.println("N,Tempo_Medio_ms,Tempo_Min_ms,Tempo_Max_ms,Desvio_Padrao_ms");
        
        // Regerar dados em formato CSV
        random.setSeed(42); // Reset da seed para mesmos dados
        for (int n : tamanhos) {
            ResultadoTeste resultado = executarTeste(n);
            System.out.printf("%d,%.2f,%.2f,%.2f,%.2f%n",
                n,
                resultado.tempoMedio,
                resultado.tempoMinimo,
                resultado.tempoMaximo,
                resultado.desvioPadrao
            );
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("Teste concluído com sucesso!");
    }
    
    /**
     * Executa o teste para um tamanho específico de clientes.
     * 
     * @param n Número de clientes
     * @return Resultado estatístico do teste
     */
    private static ResultadoTeste executarTeste(int n) {
        double[] tempos = new double[REPETICOES];
        
        for (int rep = 0; rep < REPETICOES; rep++) {
            // Gerar cenário de teste
            List<Cliente> clientes = gerarClientes(n);
            Central central = new Central("Central Teste");
            Veiculo veiculo = criarVeiculoInfinito();
            Roteirizador roteirizador = new Roteirizador();
            
            // Medir tempo de execução
            long inicio = System.nanoTime();
            Rota rota = roteirizador.calcularRota(clientes, veiculo, central.getLocalizacao());
            long fim = System.nanoTime();
            
            // Converter para milissegundos
            tempos[rep] = (fim - inicio) / 1_000_000.0;
        }
        
        // Calcular estatísticas
        return calcularEstatisticas(tempos);
    }
    
    /**
     * Gera uma lista de clientes com coordenadas e prioridades aleatórias.
     * 
     * @param quantidade Número de clientes a gerar
     * @return Lista de clientes gerados
     */
    private static List<Cliente> gerarClientes(int quantidade) {
        List<Cliente> clientes = new ArrayList<>();
        
        for (int i = 0; i < quantidade; i++) {
            // Gerar coordenadas polares aleatórias
            double angulo = random.nextDouble() * 2 * Math.PI;
            double raio = random.nextDouble() * RAIO_GERACAO;
            
            // Converter para coordenadas cartesianas
            double x = raio * Math.cos(angulo);
            double y = raio * Math.sin(angulo);
            
            // Gerar demanda e prioridade aleatórias
            double demanda = 10.0 + random.nextDouble() * 40.0; // 10-50
            int prioridade = 1 + random.nextInt(10); // 1-10
            
            clientes.add(new Cliente(new Ponto(x, y), demanda, prioridade));
        }
        
        return clientes;
    }
    
    /**
     * Cria um veículo com capacidade e autonomia infinitas para não limitar o teste.
     * 
     * @return Veículo configurado
     */
    private static Veiculo criarVeiculoInfinito() {
        return new Veiculo(
            Double.MAX_VALUE,  // Capacidade infinita
            Double.MAX_VALUE,  // Autonomia infinita
            new Ponto(0, 0),   // Localização inicial na central
            0.0,               // Sem carga inicial
            Double.MAX_VALUE   // Autonomia restante infinita
        );
    }
    
    /**
     * Calcula estatísticas descritivas de um conjunto de tempos.
     * 
     * @param tempos Array de tempos medidos
     * @return Resultado com estatísticas calculadas
     */
    private static ResultadoTeste calcularEstatisticas(double[] tempos) {
        ResultadoTeste resultado = new ResultadoTeste();
        
        // Média
        double soma = 0;
        resultado.tempoMinimo = Double.MAX_VALUE;
        resultado.tempoMaximo = Double.MIN_VALUE;
        
        for (double tempo : tempos) {
            soma += tempo;
            if (tempo < resultado.tempoMinimo) {
                resultado.tempoMinimo = tempo;
            }
            if (tempo > resultado.tempoMaximo) {
                resultado.tempoMaximo = tempo;
            }
        }
        
        resultado.tempoMedio = soma / tempos.length;
        
        // Desvio padrão
        double somaQuadrados = 0;
        for (double tempo : tempos) {
            double diff = tempo - resultado.tempoMedio;
            somaQuadrados += diff * diff;
        }
        resultado.desvioPadrao = Math.sqrt(somaQuadrados / tempos.length);
        
        return resultado;
    }
    
    /**
     * Classe interna para armazenar resultados estatísticos.
     */
    private static class ResultadoTeste {
        double tempoMedio;
        double tempoMinimo;
        double tempoMaximo;
        double desvioPadrao;
    }
}
