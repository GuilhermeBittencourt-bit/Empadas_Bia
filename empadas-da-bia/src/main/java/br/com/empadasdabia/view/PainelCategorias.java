package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.CategoriaController;
import br.com.empadasdabia.model.Categoria;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.Componentes;
import br.com.empadasdabia.view.componentes.PainelArredondado;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD de categorias em uma única tela: formulário em cima e lista embaixo.
 * Não tem janela separada porque a categoria possui apenas dois campos.
 */
public class PainelCategorias extends JPanel implements Atualizavel {

    private final CategoriaController controller = new CategoriaController();

    private final JLabel tituloFormulario = new JLabel();
    private final JTextField nome = Tema.campoTexto();
    private final JTextField descricao = Tema.campoTexto();
    private final DefaultTableModel modelo =
            Componentes.modeloSomenteLeitura("ID", "Nome", "Descrição", "Produtos");
    private final JTable tabela = Componentes.tabela(modelo);
    private List<Categoria> categorias = new ArrayList<>();
    private Categoria emEdicao;

    public PainelCategorias() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(26, 32, 26, 32));

        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 18));
        topo.add(Componentes.cabecalhoTela("Categorias",
                "Organize os produtos em grupos. Clique em uma linha para editá-la."), BorderLayout.NORTH);
        topo.add(criarFormulario(), BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        Componentes.largurasColunas(tabela, 70, 260, 420, 100);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                editar(categorias.get(tabela.getSelectedRow()));
            }
        });
        add(Componentes.tabelaEmCartao(tabela), BorderLayout.CENTER);
        add(Tema.subtitulo("Categorias com produtos vinculados não podem ser excluídas."), BorderLayout.SOUTH);
    }

    private JPanel criarFormulario() {
        PainelArredondado cartao = Componentes.cartao(new BorderLayout(0, 14));
        cartao.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        tituloFormulario.setFont(Tema.fonteCardapio(Font.BOLD, 18));
        cartao.add(tituloFormulario, BorderLayout.NORTH);

        JPanel campos = Tema.painelTransparente(new BorderLayout(14, 0));
        JPanel campoNome = Tema.comRotulo("Nome *", nome);
        campoNome.setPreferredSize(new Dimension(300, 64));
        campos.add(campoNome, BorderLayout.WEST);
        campos.add(Tema.comRotulo("Descrição", descricao), BorderLayout.CENTER);
        cartao.add(campos, BorderLayout.CENTER);

        JButton salvar = Tema.botaoPrimario("Salvar");
        JButton novo = Tema.botaoSecundario("+  Novo");
        JButton excluir = Tema.botaoPerigo("Excluir");
        salvar.addActionListener(e -> salvar());
        novo.addActionListener(e -> limpar());
        excluir.addActionListener(e -> excluir());
        JPanel botoes = Tema.painelTransparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        botoes.add(salvar);
        botoes.add(javax.swing.Box.createHorizontalStrut(10));
        botoes.add(novo);
        botoes.add(javax.swing.Box.createHorizontalStrut(10));
        botoes.add(excluir);
        cartao.add(botoes, BorderLayout.SOUTH);
        return cartao;
    }

    @Override
    public void atualizar() {
        Mensagens.tentar(this, () -> {
            categorias = controller.listar();
            modelo.setRowCount(0);
            for (Categoria categoria : categorias) {
                modelo.addRow(new Object[]{categoria.getId(), categoria.getNome(),
                        Formatador.textoOuTraco(categoria.getDescricao()), categoria.getQuantidadeProdutos()});
            }
            limpar();
        });
    }

    private void editar(Categoria categoria) {
        emEdicao = categoria;
        tituloFormulario.setText("Editando: " + categoria.getNome() + "  (ID " + categoria.getId() + ")");
        nome.setText(categoria.getNome());
        descricao.setText(categoria.getDescricao());
    }

    private void limpar() {
        emEdicao = null;
        tituloFormulario.setText("Nova categoria");
        nome.setText("");
        descricao.setText("");
        tabela.clearSelection();
        nome.requestFocusInWindow();
    }

    private void salvar() {
        Integer id = emEdicao == null ? null : emEdicao.getId();
        if (Mensagens.tentar(this, () -> controller.salvar(id, nome.getText(), descricao.getText()))) {
            Mensagens.info(this, "Categoria salva com sucesso.");
            atualizar();
        }
    }

    private void excluir() {
        if (emEdicao == null) {
            Mensagens.aviso(this, "Selecione uma categoria na lista.");
            return;
        }
        if (Mensagens.confirmar(this, "Excluir a categoria \"" + emEdicao.getNome() + "\"?", "Excluir categoria")
                && Mensagens.tentar(this, () -> controller.excluir(emEdicao))) {
            atualizar();
        }
    }
}
