package com.unifor.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.unifor.model.Cliente;
import com.unifor.model.Ponto;
import com.unifor.model.Rota;

/**
 * Painel responsável por desenhar visualmente a simulação de roteirização.
 * Implementa transformação World-to-Screen com escalamento automático e responsivo.
 * 
 * Sistema de Coordenadas:
 * - World Space: Coordenadas do domínio (ex: 0-100 ou -50 a 50)
 * - Screen Space: Pixels da tela com origem no canto superior esquerdo
 * - Transformação: Inclui escala, translação e inversão do eixo Y
 */
public class PainelMapa extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Configurações de renderização
    private static final int PADDING = 50;              // Margem em pixels
    private static final int TAMANHO_CENTRAL = 24;      // Tamanho do quadrado da central
    private static final int TAMANHO_CLIENTE = 12;      // Diâmetro dos círculos dos clientes
    private static final int TAMANHO_FONTE_LABEL = 10;  // Tamanho da fonte das labels
    
    // Cores - Tema Escuro Moderno
    private static final Color COR_CENTRAL = new Color(64, 156, 255);      // Azul brilhante
    private static final Color COR_CLIENTE = new Color(255, 107, 107);     // Vermelho coral
    private static final Color COR_CLIENTE_PRIORITARIO = new Color(255, 69, 0); // Laranja forte
    private static final Color COR_ROTA_IDA = new Color(72, 219, 109);     // Verde neon
    private static final Color COR_ROTA_VOLTA = new Color(255, 184, 77);   // Laranja claro
    private static final Color COR_FUNDO = new Color(30, 30, 30);          // Cinza escuro
    private static final Color COR_GRID = new Color(50, 50, 50);           // Grid sutil
    private static final Color COR_TEXTO = new Color(220, 220, 220);       // Branco suave
    private static final Color COR_EIXOS = new Color(80, 80, 80);          // Cinza para eixos
    
    // Dados do modelo
    private Ponto central;
    private List<Cliente> clientes;
    private Rota rota;
    
    // Parâmetros de transformação World-to-Screen (calculados dinamicamente)
    private double minX, maxX, minY, maxY;  // Limites do mundo
    private double worldWidth, worldHeight;  // Dimensões do mundo
    private double scale;                    // Fator de escala (pixels por unidade do mundo)
    private double offsetX, offsetY;         // Deslocamento para centralização
    
    /**
     * Construtor padrão.
     */
    public PainelMapa() {
        this.central = new Ponto(0.0, 0.0);
        this.clientes = new ArrayList<>();
        this.rota = null;
        setBackground(COR_FUNDO);
    }
    
    /**
     * Define a central de distribuição.
     * 
     * @param central Ponto da central
     */
    public void setCentral(Ponto central) {
        this.central = central;
        repaint();
    }
    
    /**
     * Define a lista de clientes a serem exibidos.
     * 
     * @param clientes Lista de clientes
     */
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes != null ? clientes : new ArrayList<>();
        repaint();
    }
    
    /**
     * Define a rota calculada a ser desenhada.
     * 
     * @param rota Rota calculada
     */
    public void setRota(Rota rota) {
        this.rota = rota;
        repaint();
    }
    
    /**
     * Limpa o mapa (remove rota mas mantém clientes).
     */
    public void limparRota() {
        this.rota = null;
        repaint();
    }
    
    /**
     * Limpa completamente o mapa (clientes e rota).
     */
    public void limparTudo() {
        this.clientes.clear();
        this.rota = null;
        repaint();
    }
    
    /**
     * Calcula os limites do mundo (bounding box) baseado em todos os pontos.
     * Atualiza: minX, maxX, minY, maxY, worldWidth, worldHeight
     */
    private void calcularLimitesMundo() {
        // Inicializar com a central
        minX = central.getX();
        maxX = central.getX();
        minY = central.getY();
        maxY = central.getY();
        
        // Expandir limites incluindo todos os clientes
        for (Cliente cliente : clientes) {
            Ponto loc = cliente.getLocalizacao();
            minX = Math.min(minX, loc.getX());
            maxX = Math.max(maxX, loc.getX());
            minY = Math.min(minY, loc.getY());
            maxY = Math.max(maxY, loc.getY());
        }
        
        // Calcular dimensões do mundo
        worldWidth = maxX - minX;
        worldHeight = maxY - minY;
        
        // Garantir dimensões mínimas para evitar divisão por zero
        if (worldWidth < 1.0) worldWidth = 100.0;
        if (worldHeight < 1.0) worldHeight = 100.0;
        
        // Adicionar margem extra (10% do tamanho) para não cortar pontos nas bordas
        double margem = 0.1;
        double deltaX = worldWidth * margem;
        double deltaY = worldHeight * margem;
        
        minX -= deltaX / 2;
        maxX += deltaX / 2;
        minY -= deltaY / 2;
        maxY += deltaY / 2;
        
        worldWidth = maxX - minX;
        worldHeight = maxY - minY;
    }
    
    /**
     * Calcula o fator de escala e os offsets para a transformação World-to-Screen.
     * Deve ser chamado sempre que o painel for redimensionado ou os dados mudarem.
     */
    private void calcularTransformacao() {
        // Se não houver clientes, usar valores padrão
        if (clientes.isEmpty()) {
            scale = 5.0;
            offsetX = getWidth() / 2.0;
            offsetY = getHeight() / 2.0;
            return;
        }
        
        // Calcular limites do mundo
        calcularLimitesMundo();
        
        // Calcular área disponível na tela (descontando padding)
        int larguraTela = getWidth() - 2 * PADDING;
        int alturaTela = getHeight() - 2 * PADDING;
        
        // Garantir valores mínimos
        if (larguraTela < 100) larguraTela = 100;
        if (alturaTela < 100) alturaTela = 100;
        
        // Calcular fator de escala para cada eixo
        double scaleX = larguraTela / worldWidth;
        double scaleY = alturaTela / worldHeight;
        
        // Usar a menor escala para manter proporções (fit to viewport)
        scale = Math.min(scaleX, scaleY);
        
        // Calcular offsets para centralizar o conteúdo
        // Offset X: centraliza horizontalmente
        offsetX = PADDING + (larguraTela - worldWidth * scale) / 2.0;
        
        // Offset Y: centraliza verticalmente + inverte eixo Y
        // (em Swing, Y=0 é no topo; queremos Y crescendo para cima)
        offsetY = getHeight() - PADDING - (alturaTela - worldHeight * scale) / 2.0;
    }
    
    /**
     * Transforma coordenadas do mundo para coordenadas da tela.
     * Aplica escala, translação e inversão do eixo Y.
     * 
     * @param mundo Ponto em coordenadas do mundo
     * @return Ponto em coordenadas da tela (pixels)
     */
    private Point worldToScreen(Ponto mundo) {
        // Fórmula World-to-Screen:
        // screenX = offsetX + (worldX - minX) * scale
        // screenY = offsetY - (worldY - minY) * scale  (note o MENOS para inverter Y)
        
        int screenX = (int) (offsetX + (mundo.getX() - minX) * scale);
        int screenY = (int) (offsetY - (mundo.getY() - minY) * scale);
        
        return new Point(screenX, screenY);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Configurar Graphics2D com alta qualidade de renderização
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // PASSO 1: Calcular transformação de coordenadas
        calcularTransformacao();
        
        // PASSO 2: Desenhar elementos na ordem correta (de trás para frente)
        desenharGrid(g2d);
        desenharEixos(g2d);
        
        // Desenhar rota PRIMEIRO (camada de fundo)
        if (rota != null && rota.getPontos() != null && !rota.getPontos().isEmpty()) {
            desenharRota(g2d);
        }
        
        // Desenhar pontos POR CIMA da rota
        desenharCentral(g2d);
        desenharClientes(g2d);
        
        // Desenhar informações adicionais
        desenharLegenda(g2d);
        desenharEstatisticas(g2d);
    }
    
    /**
     * Desenha um grid sutil de fundo para referência espacial.
     * 
     * @param g2d Contexto gráfico
     */
    private void desenharGrid(Graphics2D g2d) {
        g2d.setColor(COR_GRID);
        g2d.setStroke(new BasicStroke(1));
        
        int espacamento = 40;
        int largura = getWidth();
        int altura = getHeight();
        
        // Linhas verticais
        for (int x = 0; x < largura; x += espacamento) {
            g2d.drawLine(x, 0, x, altura);
        }
        
        // Linhas horizontais
        for (int y = 0; y < altura; y += espacamento) {
            g2d.drawLine(0, y, largura, y);
        }
    }
    
    /**
     * Desenha os eixos cartesianos centralizados na origem do mundo.
     * 
     * @param g2d Contexto gráfico
     */
    private void desenharEixos(Graphics2D g2d) {
        if (clientes.isEmpty()) return;
        
        g2d.setColor(COR_EIXOS);
        g2d.setStroke(new BasicStroke(2));
        
        // Eixo X (horizontal passando por Y=0 do mundo)
        Point esquerda = worldToScreen(new Ponto(minX, 0));
        Point direita = worldToScreen(new Ponto(maxX, 0));
        g2d.drawLine(esquerda.x, esquerda.y, direita.x, direita.y);
        
        // Eixo Y (vertical passando por X=0 do mundo)
        Point baixo = worldToScreen(new Ponto(0, minY));
        Point cima = worldToScreen(new Ponto(0, maxY));
        g2d.drawLine(baixo.x, baixo.y, cima.x, cima.y);
    }
    
    /**
     * Desenha a central de distribuição como um quadrado azul.
     * 
     * @param g2d Contexto gráfico
     */
    private void desenharCentral(Graphics2D g2d) {
        Point screen = worldToScreen(central);
        
        // Desenhar quadrado preenchido
        Rectangle2D quadrado = new Rectangle2D.Double(
            screen.x - TAMANHO_CENTRAL / 2.0,
            screen.y - TAMANHO_CENTRAL / 2.0,
            TAMANHO_CENTRAL,
            TAMANHO_CENTRAL
        );
        
        g2d.setColor(COR_CENTRAL);
        g2d.fill(quadrado);
        
        // Desenhar borda
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(quadrado);
        
        // Desenhar label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("CENTRAL", screen.x + TAMANHO_CENTRAL / 2 + 8, screen.y + 5);
        
        // Desenhar coordenadas
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        String coords = String.format("(%.1f, %.1f)", central.getX(), central.getY());
        g2d.drawString(coords, screen.x + TAMANHO_CENTRAL / 2 + 8, screen.y + 17);
    }
    
    /**
     * Desenha os clientes como círculos coloridos com labels.
     * 
     * @param g2d Contexto gráfico
     */
    private void desenharClientes(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, TAMANHO_FONTE_LABEL));
        
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            Ponto loc = cliente.getLocalizacao();
            Point screen = worldToScreen(loc);
            
            // Desenhar círculo preenchido
            Ellipse2D circulo = new Ellipse2D.Double(
                screen.x - TAMANHO_CLIENTE / 2.0,
                screen.y - TAMANHO_CLIENTE / 2.0,
                TAMANHO_CLIENTE,
                TAMANHO_CLIENTE
            );
            
            // Cor baseada em prioridade (clientes de alta prioridade mais intensos)
            int prioridade = cliente.getPrioridade();
            Color corCliente;
            
            if (prioridade >= 8) {
                corCliente = COR_CLIENTE_PRIORITARIO; // Laranja forte para alta prioridade
            } else {
                // Gradiente de vermelho baseado na prioridade
                int intensidade = Math.max(0, Math.min(255, 107 + (prioridade - 5) * 20));
                corCliente = new Color(255, intensidade, intensidade);
            }
            
            g2d.setColor(corCliente);
            g2d.fill(circulo);
            
            // Desenhar borda
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(circulo);
            
            // Desenhar label com ID do cliente
            g2d.setColor(COR_TEXTO);
            String label = String.format("C%d", i + 1);
            g2d.drawString(label, screen.x + TAMANHO_CLIENTE / 2 + 5, screen.y + 4);
            
            // Mostrar prioridade se for alta
            if (prioridade >= 7) {
                g2d.setColor(new Color(255, 200, 0));
                g2d.drawString("★" + prioridade, screen.x + TAMANHO_CLIENTE / 2 + 5, screen.y + 15);
            }
        }
    }
    
    /**
     * Desenha a rota calculada com linhas conectando os pontos.
     * Diferencia visualmente o trajeto de ida e o retorno à central.
     * 
     * @param g2d Contexto gráfico
     */
    private void desenharRota(Graphics2D g2d) {
        List<Cliente> pontosRota = rota.getPontos();
        if (pontosRota == null || pontosRota.isEmpty()) {
            return;
        }
        
        // Configurar linha mais grossa para a rota
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Desenhar linha da central ao primeiro cliente e entre clientes
        Ponto pontoAtual = central;
        
        for (int i = 0; i < pontosRota.size(); i++) {
            Cliente cliente = pontosRota.get(i);
            Ponto proximoPonto = cliente.getLocalizacao();
            
            Point screen1 = worldToScreen(pontoAtual);
            Point screen2 = worldToScreen(proximoPonto);
            
            // Cor da linha (verde para trajeto de ida)
            g2d.setColor(COR_ROTA_IDA);
            
            Line2D linha = new Line2D.Double(screen1.x, screen1.y, screen2.x, screen2.y);
            g2d.draw(linha);
            
            // Desenhar seta indicando direção
            desenharSeta(g2d, screen1.x, screen1.y, screen2.x, screen2.y, COR_ROTA_IDA);
            
            // Desenhar número da sequência
            int mx = (screen1.x + screen2.x) / 2;
            int my = (screen1.y + screen2.y) / 2;
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString(String.valueOf(i + 1), mx - 5, my - 5);
            
            pontoAtual = proximoPonto;
        }
        
        // Desenhar linha de retorno à central (cor e estilo diferentes)
        if (!pontosRota.isEmpty()) {
            Ponto ultimoPonto = pontosRota.get(pontosRota.size() - 1).getLocalizacao();
            
            Point screen1 = worldToScreen(ultimoPonto);
            Point screen2 = worldToScreen(central);
            
            // Cor laranja para retorno
            g2d.setColor(COR_ROTA_VOLTA);
            
            // Linha tracejada para o retorno
            float[] tracejado = {12.0f, 8.0f};
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                          1.0f, tracejado, 0.0f));
            
            Line2D linha = new Line2D.Double(screen1.x, screen1.y, screen2.x, screen2.y);
            g2d.draw(linha);
            
            // Desenhar seta de retorno
            desenharSeta(g2d, screen1.x, screen1.y, screen2.x, screen2.y, COR_ROTA_VOLTA);
        }
    }
    
    /**
     * Desenha uma pequena seta na direção da linha para indicar o sentido da rota.
     * 
     * @param g2d Contexto gráfico
     * @param x1 Coordenada X inicial
     * @param y1 Coordenada Y inicial
     * @param x2 Coordenada X final
     * @param y2 Coordenada Y final
     * @param cor Cor da seta
     */
    private void desenharSeta(Graphics2D g2d, int x1, int y1, int x2, int y2, Color cor) {
        // Calcular ponto médio
        double mx = (x1 + x2) / 2.0;
        double my = (y1 + y2) / 2.0;
        
        // Calcular ângulo da linha
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        
        // Tamanho da seta
        int tamanhoSeta = 10;
        double anguloSeta = Math.PI / 6; // 30 graus
        
        // Calcular pontos da seta
        int xa1 = (int) (mx - tamanhoSeta * Math.cos(angulo - anguloSeta));
        int ya1 = (int) (my - tamanhoSeta * Math.sin(angulo - anguloSeta));
        int xa2 = (int) (mx - tamanhoSeta * Math.cos(angulo + anguloSeta));
        int ya2 = (int) (my - tamanhoSeta * Math.sin(angulo + anguloSeta));
        
        g2d.setColor(cor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine((int) mx, (int) my, xa1, ya1);
        g2d.drawLine((int) mx, (int) my, xa2, ya2);
    }
    
    /**
     * Desenha uma legenda explicativa no canto superior esquerdo.
     * 
     * @param g2d Contexto gráfico
     */
    private void desenharLegenda(Graphics2D g2d) {
        int x = 10;
        int y = 20;
        int espacamento = 20;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Legenda:", x, y);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        y += espacamento;
        
        // Central
        g2d.setColor(COR_CENTRAL);
        g2d.fillRect(x, y - 8, 14, 14);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Central de Distribuição", x + 22, y + 3);
        
        y += espacamento;
        
        // Cliente
        g2d.setColor(COR_CLIENTE);
        g2d.fillOval(x, y - 8, 14, 14);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Cliente", x + 22, y + 3);
        
        y += espacamento;
        
        // Rota ida
        g2d.setColor(COR_ROTA_IDA);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y - 4, x + 14, y - 4);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Rota (Entrega)", x + 22, y + 3);
        
        y += espacamento;
        
        // Rota volta
        g2d.setColor(COR_ROTA_VOLTA);
        float[] tracejado = {6.0f, 4.0f};
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                      1.0f, tracejado, 0.0f));
        g2d.drawLine(x, y - 4, x + 14, y - 4);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Retorno", x + 22, y + 3);
    }
    
    /**
     * Desenha estatísticas da visualização no canto superior direito.
     * 
     * @param g2d Contexto gráfico
     */
    private void desenharEstatisticas(Graphics2D g2d) {
        if (clientes.isEmpty()) return;
        
        int x = getWidth() - 180;
        int y = 20;
        int espacamento = 16;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(new Color(180, 180, 180));
        
        g2d.drawString("📊 Estatísticas:", x, y);
        y += espacamento;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString(String.format("Clientes: %d", clientes.size()), x, y);
        y += espacamento;
        
        if (rota != null && rota.getPontos() != null) {
            g2d.drawString(String.format("Atendidos: %d", rota.getPontos().size()), x, y);
            y += espacamento;
        }
        
        g2d.drawString(String.format("Escala: %.2fx", scale), x, y);
        y += espacamento;
        
        g2d.drawString(String.format("Mundo: %.1f × %.1f", worldWidth, worldHeight), x, y);
    }
}
