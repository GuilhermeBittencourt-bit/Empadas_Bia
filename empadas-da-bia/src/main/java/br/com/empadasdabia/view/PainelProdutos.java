package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.ProdutoController;
import br.com.empadasdabia.model.Categoria;
import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.CardProduto;
import br.com.empadasdabia.view.componentes.Componentes;
import br.com.empadasdabia.view.componentes.PainelRolavel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** "Nosso Cardápio": produtos em cartões com foto, agrupados por categoria. */
public class PainelProdutos extends JPanel implements Atualizavel {

    private static final int COLUNAS = 5;

    private final ProdutoController controller = new ProdutoController();

    private final JTextField busca = Tema.campoBusca("Buscar no cardápio");
    private final JPanel chips = Tema.painelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final JPanel listaSecoes = Tema.painelTransparente(null);
    private final List<CardProduto> cartoes = new ArrayList<>();
    private Categoria categoriaFiltro;
    private Produto selecionado;

    public PainelProdutos(TelaPrincipal telaPrincipal) {
        setLayout(new BorderLayout(0, 18));
        setBorder(BorderFactory.createEmptyBorder(26, 32, 10, 32));

        JButton editar = Tema.botaoSecundario("Editar");
        JButton ativar = Tema.botaoSecundario("Ativar/Desativar");
        JButton excluir = Tema.botaoPerigo("Excluir");
        JButton novo = Tema.botaoPrimario("+  Novo Produto");
        editar.addActionListener(e -> editarSelecionado());
        ativar.addActionListener(e -> alternarAtivo());
        excluir.addActionListener(e -> excluir());
        novo.addActionListener(e -> abrirFormulario(null));

        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 16));
        topo.add(Componentes.cabecalhoTela("Nosso Cardápio",
                "Clique em um produto para selecioná-lo · duplo clique para editar",
                editar, ativar, excluir, novo), BorderLayout.NORTH);
        JPanel filtros = Tema.painelTransparente(new BorderLayout(16, 0));
        filtros.add(chips, BorderLayout.CENTER);
        busca.setPreferredSize(new Dimension(280, 38));
        filtros.add(busca, BorderLayout.EAST);
        topo.add(filtros, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        busca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarPor(categoriaFiltro); }
            public void removeUpdate(DocumentEvent e) { filtrarPor(categoriaFiltro); }
            public void changedUpdate(DocumentEvent e) { filtrarPor(categoriaFiltro); }
        });

        listaSecoes.setLayout(new BoxLayout(listaSecoes, BoxLayout.Y_AXIS));
        PainelRolavel alinhadoNoTopo = new PainelRolavel(new BorderLayout());
        alinhadoNoTopo.add(listaSecoes, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(alinhadoNoTopo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getViewport().setOpaque(false);
        rolagem.setOpaque(false);
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        add(rolagem, BorderLayout.CENTER);
    }

    @Override
    public void atualizar() {
        Mensagens.tentar(this, () -> {
            montarChips(controller.listarCategorias());
            carregarProdutos();
        });
    }

    private void montarChips(List<Categoria> categorias) {
        chips.removeAll();
        ButtonGroup grupo = new ButtonGroup();
        JToggleButton todas = Tema.chip("Todas");
        todas.addActionListener(e -> filtrarPor(null));
        grupo.add(todas);
        chips.add(todas);
        boolean filtroAindaExiste = false;
        for (Categoria categoria : categorias) {
            JToggleButton chip = Tema.chip(categoria.getNome());
            chip.addActionListener(e -> filtrarPor(categoria));
            grupo.add(chip);
            chips.add(chip);
            if (categoria.equals(categoriaFiltro)) {
                chip.setSelected(true);
                filtroAindaExiste = true;
            }
        }
        if (!filtroAindaExiste) {
            categoriaFiltro = null;
            todas.setSelected(true);
        }
        chips.revalidate();
        chips.repaint();
    }

    private void filtrarPor(Categoria categoria) {
        categoriaFiltro = categoria;
        Mensagens.tentar(this, this::carregarProdutos);
    }

    /** Busca os produtos e monta uma seção (título + grade de cartões) para cada categoria. */
    private void carregarProdutos() {
        List<Produto> produtos = controller.listar(busca.getText(), categoriaFiltro);
        Map<String, List<Produto>> porCategoria = new LinkedHashMap<>();
        for (Produto produto : produtos) {
            porCategoria.computeIfAbsent(produto.getCategoria().getNome(), k -> new ArrayList<>()).add(produto);
        }

        listaSecoes.removeAll();
        cartoes.clear();
        if (produtos.isEmpty()) {
            listaSecoes.add(Tema.subtitulo("Nenhum produto encontrado."));
        }
        for (Map.Entry<String, List<Produto>> secao : porCategoria.entrySet()) {
            int quantidade = secao.getValue().size();
            JPanel cabecalho = Componentes.cabecalhoSecao(secao.getKey(),
                    quantidade + (quantidade == 1 ? " produto" : " produtos"), 22);
            cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
            cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            listaSecoes.add(cabecalho);
            listaSecoes.add(Box.createVerticalStrut(12));

            JPanel grade = Tema.painelTransparente(new GridLayout(0, COLUNAS, 16, 16));
            grade.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (Produto produto : secao.getValue()) {
                CardProduto cartao = new CardProduto(produto, this::selecionar, p -> editarSelecionado());
                cartao.setSelecionado(produto.equals(selecionado));
                cartoes.add(cartao);
                grade.add(cartao);
            }
            listaSecoes.add(grade);
            listaSecoes.add(Box.createVerticalStrut(26));
        }
        if (selecionado != null && produtos.stream().noneMatch(selecionado::equals)) {
            selecionado = null;
        }
        listaSecoes.revalidate();
        listaSecoes.repaint();
    }

    private void selecionar(Produto produto) {
        selecionado = produto;
        for (CardProduto cartao : cartoes) {
            cartao.setSelecionado(cartao.getProduto().equals(produto));
        }
    }

    private void editarSelecionado() {
        if (selecionado == null) {
            Mensagens.aviso(this, "Clique em um produto do cardápio para selecioná-lo.");
            return;
        }
        abrirFormulario(selecionado);
    }

    private void abrirFormulario(Produto produto) {
        Mensagens.tentar(this, () -> {
            DialogProduto dialogo = new DialogProduto(this, produto, controller.listarCategorias());
            dialogo.setVisible(true);
            if (dialogo.foiSalvo()) {
                selecionado = dialogo.getProdutoSalvo();
                atualizar();
            }
        });
    }

    private void alternarAtivo() {
        Mensagens.tentar(this, () -> {
            boolean ativo = controller.alternarAtivo(selecionado);
            Mensagens.info(this, "\"" + selecionado.getNome() + "\" agora está "
                    + (ativo ? "ATIVO e aparece no cardápio de pedidos." : "INATIVO e não aparece em novos pedidos."));
            carregarProdutos();
        });
    }

    private void excluir() {
        if (selecionado == null) {
            Mensagens.aviso(this, "Clique em um produto do cardápio para selecioná-lo.");
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir o produto \"" + selecionado.getNome() + "\"?", "Excluir produto")) {
            return;
        }
        Mensagens.tentar(this, () -> {
            controller.excluir(selecionado);
            selecionado = null;
            atualizar();
        });
    }
}
