package br.com.empadasdabia.view.componentes;

import br.com.empadasdabia.model.StatusPedido;
import br.com.empadasdabia.utils.Tema;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Desenha o status do pedido como um selo colorido dentro da tabela. */
public class RendererStatus extends DefaultTableCellRenderer {

    private StatusPedido status;
    private Color fundoLinha;

    @Override
    public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado,
                                                   boolean foco, int linha, int coluna) {
        status = (StatusPedido) valor;
        fundoLinha = selecionado ? tabela.getSelectionBackground() : tabela.getBackground();
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(fundoLinha);
        g2.fillRect(0, 0, getWidth(), getHeight());
        if (status != null) {
            String texto = status.name();
            g2.setFont(Tema.fonte(Font.BOLD, 11.5f));
            FontMetrics medidas = g2.getFontMetrics();
            int largura = medidas.stringWidth(texto) + 20;
            int altura = 22;
            int y = (getHeight() - altura) / 2;
            Color[] cores = Tema.coresStatus(status);
            g2.setColor(cores[0]);
            g2.fillRoundRect(14, y, largura, altura, altura, altura);
            g2.setColor(cores[1]);
            g2.drawString(texto, 24, y + (altura + medidas.getAscent() - medidas.getDescent()) / 2);
        }
        g2.dispose();
    }
}
