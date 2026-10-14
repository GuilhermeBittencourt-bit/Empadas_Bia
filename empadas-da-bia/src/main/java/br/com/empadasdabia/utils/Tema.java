package br.com.empadasdabia.utils;

import br.com.empadasdabia.model.StatusPedido;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

/**
 * Identidade visual do sistema: cores, fontes e "fábricas" de componentes padronizados.
 * Centralizar aqui evita repetir estilo em todas as telas.
 */
public final class Tema {

    // Cores principais
    public static final Color FUNDO = new Color(0xFFF8EC);
    public static final Color BRANCO = Color.WHITE;
    public static final Color BORDA = new Color(0xEADBC8);
    public static final Color BORDA_CAMPO = new Color(0xD9CBB4);
    public static final Color AZUL = new Color(0x2F5D8A);
    public static final Color AZUL_ESCURO = new Color(0x1F4468);
    public static final Color AZUL_CLARO = new Color(0xEAF1F8);
    public static final Color ROSA = new Color(0xC2416B);
    public static final Color ROSA_ESCURO = new Color(0x9E3157);
    public static final Color TEXTO = new Color(0x2E2A26);
    public static final Color TEXTO_SUAVE = new Color(0x6B6259);
    public static final Color CABECALHO_TABELA = new Color(0xFBF4E6);
    public static final Color LINHA_TABELA = new Color(0xF1E7D6);

    // Fundo das fotos por categoria (creme para salgadas, rosado para doces, azulado para combos)
    public static final Color FUNDO_FOTO_SALGADA = new Color(0xFFF3E0);
    public static final Color FUNDO_FOTO_DOCE = new Color(0xFCE8E6);
    public static final Color FUNDO_FOTO_COMBO = new Color(0xE8F0F7);

    private static final String SERIFADA = escolherFonte("Georgia", "Serif");
    private static final String SEM_SERIFA = escolherFonte("Segoe UI", "SansSerif");

    private Tema() {
    }

    /** Aplica o FlatLaf e os ajustes globais. Deve ser chamado antes de abrir a primeira tela. */
    public static void aplicar() {
        FlatLightLaf.setup();
        UIManager.put("defaultFont", fonte(Font.PLAIN, 14));
        UIManager.put("Panel.background", FUNDO);
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.borderColor", BORDA_CAMPO);
        UIManager.put("Component.focusedBorderColor", AZUL);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("Table.rowHeight", 40);
        UIManager.put("Table.cellMargins", new Insets(4, 14, 4, 14));
        UIManager.put("Table.selectionBackground", AZUL_CLARO);
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("Table.selectionInactiveBackground", AZUL_CLARO);
        UIManager.put("Table.selectionInactiveForeground", TEXTO);
        UIManager.put("Table.gridColor", LINHA_TABELA);
        UIManager.put("TableHeader.background", CABECALHO_TABELA);
        UIManager.put("TableHeader.foreground", TEXTO_SUAVE);
        UIManager.put("TableHeader.cellMargins", new Insets(9, 14, 9, 14));
        UIManager.put("TableHeader.separatorColor", CABECALHO_TABELA);
        UIManager.put("TableHeader.bottomSeparatorColor", BORDA);
        UIManager.put("OptionPane.background", BRANCO);
        UIManager.put("OptionPane.messageFont", fonte(Font.PLAIN, 14));
    }

    // ---------------------------------------------------------------- fontes

    public static Font fonte(int estilo, float tamanho) {
        return new Font(SEM_SERIFA, estilo, Math.round(tamanho)).deriveFont(tamanho);
    }

    /** Fonte serifada, usada em títulos, nomes de produtos e preços (estilo cardápio). */
    public static Font fonteCardapio(int estilo, float tamanho) {
        return new Font(SERIFADA, estilo, Math.round(tamanho)).deriveFont(tamanho);
    }

    private static String escolherFonte(String preferida, String alternativa) {
        String[] instaladas = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        List<String> lista = Arrays.asList(instaladas);
        return lista.contains(preferida) ? preferida : alternativa;
    }

    // ---------------------------------------------------------------- textos

