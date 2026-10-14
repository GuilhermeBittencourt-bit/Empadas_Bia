package br.com.empadasdabia.view.componentes;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 * Painel para usar dentro de um JScrollPane que acompanha a largura da janela:
 * a rolagem é só vertical e as grades de cartões se ajustam à largura disponível.
 */
public class PainelRolavel extends JPanel implements Scrollable {

    public PainelRolavel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle area, int orientacao, int direcao) {
        return 18;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle area, int orientacao, int direcao) {
        return area.height - 40;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
