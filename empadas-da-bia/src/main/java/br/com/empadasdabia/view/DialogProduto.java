package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.ProdutoController;
import br.com.empadasdabia.model.Categoria;
import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Imagens;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.PainelArredondado;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/** Formulário de cadastro e edição de produto (com foto). */
public class DialogProduto extends JDialog {

    private final ProdutoController controller = new ProdutoController();
    private final Produto produtoOriginal;

    private final JTextField nome = Tema.campoTexto();
    private final JTextArea descricao = new JTextArea(3, 20);
    private final JTextField preco = Tema.campoTexto();
    private final JComboBox<Categoria> categoria = new JComboBox<>();
    private final JCheckBox ativo = new JCheckBox("Produto ativo (aparece no cardápio de novos pedidos)", true);
    private final JLabel previa = new JLabel("", SwingConstants.CENTER);
    private final PainelArredondado quadroFoto =
            new PainelArredondado(new BorderLayout(), Tema.FUNDO_FOTO_SALGADA, Tema.BORDA, 16);

    private String imagem;
    private boolean salvo;
    private Produto produtoSalvo;

    public DialogProduto(Component pai, Produto produto, List<Categoria> categorias) {
        super(SwingUtilities.getWindowAncestor(pai), produto == null ? "Novo Produto" : "Editar Produto",
                ModalityType.APPLICATION_MODAL);
        this.produtoOriginal = produto;
        categorias.forEach(categoria::addItem);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBorder(BorderFactory.createEmptyBorder(22, 28, 20, 28));
        JPanel textos = Tema.painelTransparente(new GridLayout(2, 1, 0, 2));
        JLabel titulo = Tema.titulo(produto == null ? "Novo Produto" : "Editar Produto");
        titulo.setFont(Tema.fonteCardapio(Font.BOLD, 26));
        textos.add(titulo);
        textos.add(Tema.subtitulo("Campos com * são obrigatórios"));
        raiz.add(textos, BorderLayout.NORTH);

        JPanel centro = Tema.painelTransparente(new BorderLayout(22, 0));
        centro.add(criarAreaFoto(), BorderLayout.WEST);
        centro.add(criarFormulario(), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(criarBotoes(), BorderLayout.SOUTH);
        setContentPane(raiz);

        preencher(produto);
        setSize(780, 560);
        setResizable(false);
        setLocationRelativeTo(pai);
    }

    public boolean foiSalvo() {
        return salvo;
    }

    public Produto getProdutoSalvo() {
        return produtoSalvo;
    }

    private JPanel criarAreaFoto() {
        JPanel area = Tema.painelTransparente(null);
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
        area.setPreferredSize(new Dimension(220, 0));

        JLabel rotulo = Tema.texto("Foto do produto", Font.BOLD, 13, Tema.TEXTO);
        quadroFoto.setPreferredSize(new Dimension(220, 200));
        quadroFoto.setMaximumSize(new Dimension(220, 200));
        quadroFoto.add(previa, BorderLayout.CENTER);

        JButton escolher = Tema.botaoSecundario("Escolher imagem…");
        escolher.setForeground(Tema.AZUL);
        escolher.addActionListener(e -> escolherImagem());
        escolher.setMaximumSize(new Dimension(220, 40));

        JLabel dica = new JLabel("<html><div style='width:200px'>PNG ou JPG. Sem imagem, o cardápio mostra uma ilustração padrão.</div></html>");
        dica.setFont(Tema.fonte(Font.PLAIN, 12));
        dica.setForeground(Tema.TEXTO_SUAVE);

        for (Component c : new Component[]{rotulo, quadroFoto, escolher, dica}) {
            ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        area.add(rotulo);
        area.add(Box.createVerticalStrut(8));
        area.add(quadroFoto);
        area.add(Box.createVerticalStrut(10));
        area.add(escolher);
        area.add(Box.createVerticalStrut(8));
        area.add(dica);
        return area;
    }

    private JPanel criarFormulario() {
        JPanel formulario = Tema.painelTransparente(null);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));

        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);
        descricao.setFont(Tema.fonte(Font.PLAIN, 14));
        descricao.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane rolagemDescricao = new JScrollPane(descricao);
        rolagemDescricao.setPreferredSize(new Dimension(300, 80));

        JPanel precoECategoria = Tema.painelTransparente(new GridLayout(1, 2, 14, 0));
        precoECategoria.add(Tema.comRotulo("Preço (R$) *", preco));
        precoECategoria.add(Tema.comRotulo("Categoria *", categoria));
        categoria.addActionListener(e -> atualizarFundoFoto());

        ativo.setOpaque(false);
        ativo.setFont(Tema.fonte(Font.PLAIN, 14));

        JComponent[] linhas = {
                Tema.comRotulo("Nome *", nome),
                Tema.comRotulo("Descrição (aparece no cardápio)", rolagemDescricao),
                precoECategoria,
                ativo};
        for (JComponent linha : linhas) {
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);
            linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
            formulario.add(linha);
            formulario.add(Box.createVerticalStrut(14));
        }
        return formulario;
    }

    private JPanel criarBotoes() {
        JButton cancelar = Tema.botaoSecundario("Cancelar");
        JButton salvar = Tema.botaoPrimario("Salvar");
        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> salvar());
        getRootPane().setDefaultButton(salvar);

        JPanel botoes = Tema.painelTransparente(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDA),
                BorderFactory.createEmptyBorder(14, 0, 0, 0)));
        botoes.add(cancelar);
        botoes.add(salvar);
        return botoes;
    }

    private void preencher(Produto produto) {
        if (produto != null) {
            nome.setText(produto.getNome());
            descricao.setText(produto.getDescricao());
            preco.setText(Formatador.numeroDecimal(produto.getPreco()));
            categoria.setSelectedItem(produto.getCategoria());
            ativo.setSelected(produto.isAtivo());
            imagem = produto.getImagem();
        }
        atualizarPrevia();
    }

    private void escolherImagem() {
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Escolher foto do produto");
        seletor.setFileFilter(new FileNameExtensionFilter("Imagens (PNG, JPG)", "png", "jpg", "jpeg"));
        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Mensagens.tentar(this, () -> {
                imagem = Imagens.copiarParaPastaDoSistema(seletor.getSelectedFile());
                atualizarPrevia();
            });
        }
    }

    private void atualizarPrevia() {
        previa.setIcon(Imagens.carregar(imagem, 200, 170));
        atualizarFundoFoto();
    }

    private void atualizarFundoFoto() {
        Categoria selecionada = (Categoria) categoria.getSelectedItem();
        quadroFoto.setFundo(Tema.fundoDaFoto(selecionada == null ? null : selecionada.getNome()));
    }

    private void salvar() {
        Integer id = produtoOriginal == null ? null : produtoOriginal.getId();
        boolean ok = Mensagens.tentar(this, () -> produtoSalvo = controller.salvar(id, nome.getText(),
                descricao.getText(), preco.getText(), (Categoria) categoria.getSelectedItem(),
                ativo.isSelected(), imagem));
        if (ok) {
            salvo = true;
            dispose();
        }
    }
}
