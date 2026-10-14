package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.ClienteController;
import br.com.empadasdabia.controller.PedidoController;
import br.com.empadasdabia.controller.ProdutoController;
import br.com.empadasdabia.model.Categoria;
import br.com.empadasdabia.model.Cliente;
import br.com.empadasdabia.model.ItemPedido;
import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Imagens;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.CardCardapio;
import br.com.empadasdabia.view.componentes.Componentes;
import br.com.empadasdabia.view.componentes.PainelRolavel;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Novo Pedido: à esquerda o cardápio (clique em + para adicionar),
 * à direita a comanda com cliente, itens, quantidades e total.
 */
public class DialogNovoPedido extends JDialog {

    private final PedidoController pedidoController = new PedidoController();
    private final ProdutoController produtoController = new ProdutoController();
    private final ClienteController clienteController = new ClienteController();

    /** O pedido em montagem: as regras (somar itens, calcular total) ficam no Model. */
    private final Pedido pedido = new Pedido();
    private Pedido pedidoSalvo;

    private final JTextField busca = Tema.campoBusca("Buscar sabor");
    private final JPanel chips = Tema.painelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final JPanel cardapio = Tema.painelTransparente(null);
    private final List<CardCardapio> cartoes = new ArrayList<>();
    private Categoria categoriaFiltro;

    private final JComboBox<Cliente> cliente = new JComboBox<>();
    private final JPanel listaItens = Tema.painelTransparente(null);
    private final JTextField observacao = Tema.campoTexto();
    private final JLabel quantidadeItens = Tema.texto(" ", Font.PLAIN, 13, Tema.TEXTO_SUAVE);
    private final JLabel total = Tema.preco("R$ 0,00", 32);

    public DialogNovoPedido(JFrame dono) {
        super(dono, "Novo Pedido", true);
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(criarAreaCardapio(), BorderLayout.CENTER);
        raiz.add(criarComanda(), BorderLayout.EAST);
        setContentPane(raiz);

        Mensagens.tentar(this, () -> {
            clienteController.listar("").forEach(cliente::addItem);
            cliente.setSelectedItem(null);
            montarChips(produtoController.listarCategorias());
            carregarCardapio();
        });
        atualizarComanda();

        setSize(1240, 780);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(dono);
    }

    /** Pedido gravado no banco, ou null se o usuário cancelou. */
    public Pedido getPedidoSalvo() {
        return pedidoSalvo;
    }

    // ------------------------------------------------------------- cardápio

