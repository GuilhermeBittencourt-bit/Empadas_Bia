package br.com.empadasdabia.view.componentes;

import br.com.empadasdabia.utils.Tema;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Pequenos componentes visuais reutilizados por várias telas. */
public final class Componentes {

    private Componentes() {
    }

    /** Cartão branco com borda creme. */
    public static PainelArredondado cartao(java.awt.LayoutManager layout) {
        return new PainelArredondado(layout, Tema.BRANCO, Tema.BORDA, 16);
    }

    /** Topo padrão das telas: título e subtítulo à esquerda, botões à direita. */
    public static JPanel cabecalhoTela(String titulo, String subtitulo, JComponent... botoes) {
        return cabecalhoTela(titulo, Tema.subtitulo(subtitulo), botoes);
    }

    /** Versão em que o subtítulo pode ser alterado depois (ex.: a data no Dashboard). */
    public static JPanel cabecalhoTela(String titulo, JLabel subtitulo, JComponent... botoes) {
        JPanel cabecalho = Tema.painelTransparente(new BorderLayout(16, 0));
        JPanel textos = Tema.painelTransparente(new java.awt.GridLayout(0, 1, 0, 2));
        textos.add(Tema.titulo(titulo));
        textos.add(subtitulo);
        cabecalho.add(textos, BorderLayout.WEST);
        JPanel acoes = Tema.painelTransparente(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 8));
        for (JComponent botao : botoes) {
            acoes.add(botao);
        }
        cabecalho.add(acoes, BorderLayout.EAST);
        return cabecalho;
    }

    /** Cabeçalho de seção do cardápio: "Empadas Salgadas ........ 5 produtos". */
    public static JPanel cabecalhoSecao(String titulo, String informacao, float tamanhoTitulo) {
        JPanel painel = Tema.painelTransparente(new BorderLayout(14, 0));
        JLabel rotulo = Tema.tituloSecao(titulo);
        rotulo.setFont(Tema.fonteCardapio(Font.BOLD, tamanhoTitulo));
        painel.add(rotulo, BorderLayout.WEST);
        painel.add(new LinhaPontilhada(), BorderLayout.CENTER);
        if (informacao != null) {
            painel.add(Tema.texto(informacao, Font.PLAIN, 13, Tema.TEXTO_SUAVE), BorderLayout.EAST);
        }
        return painel;
    }

    /** Selo arredondado de status (Pendente, Entregue, Cancelado, Inativo). */
    public static JLabel selo(String texto, Color fundo, Color frente) {
        JLabel selo = new JLabel(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        selo.setFont(Tema.fonte(Font.BOLD, 11.5f));
        selo.setBackground(fundo);
        selo.setForeground(frente);
        selo.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return selo;
    }

    /** Tabela somente leitura no estilo do sistema. */
    public static JTable tabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setFillsViewportHeight(true);
        tabela.setBackground(Tema.BRANCO);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setFont(Tema.fonte(Font.BOLD, 12));
        // Títulos das colunas alinhados à esquerda, como o conteúdo
        javax.swing.table.TableCellRenderer padrao = tabela.getTableHeader().getDefaultRenderer();
        tabela.getTableHeader().setDefaultRenderer((t, valor, selecionado, foco, linha, coluna) -> {
            Component componente = padrao.getTableCellRendererComponent(t, valor, selecionado, foco, linha, coluna);
            if (componente instanceof JLabel rotulo) {
                rotulo.setHorizontalAlignment(JLabel.LEFT);
            }
            return componente;
        });
        return tabela;
    }

    /** Modelo de tabela em que o usuário não consegue editar as células. */
    public static DefaultTableModel modeloSomenteLeitura(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    public static void largurasColunas(JTable tabela, int... larguras) {
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
            if (larguras[i] <= 90) {
                tabela.getColumnModel().getColumn(i).setMaxWidth(larguras[i] + 30);
            }
        }
    }

    /** Coloca a tabela dentro de um cartão com rolagem. */
    public static JComponent tabelaEmCartao(JTable tabela) {
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getViewport().setBackground(Tema.BRANCO);
        PainelArredondado cartao = cartao(new BorderLayout());
        cartao.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
        cartao.add(rolagem, BorderLayout.CENTER);
        return cartao;
    }

    /** Executa a ação ao dar duplo clique em uma linha da tabela. */
    public static void aoDuploClique(JTable tabela, Runnable acao) {
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                    acao.run();
                }
            }
        });
    }

    /** Linha pontilhada usada nos cabeçalhos do cardápio. */
    static class LinhaPontilhada extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Tema.BORDA_CAMPO);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{1, 5}, 0));
            int y = getHeight() / 2 + 2;
            g2.drawLine(0, y, getWidth(), y);
            g2.dispose();
        }
    }
}
