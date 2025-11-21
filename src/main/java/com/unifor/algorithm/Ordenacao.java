package com.unifor.algorithm;

import java.util.List;

import com.unifor.model.Cliente;

/**
 * Classe para algoritmos de ordenação.
 */
public class Ordenacao {

    /**
     * Construtor privado para evitar instanciação.
     */
    private Ordenacao() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    /**
     * Ordena uma lista de clientes por prioridade em ordem decrescente usando QuickSort.
     * Implementação manual do algoritmo QuickSort.
     * 
     * @param clientes Lista de clientes a ser ordenada
     * @throws IllegalArgumentException se a lista for nula
     */
    public static void quickSort(List<Cliente> clientes) {
        if (clientes == null) {
            throw new IllegalArgumentException("A lista de clientes não pode ser nula");
        }

        if (clientes.isEmpty()) {
            return;
        }

        quickSortRecursivo(clientes, 0, clientes.size() - 1);
    }

    /**
     * Método recursivo do QuickSort.
     * 
     * @param clientes Lista de clientes
     * @param inicio Índice inicial
     * @param fim Índice final
     */
    private static void quickSortRecursivo(List<Cliente> clientes, int inicio, int fim) {
        if (inicio < fim) {
            int indicePivo = particionar(clientes, inicio, fim);
            quickSortRecursivo(clientes, inicio, indicePivo - 1);
            quickSortRecursivo(clientes, indicePivo + 1, fim);
        }
    }

    /**
     * Particiona a lista e retorna o índice do pivô.
     * Ordena por prioridade em ordem decrescente (maior prioridade primeiro).
     * 
     * @param clientes Lista de clientes
     * @param inicio Índice inicial
     * @param fim Índice final
     * @return Índice do pivô após particionamento
     */
    private static int particionar(List<Cliente> clientes, int inicio, int fim) {
        Cliente pivo = clientes.get(fim);
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            // Ordena em ordem decrescente (maior prioridade primeiro)
            if (clientes.get(j).getPrioridade() > pivo.getPrioridade()) {
                i++;
                trocar(clientes, i, j);
            }
        }

        trocar(clientes, i + 1, fim);
        return i + 1;
    }

    /**
     * Troca dois elementos de posição na lista.
     * 
     * @param clientes Lista de clientes
     * @param i Índice do primeiro elemento
     * @param j Índice do segundo elemento
     */
    private static void trocar(List<Cliente> clientes, int i, int j) {
        Cliente temp = clientes.get(i);
        clientes.set(i, clientes.get(j));
        clientes.set(j, temp);
    }
}