    public static JLabel titulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteCardapio(Font.BOLD, 30));
        rotulo.setForeground(TEXTO);
        return rotulo;
    }

    public static JLabel subtitulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonte(Font.PLAIN, 14));
        rotulo.setForeground(TEXTO_SUAVE);
        return rotulo;
    }

    public static JLabel tituloSecao(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteCardapio(Font.BOLD, 20));
        rotulo.setForeground(AZUL);
        return rotulo;
    }

    public static JLabel texto(String texto, int estilo, float tamanho, Color cor) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonte(estilo, tamanho));
        rotulo.setForeground(cor);
        return rotulo;
    }

    public static JLabel preco(String texto, float tamanho) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteCardapio(Font.BOLD, tamanho));
        rotulo.setForeground(ROSA);
        return rotulo;
    }

    // ---------------------------------------------------------------- botões

    /** Ação principal da tela (azul). */
    public static JButton botaoPrimario(String texto) {
        return botao(texto, AZUL, BRANCO, AZUL, "#264E75", "#1F4468");
    }

    /** Ações ligadas a pedidos (rosa). */
    public static JButton botaoDestaque(String texto) {
        return botao(texto, ROSA, BRANCO, ROSA, "#AD3A60", "#9E3157");
    }

    public static JButton botaoSecundario(String texto) {
        return botao(texto, BRANCO, TEXTO, BORDA_CAMPO, "#FBF4E6", "#F3EEE6");
    }

    public static JButton botaoPerigo(String texto) {
        return botao(texto, BRANCO, new Color(0x9B2C2C), new Color(0xE2B4B4), "#FDF1F1", "#F6DCDC");
    }

    /** Botão com aparência de link (ex.: "Ver todos"). */
    public static JButton botaoLink(String texto) {
        JButton botao = new JButton(texto);
        botao.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        botao.setForeground(AZUL);
        botao.setFont(fonte(Font.BOLD, 14));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private static JButton botao(String texto, Color fundo, Color frente, Color borda, String hover, String pressionado) {
        JButton botao = new JButton(texto);
        botao.setFont(fonte(Font.BOLD, 14));
        botao.setBackground(fundo);
        botao.setForeground(frente);
        botao.setMargin(new Insets(9, 18, 9, 18));
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.putClientProperty(FlatClientProperties.STYLE,
                "borderColor:" + hex(borda) + "; hoverBackground:" + hover
                        + "; pressedBackground:" + pressionado + "; focusWidth:0; innerFocusWidth:0");
        return botao;
    }

    /** Botão arredondado de filtro (ex.: "Todas", "Empadas Doces"). */
    public static JToggleButton chip(String texto) {
        JToggleButton chip = new JToggleButton(texto);
        chip.setFont(fonte(Font.BOLD, 13));
        chip.setFocusPainted(false);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.putClientProperty(FlatClientProperties.STYLE,
                "arc:999; margin:6,16,6,16; background:#FFFFFF; borderColor:" + hex(BORDA_CAMPO)
                        + "; selectedBackground:" + hex(AZUL) + "; selectedForeground:#FFFFFF; focusWidth:0");
        return chip;
    }

    // ---------------------------------------------------------------- campos

    public static JTextField campoTexto() {
        JTextField campo = new JTextField();
        campo.putClientProperty(FlatClientProperties.STYLE, "margin:7,10,7,10");
        return campo;
    }

    public static JTextField campoBusca(String dica) {
        JTextField campo = campoTexto();
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, dica);
        campo.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        return campo;
    }

    /** Coloca um rótulo em negrito acima do campo (padrão de todos os formulários). */
    public static JPanel comRotulo(String rotulo, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setOpaque(false);
        JLabel label = texto(rotulo, Font.BOLD, 13, new Color(0x4A443D));
        painel.add(label, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    /** Painel transparente com espaçamento interno, usado para montar as telas. */
    public static JPanel painelTransparente(java.awt.LayoutManager layout) {
        JPanel painel = new JPanel(layout);
        painel.setOpaque(false);
        return painel;
    }

    public static void espacamento(JComponent componente, int cima, int esquerda, int baixo, int direita) {
        componente.setBorder(BorderFactory.createEmptyBorder(cima, esquerda, baixo, direita));
    }

    // ---------------------------------------------------------------- status

    /** Cores do "selo" de status: [0] fundo, [1] texto. */
    public static Color[] coresStatus(StatusPedido status) {
        return switch (status) {
            case PENDENTE -> new Color[]{new Color(0xFCEFD2), new Color(0x7A4F00)};
            case ENTREGUE -> new Color[]{new Color(0xDFF1E4), new Color(0x1E6B3A)};
            case CANCELADO -> new Color[]{new Color(0xF6DCDC), new Color(0x9B2C2C)};
        };
    }

    public static Color fundoDaFoto(String nomeCategoria) {
        if (nomeCategoria == null) {
            return FUNDO_FOTO_SALGADA;
        }
        String nome = nomeCategoria.toLowerCase();
        if (nome.contains("doce")) {
            return FUNDO_FOTO_DOCE;
        }
        if (nome.contains("combo")) {
            return FUNDO_FOTO_COMBO;
        }
        return FUNDO_FOTO_SALGADA;
    }

    private static String hex(Color cor) {
        return String.format("#%06X", cor.getRGB() & 0xFFFFFF);
    }
}