    private JPanel criarAreaCardapio() {
        JPanel area = new JPanel(new BorderLayout(0, 14));
        area.setBorder(BorderFactory.createEmptyBorder(20, 22, 16, 22));

        JPanel cabecalho = Tema.painelTransparente(new BorderLayout(12, 0));
        JLabel titulo = Tema.titulo("Escolha as empadas");
        titulo.setFont(Tema.fonteCardapio(Font.BOLD, 26));
        cabecalho.add(titulo, BorderLayout.WEST);
        busca.setPreferredSize(new Dimension(240, 38));
        cabecalho.add(busca, BorderLayout.EAST);
        busca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarPor(categoriaFiltro); }
            public void removeUpdate(DocumentEvent e) { filtrarPor(categoriaFiltro); }
            public void changedUpdate(DocumentEvent e) { filtrarPor(categoriaFiltro); }
        });

        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 12));
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(chips, BorderLayout.CENTER);
        area.add(topo, BorderLayout.NORTH);

        cardapio.setLayout(new BoxLayout(cardapio, BoxLayout.Y_AXIS));
        PainelRolavel alinhadoNoTopo = new PainelRolavel(new BorderLayout());
        alinhadoNoTopo.add(cardapio, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(alinhadoNoTopo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getViewport().setOpaque(false);
        rolagem.setOpaque(false);
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        area.add(rolagem, BorderLayout.CENTER);
        area.add(Tema.texto("Produtos inativos não aparecem aqui. Clique em + para adicionar uma unidade à comanda.",
                Font.PLAIN, 12.5f, Tema.TEXTO_SUAVE), BorderLayout.SOUTH);
        return area;
    }

    private void montarChips(List<Categoria> categorias) {
        ButtonGroup grupo = new ButtonGroup();
        JToggleButton todas = Tema.chip("Todas");
        todas.setSelected(true);
        todas.addActionListener(e -> filtrarPor(null));
        grupo.add(todas);
        chips.add(todas);
        for (Categoria categoria : categorias) {
            JToggleButton chip = Tema.chip(categoria.getNome());
            chip.addActionListener(e -> filtrarPor(categoria));
            grupo.add(chip);
            chips.add(chip);
        }
    }

    private void filtrarPor(Categoria categoria) {
        categoriaFiltro = categoria;
        Mensagens.tentar(this, this::carregarCardapio);
    }

    private void carregarCardapio() {
        List<Produto> produtos = produtoController.listarCardapio(busca.getText(), categoriaFiltro);
        Map<String, List<Produto>> porCategoria = new LinkedHashMap<>();
        for (Produto produto : produtos) {
            porCategoria.computeIfAbsent(produto.getCategoria().getNome(), k -> new ArrayList<>()).add(produto);
        }
        cardapio.removeAll();
        cartoes.clear();
        if (produtos.isEmpty()) {
            cardapio.add(Tema.subtitulo("Nenhum produto encontrado."));
        }
        for (Map.Entry<String, List<Produto>> secao : porCategoria.entrySet()) {
            JPanel cabecalho = Componentes.cabecalhoSecao(secao.getKey(), null, 18);
            cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
            cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            cardapio.add(cabecalho);
            cardapio.add(Box.createVerticalStrut(10));

            JPanel grade = Tema.painelTransparente(new GridLayout(0, 3, 12, 12));
            grade.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (Produto produto : secao.getValue()) {
                CardCardapio cartao = new CardCardapio(produto, () -> adicionar(produto));
                cartao.atualizarQuantidade(pedido.quantidadeDoProduto(produto));
                cartoes.add(cartao);
                grade.add(cartao);
            }
            cardapio.add(grade);
            cardapio.add(Box.createVerticalStrut(18));
        }
        cardapio.revalidate();
        cardapio.repaint();
    }

    // ------------------------------------------------------------- comanda

    private JPanel criarComanda() {
        JPanel comanda = new JPanel(new BorderLayout(0, 12));
        comanda.setBackground(Tema.BRANCO);
        comanda.setPreferredSize(new Dimension(390, 0));
        comanda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Tema.BORDA),
                BorderFactory.createEmptyBorder(20, 22, 18, 22)));

        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 12));
        JPanel titulo = Tema.painelTransparente(new BorderLayout());
        JLabel rotulo = new JLabel("Comanda");
        rotulo.setFont(Tema.fonteCardapio(Font.BOLD, 24));
        titulo.add(rotulo, BorderLayout.WEST);
        titulo.add(Tema.subtitulo("novo pedido"), BorderLayout.EAST);
        topo.add(titulo, BorderLayout.NORTH);
        cliente.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Selecione o cliente…");
        cliente.setMaximumRowCount(12);
        topo.add(Tema.comRotulo("Cliente *", cliente), BorderLayout.CENTER);
        comanda.add(topo, BorderLayout.NORTH);

        listaItens.setLayout(new BoxLayout(listaItens, BoxLayout.Y_AXIS));
        PainelRolavel alinhadoNoTopo = new PainelRolavel(new BorderLayout());
        alinhadoNoTopo.add(listaItens, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(alinhadoNoTopo);
        rolagem.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.LINHA_TABELA));
        rolagem.getViewport().setBackground(Tema.BRANCO);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        comanda.add(rolagem, BorderLayout.CENTER);

        comanda.add(criarRodapeComanda(), BorderLayout.SOUTH);
        return comanda;
    }

    private JPanel criarRodapeComanda() {
        JPanel rodape = Tema.painelTransparente(null);
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));

        JPanel obs = Tema.comRotulo("Observação", observacao);
        JPanel resumo = Tema.painelTransparente(new BorderLayout());
        resumo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Tema.BORDA),
                BorderFactory.createEmptyBorder(10, 0, 4, 0)));
        JPanel linhaItens = Tema.painelTransparente(new BorderLayout());
        linhaItens.add(Tema.texto("Itens", Font.PLAIN, 13, Tema.TEXTO_SUAVE), BorderLayout.WEST);
        linhaItens.add(quantidadeItens, BorderLayout.EAST);
        JPanel linhaTotal = Tema.painelTransparente(new BorderLayout());
        linhaTotal.add(Tema.texto("TOTAL", Font.BOLD, 14, new Color(0x4A443D)), BorderLayout.WEST);
        linhaTotal.add(total, BorderLayout.EAST);
        resumo.add(linhaItens, BorderLayout.NORTH);
        resumo.add(linhaTotal, BorderLayout.CENTER);

        JButton confirmar = Tema.botaoDestaque("Revisar e Confirmar");
        confirmar.setFont(Tema.fonte(Font.BOLD, 15));
        confirmar.addActionListener(e -> revisarEConfirmar());
        JButton cancelar = Tema.botaoSecundario("Cancelar");
        cancelar.addActionListener(e -> cancelar());

        for (JPanel bloco : new JPanel[]{obs, resumo}) {
            bloco.setAlignmentX(Component.LEFT_ALIGNMENT);
            bloco.setMaximumSize(new Dimension(Integer.MAX_VALUE, bloco.getPreferredSize().height + 10));
            rodape.add(bloco);
            rodape.add(Box.createVerticalStrut(10));
        }
        for (JButton botao : new JButton[]{confirmar, cancelar}) {
            botao.setAlignmentX(Component.LEFT_ALIGNMENT);
            botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            rodape.add(botao);
            rodape.add(Box.createVerticalStrut(8));
        }
        return rodape;
    }

    private void adicionar(Produto produto) {
        Mensagens.tentar(this, () -> pedido.adicionarItem(produto, 1));
        atualizarComanda();
    }

    /** Redesenha a lista de itens, os totais e as marcações do cardápio. */
    private void atualizarComanda() {
        listaItens.removeAll();
        if (pedido.getItens().isEmpty()) {
            JLabel vazio = Tema.subtitulo("<html><div style='width:280px; padding-top:14px'>"
                    + "A comanda está vazia. Clique em <b>+</b> nos produtos do cardápio para adicioná-los.</div></html>");
            listaItens.add(vazio);
        }
        List<ItemPedido> itens = pedido.getItens();
        for (int i = 0; i < itens.size(); i++) {
            listaItens.add(criarLinhaItem(itens.get(i), i));
        }
        quantidadeItens.setText(pedido.getQuantidadeTotalItens() + " unidade(s)");
        total.setText(Formatador.moeda(pedido.calcularTotal()));
        for (CardCardapio cartao : cartoes) {
            cartao.atualizarQuantidade(pedido.quantidadeDoProduto(cartao.getProduto()));
        }
        listaItens.revalidate();
        listaItens.repaint();
    }

    private JPanel criarLinhaItem(ItemPedido item, int indice) {
        JPanel linha = Tema.painelTransparente(new BorderLayout(10, 0));
        linha.setBorder(BorderFactory.createEmptyBorder(12, 0, 2, 0));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.add(new JLabel(Imagens.carregar(item.getProduto().getImagem(), 48, 41)), BorderLayout.WEST);

        JPanel conteudo = Tema.painelTransparente(new BorderLayout(0, 6));
        JPanel nomeESubtotal = Tema.painelTransparente(new BorderLayout(8, 0));
        nomeESubtotal.add(Tema.texto(item.getProduto().getNome(), Font.BOLD, 13.5f, Tema.TEXTO), BorderLayout.CENTER);
        nomeESubtotal.add(Tema.texto(Formatador.moeda(item.getSubtotal()), Font.BOLD, 13.5f, Tema.TEXTO), BorderLayout.EAST);
        conteudo.add(nomeESubtotal, BorderLayout.NORTH);

        JPanel controles = Tema.painelTransparente(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton menos = botaoQuantidade("−", "Diminuir quantidade");
        JButton mais = botaoQuantidade("+", "Aumentar quantidade");
        menos.addActionListener(e -> mudarQuantidade(indice, item.getQuantidade() - 1));
        mais.addActionListener(e -> mudarQuantidade(indice, item.getQuantidade() + 1));
        JLabel quantidade = Tema.texto(String.valueOf(item.getQuantidade()), Font.BOLD, 14, Tema.TEXTO);
        quantidade.setPreferredSize(new Dimension(22, 28));
        quantidade.setHorizontalAlignment(JLabel.CENTER);
        controles.add(menos);
        controles.add(quantidade);
        controles.add(mais);
        controles.add(Tema.texto("× " + Formatador.moeda(item.getPrecoUnitario()), Font.PLAIN, 12, Tema.TEXTO_SUAVE));

        JButton remover = Tema.botaoLink("Remover");
        remover.setForeground(new Color(0x9B2C2C));
        remover.setFont(Tema.fonte(Font.BOLD, 12.5f));
        remover.addActionListener(e -> mudarQuantidade(indice, 0));
        JPanel linhaControles = Tema.painelTransparente(new BorderLayout());
        linhaControles.add(controles, BorderLayout.WEST);
        linhaControles.add(remover, BorderLayout.EAST);
        conteudo.add(linhaControles, BorderLayout.CENTER);

        linha.add(conteudo, BorderLayout.CENTER);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
        return linha;
    }

    private JButton botaoQuantidade(String texto, String descricao) {
        JButton botao = Tema.botaoSecundario(texto);
        botao.setToolTipText(descricao);
        botao.setMargin(new java.awt.Insets(2, 8, 2, 8));
        botao.setPreferredSize(new Dimension(32, 30));
        return botao;
    }

    private void mudarQuantidade(int indice, int novaQuantidade) {
        pedido.alterarQuantidade(indice, novaQuantidade);
        atualizarComanda();
    }

    // ------------------------------------------------------------- finalização

    /** Mostra o resumo para revisão e, se confirmado, grava o pedido. */
    private void revisarEConfirmar() {
        Cliente selecionado = (Cliente) cliente.getSelectedItem();
        if (!Mensagens.tentar(this, () -> pedidoController.validar(pedido, selecionado))) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, montarResumo(selecionado), "Revisar pedido",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resposta != JOptionPane.OK_OPTION) {
            return;
        }
        if (Mensagens.tentar(this, () -> pedidoController.salvar(pedido, selecionado, observacao.getText()))) {
            pedidoSalvo = pedido;
            Mensagens.info(this, "Pedido " + Formatador.numeroPedido(pedido.getId()) + " registrado com sucesso!");
            dispose();
        }
    }

    private String montarResumo(Cliente selecionado) {
        StringBuilder html = new StringBuilder("<html><body style='width:380px'>");
        html.append("<p style='font-size:13px'><b>Cliente:</b> ").append(selecionado.getNome()).append("<br>")
                .append(selecionado.getEnderecoCompleto()).append("</p><br>");
        html.append("<table width='100%' cellspacing='0' cellpadding='3'>");
        for (ItemPedido item : pedido.getItens()) {
            html.append("<tr><td>").append(item.getQuantidade()).append("x ").append(item.getProduto().getNome())
                    .append("</td><td align='right'>").append(Formatador.moeda(item.getSubtotal())).append("</td></tr>");
        }
        html.append("</table><hr>");
        if (!observacao.getText().isBlank()) {
            html.append("<p><b>Observação:</b> ").append(observacao.getText().trim()).append("</p>");
        }
        html.append("<p style='font-size:15px'><b>TOTAL: <font color='#C2416B'>")
                .append(Formatador.moeda(pedido.calcularTotal())).append("</font></b></p><br>")
                .append("<p>Confirmar e salvar o pedido?</p></body></html>");
        return html.toString();
    }

    private void cancelar() {
        if (pedido.getItens().isEmpty()
                || Mensagens.confirmar(this, "Descartar este pedido? Os itens da comanda serão perdidos.", "Cancelar pedido")) {
            dispose();
        }
    }
}
