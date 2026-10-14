package br.com.empadasdabia.view.componentes;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/** Painel com cantos arredondados e borda: é a base dos "cartões" do sistema. */
public class PainelArredondado extends JPanel {

    private Color fundo;
    private Color borda;
    private int espessura;
    private final int arco;

    public PainelArredondado(LayoutManager layout, Color fundo, Color borda, int arco) {
        super(layout);
        this.fundo = fundo;
        this.borda = borda;
        this.arco = arco;
        this.espessura = borda == null ? 0 : 1;
        setOpaque(false);
    }

    /** Muda a borda (usado para destacar o cartão selecionado). */
    public void setBorda(Color borda, int espessura) {
        this.borda = borda;
        this.espessura = espessura;
        repaint();
    }

    public void setFundo(Color fundo) {
        this.fundo = fundo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fundo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
        if (borda != null && espessura > 0) {
            g2.setColor(borda);
            g2.setStroke(new BasicStroke(espessura));
            int meio = espessura / 2;
            g2.drawRoundRect(meio, meio, getWidth() - 1 - espessura, getHeight() - 1 - espessura, arco, arco);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
