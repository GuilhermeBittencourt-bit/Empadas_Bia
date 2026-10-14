package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.ClienteController;
import br.com.empadasdabia.model.Cliente;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/** Lista de clientes com busca, cadastro, edição e exclusão. */
public class PainelClientes extends JPanel implements Atualizavel {

    private final ClienteController controller = new ClienteController();

    private final JTextField busca = Tema.campoBusca("Digite o nome ou telefone");
    private final DefaultTableModel modelo =
            Componentes.modeloSomenteLeitura("ID", "Nome", "Telefone", "E-mail", "Bairro", "Cidade");
    private final JTable tabela = Componentes.tabela(modelo);
    private final JLabel resumo = Tema.subtitulo(" ");
    private List<Cliente> clientes = new ArrayList<>();

    public PainelClientes() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(26, 32, 26, 32));

        JButton novo = Tema.botaoPrimario("+  Novo Cliente");
        novo.addActionListener(e -> abrirFormulario(null));

        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 18));
        topo.add(Componentes.cabecalhoTela("Clientes", "Cadastro de clientes e endereços de entrega", novo),
                BorderLayout.NORTH);
        topo.add(Tema.comRotulo("Buscar cliente", busca), BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);
        busca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizar(); }
            public void removeUpdate(DocumentEvent e) { atualizar(); }
            public void changedUpdate(DocumentEvent e) { atualizar(); }
        });

        Componentes.largurasColunas(tabela, 60, 220, 150, 240, 140, 140);
        Componentes.aoDuploClique(tabela, this::editarSelecionado);
        add(Componentes.tabelaEmCartao(tabela), BorderLayout.CENTER);

        JButton editar = Tema.botaoSecundario("Editar");
        JButton excluir = Tema.botaoPerigo("Excluir");
        editar.addActionListener(e -> editarSelecionado());
        excluir.addActionListener(e -> excluir());
        JPanel botoes = Tema.painelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.add(editar);
        botoes.add(excluir);
        JPanel rodape = Tema.painelTransparente(new BorderLayout());
        rodape.add(resumo, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);
    }

    @Override
    public void atualizar() {
        Mensagens.tentar(this, () -> {
            clientes = controller.listar(busca.getText());
            modelo.setRowCount(0);
            for (Cliente cliente : clientes) {
                modelo.addRow(new Object[]{cliente.getId(), cliente.getNome(),
                        Formatador.telefone(cliente.getTelefone()), Formatador.textoOuTraco(cliente.getEmail()),
                        Formatador.textoOuTraco(cliente.getBairro()), Formatador.textoOuTraco(cliente.getCidade())});
            }
            resumo.setText(clientes.size() + " cliente(s) · clientes com pedidos não podem ser excluídos");
        });
    }

    private Cliente selecionado() {
        int linha = tabela.getSelectedRow();
        return linha < 0 ? null : clientes.get(linha);
    }

    private void editarSelecionado() {
        Cliente cliente = selecionado();
        if (cliente == null) {
            Mensagens.aviso(this, "Selecione um cliente na lista.");
            return;
        }
        abrirFormulario(cliente);
    }

    private void abrirFormulario(Cliente cliente) {
        DialogCliente dialogo = new DialogCliente(this, cliente);
        dialogo.setVisible(true);
        if (dialogo.foiSalvo()) {
            atualizar();
        }
    }

    private void excluir() {
        Cliente cliente = selecionado();
        if (cliente == null) {
            Mensagens.aviso(this, "Selecione um cliente na lista.");
            return;
        }
        if (Mensagens.confirmar(this, "Excluir o cliente \"" + cliente.getNome() + "\"?", "Excluir cliente")
                && Mensagens.tentar(this, () -> controller.excluir(cliente))) {
            atualizar();
        }
    }
}
