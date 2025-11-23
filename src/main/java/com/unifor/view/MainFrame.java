package com.unifor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.unifor.algorithm.Roteirizador;
import com.unifor.model.Central;
import com.unifor.model.Cliente;
import com.unifor.model.Ponto;
import com.unifor.model.Rota;
import com.unifor.model.Veiculo;

/**
 * Janela principal da aplicação de roteirização com interface gráfica.
 * Permite gerar cenários aleatórios, calcular rotas e visualizar resultados.
 */
public class MainFrame extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Componentes da interface
    private PainelMapa painelMapa;
    private JSpinner spinnerQuantidade;
    private JButton btnGerarCenario;
    private JButton btnCalcularRota;
    private JButton btnLimpar;
    private JTextArea areaLog;
    
    // Dados do cenário
    private Central central;
    private List<Cliente> clientes;
    private Veiculo veiculo;
    private Rota rotaCalculada;
    
    // Configurações padrão
    private static final int QUANTIDADE_PADRAO = 20;
    private static final double RAIO_GERACAO = 100.0;
    private static final double CAPACIDADE_VEICULO = 1000.0;
    private static final double AUTONOMIA_VEICULO = 500.0;
    
    /**
     * Construtor da janela principal.
     */
    public MainFrame() {
        inicializarDados();
        inicializarInterface();
        configurarEventos();
    }
    
    /**
     * Inicializa os dados do cenário.
     */
    private void inicializarDados() {
        this.central = new Central("Central Smart Urban Delivery");
        this.clientes = new ArrayList<>();
        this.veiculo = new Veiculo(CAPACIDADE_VEICULO, AUTONOMIA_VEICULO, 
                                   central.getLocalizacao(), 0.0, AUTONOMIA_VEICULO);
        this.rotaCalculada = null;
    }
    
    /**
     * Inicializa todos os componentes da interface gráfica.
     */
    private void inicializarInterface() {
        // Configurações da janela
        setTitle("Smart Urban Delivery - Visualizador de Rotas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Layout principal
        setLayout(new BorderLayout(10, 10));
        
        // Criar painel do mapa (centro)
        painelMapa = new PainelMapa();
        painelMapa.setCentral(central.getLocalizacao());
        painelMapa.setPreferredSize(new Dimension(800, 600));
        painelMapa.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
        add(painelMapa, BorderLayout.CENTER);
        
        // Criar painel de controle (lateral direita)
        JPanel painelControle = criarPainelControle();
        add(painelControle, BorderLayout.EAST);
        
        // Criar painel de título (topo)
        JPanel painelTitulo = criarPainelTitulo();
        add(painelTitulo, BorderLayout.NORTH);
    }
    
    /**
     * Cria o painel de título no topo da janela.
     * 
     * @return Painel de título configurado
     */
    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painel.setBackground(new Color(40, 40, 40));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titulo = new JLabel("🚚 Smart Urban Delivery - Sistema de Roteirização");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        
        painel.add(titulo);
        return painel;
    }
    
    /**
     * Cria o painel de controle lateral com inputs e botões.
     * 
     * @return Painel de controle configurado
     */
    private JPanel criarPainelControle() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());
        painel.setPreferredSize(new Dimension(350, 0));
        painel.setBackground(new Color(45, 45, 45));
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        // Título da seção
        JLabel lblTitulo = new JLabel("Painel de Controle");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        painel.add(lblTitulo, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        
        // Label quantidade de clientes
        JLabel lblQuantidade = new JLabel("Quantidade de Clientes:");
        lblQuantidade.setFont(new Font("Arial", Font.PLAIN, 12));
        lblQuantidade.setForeground(Color.WHITE);
        painel.add(lblQuantidade, gbc);
        
        gbc.gridx = 1;
        
        // Spinner quantidade
        SpinnerNumberModel modeloSpinner = new SpinnerNumberModel(QUANTIDADE_PADRAO, 1, 100, 1);
        spinnerQuantidade = new JSpinner(modeloSpinner);
        spinnerQuantidade.setFont(new Font("Arial", Font.PLAIN, 12));
        spinnerQuantidade.setBackground(new Color(60, 60, 60));
        spinnerQuantidade.setForeground(Color.WHITE);
        painel.add(spinnerQuantidade, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        
        // Botão Gerar Cenário
        btnGerarCenario = new JButton("Gerar Cenário");
        btnGerarCenario.setFont(new Font("Arial", Font.BOLD, 13));
        btnGerarCenario.setBackground(new Color(56, 142, 60));
        btnGerarCenario.setForeground(Color.WHITE);
        btnGerarCenario.setFocusPainted(false);
        btnGerarCenario.setBorderPainted(false);
        btnGerarCenario.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        painel.add(btnGerarCenario, gbc);
        
        gbc.gridy++;
        
        // Botão Calcular Rota
        btnCalcularRota = new JButton("Calcular Rota");
        btnCalcularRota.setFont(new Font("Arial", Font.BOLD, 13));
        btnCalcularRota.setBackground(new Color(25, 118, 210));
        btnCalcularRota.setForeground(Color.WHITE);
        btnCalcularRota.setFocusPainted(false);
        btnCalcularRota.setBorderPainted(false);
        btnCalcularRota.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnCalcularRota.setEnabled(false);
        painel.add(btnCalcularRota, gbc);
        
        gbc.gridy++;
        
        // Botão Limpar
        btnLimpar = new JButton("Limpar Tudo");
        btnLimpar.setFont(new Font("Arial", Font.BOLD, 13));
        btnLimpar.setBackground(new Color(198, 40, 40));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setBorderPainted(false);
        btnLimpar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        painel.add(btnLimpar, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 5, 5, 5);
        
        // Label Log
        JLabel lblLog = new JLabel("Relatório de Execução:");
        lblLog.setFont(new Font("Arial", Font.BOLD, 14));
        lblLog.setForeground(Color.WHITE);
        painel.add(lblLog, gbc);
        
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Área de texto para log
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setBackground(new Color(35, 35, 35));
        areaLog.setForeground(new Color(200, 200, 200));
        areaLog.setCaretColor(Color.WHITE);
        areaLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        painel.add(scrollLog, gbc);
        
        // Mensagem inicial
        areaLog.setText("Bem-vindo ao Smart Urban Delivery!\n\n" +
                       "Instruções:\n" +
                       "1. Defina a quantidade de clientes\n" +
                       "2. Clique em 'Gerar Cenário'\n" +
                       "3. Clique em 'Calcular Rota'\n\n" +
                       "Aguardando ação...");
        
        return painel;
    }
    
    /**
     * Configura os eventos dos botões.
     */
    private void configurarEventos() {
        // Evento: Gerar Cenário
        btnGerarCenario.addActionListener(e -> gerarCenario());
        
        // Evento: Calcular Rota
        btnCalcularRota.addActionListener(e -> calcularRota());
        
        // Evento: Limpar
        btnLimpar.addActionListener(e -> limparTudo());
    }
    
    /**
     * Gera um cenário aleatório com clientes.
     */
    private void gerarCenario() {
        try {
            int quantidade = (Integer) spinnerQuantidade.getValue();
            
            // Limpar dados anteriores
            clientes.clear();
            rotaCalculada = null;
            painelMapa.limparTudo();
            
            // Gerar clientes aleatórios
            Random random = new Random();
            
            for (int i = 0; i < quantidade; i++) {
                // Coordenadas aleatórias ao redor da central
                double angulo = random.nextDouble() * 2 * Math.PI;
                double raio = random.nextDouble() * RAIO_GERACAO;
                
                double x = raio * Math.cos(angulo);
                double y = raio * Math.sin(angulo);
                
                Ponto localizacao = new Ponto(x, y);
                
                // Demanda aleatória entre 10 e 50
                double demanda = 10 + random.nextDouble() * 40;
                
                // Prioridade aleatória entre 1 e 10
                int prioridade = 1 + random.nextInt(10);
                
                Cliente cliente = new Cliente(localizacao, demanda, prioridade);
                clientes.add(cliente);
            }
            
            // Atualizar mapa
            painelMapa.setClientes(clientes);
            
            // Habilitar botão de calcular rota
            btnCalcularRota.setEnabled(true);
            
            // Atualizar log
            areaLog.setText(String.format(
                "✅ Cenário gerado com sucesso!\n\n" +
                "📊 Estatísticas:\n" +
                "- Clientes: %d\n" +
                "- Área de cobertura: %.0f unidades\n" +
                "- Central: (%.1f, %.1f)\n\n" +
                "Pronto para calcular a rota!",
                quantidade,
                RAIO_GERACAO,
                central.getLocalizacao().getX(),
                central.getLocalizacao().getY()
            ));
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao gerar cenário: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Calcula a rota otimizada usando o algoritmo de roteirização.
     */
    private void calcularRota() {
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Gere um cenário primeiro!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Resetar veículo
            veiculo = new Veiculo(CAPACIDADE_VEICULO, AUTONOMIA_VEICULO,
                                 central.getLocalizacao(), 0.0, AUTONOMIA_VEICULO);
            
            // Medir tempo de execução
            long tempoInicio = System.nanoTime();
            
            // Executar algoritmo de roteirização
            Roteirizador roteirizador = new Roteirizador();
            rotaCalculada = roteirizador.calcularRota(clientes, veiculo, central.getLocalizacao());
            
            long tempoFim = System.nanoTime();
            double tempoExecucao = (tempoFim - tempoInicio) / 1_000_000.0; // Converter para ms
            
            // Atualizar mapa
            painelMapa.setRota(rotaCalculada);
            
            // Gerar relatório
            gerarRelatorio(tempoExecucao);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao calcular rota: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Gera e exibe o relatório detalhado da execução.
     * 
     * @param tempoExecucao Tempo de execução em milissegundos
     */
    private void gerarRelatorio(double tempoExecucao) {
        if (rotaCalculada == null) {
            return;
        }
        
        int clientesAtendidos = rotaCalculada.getPontos().size();
        int clientesTotais = clientes.size();
        double distanciaTotal = rotaCalculada.getDistanciaTotal();
        double cargaTotal = rotaCalculada.getCargaTotalColetada();
        double percentualAtendimento = (clientesTotais > 0) ? 
            (clientesAtendidos * 100.0 / clientesTotais) : 0;
        
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("🎯 ROTA CALCULADA COM SUCESSO!\n");
        relatorio.append("═══════════════════════════════\n\n");
        
        relatorio.append("⏱️ DESEMPENHO:\n");
        relatorio.append(String.format("- Tempo de execução: %.2f ms\n\n", tempoExecucao));
        
        relatorio.append("📍 ATENDIMENTO:\n");
        relatorio.append(String.format("- Clientes atendidos: %d de %d\n", 
            clientesAtendidos, clientesTotais));
        relatorio.append(String.format("- Taxa de atendimento: %.1f%%\n\n", 
            percentualAtendimento));
        
        relatorio.append("🚚 LOGÍSTICA:\n");
        relatorio.append(String.format("- Distância total: %.2f unidades\n", distanciaTotal));
        relatorio.append(String.format("- Carga coletada: %.2f unidades\n", cargaTotal));
        relatorio.append(String.format("- Capacidade veículo: %.0f unidades\n", 
            CAPACIDADE_VEICULO));
        relatorio.append(String.format("- Autonomia veículo: %.0f unidades\n\n", 
            AUTONOMIA_VEICULO));
        
        relatorio.append("📊 EFICIÊNCIA:\n");
        if (clientesAtendidos > 0) {
            double distanciaMedia = distanciaTotal / clientesAtendidos;
            relatorio.append(String.format("- Distância média/cliente: %.2f\n", distanciaMedia));
        }
        
        if (clientesAtendidos < clientesTotais) {
            int naoAtendidos = clientesTotais - clientesAtendidos;
            relatorio.append(String.format("\n⚠️ ATENÇÃO:\n"));
            relatorio.append(String.format("- %d cliente(s) não atendido(s)\n", naoAtendidos));
            relatorio.append("- Possíveis causas: capacidade ou\n  autonomia insuficientes\n");
        }
        
        areaLog.setText(relatorio.toString());
        areaLog.setCaretPosition(0); // Scroll para o topo
    }
    
    /**
     * Limpa todos os dados e reseta a interface.
     */
    private void limparTudo() {
        clientes.clear();
        rotaCalculada = null;
        painelMapa.limparTudo();
        btnCalcularRota.setEnabled(false);
        
        areaLog.setText("✨ Tudo limpo!\n\n" +
                       "Gere um novo cenário para começar.");
    }
    
    /**
     * Método principal para executar a aplicação.
     * 
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // Configurar Look and Feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar Look and Feel padrão em caso de erro
            e.printStackTrace();
        }
        
        // Executar na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
