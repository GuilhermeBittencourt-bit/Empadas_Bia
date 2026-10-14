package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.PedidoController;
import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.model.StatusPedido;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.Componentes;
import br.com.empadasdabia.view.componentes.RendererStatus;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/** Lista de pedidos com filtros por cliente e por status. */
public class PainelPedidos extends JPanel implements Atualizavel {

    private static final String TODOS = "Todos";

    private final TelaPrincipal telaPrincipal;
    private final PedidoController controller = new PedidoController();

    private final JTextField busca = Tema.campoBusca("Digite o nome do cliente");
    private final JComboBox<Object> filtroStatus = new JComboBox<>(new Object[]{
            TODOS, StatusPedido.PENDENTE, StatusPedido.ENTREGUE, StatusPedido.CANCELADO});
    private final DefaultTableModel modelo =
            Componentes.modeloSomenteLeitura("Nº", "Data", "Cliente", "Itens", "Total", "Status");
    private final JTable tabela = Componentes.tabela(modelo);
    private final JLabel resumo = Tema.subtitulo(" ");
    private List<Pedido> pedidos = new ArrayList<>();

    public PainelPedidos(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(26, 32, 26, 32));

        JButton novoPedido = Tema.botaoDestaque("+  Novo Pedido");
        novoPedido.addActionListener(e -> telaPrincipal.abrirNovoPedido());

        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 18));
        topo.add(Componentes.cabecalhoTela("Pedidos",
                "Consulte os pedidos registrados e acompanhe o status de entrega", novoPedido), BorderLayout.NORTH);
        topo.add(criarFiltros(), BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        tabela.getColumnModel().getColumn(5).setCellRenderer(new RendererStatus());
        Componentes.largurasColunas(tabela, 80, 160, 280, 70, 120, 140);
        Componentes.aoDuploClique(tabela, this::verDetalhes);
        add(Componentes.tabelaEmCartao(tabela), BorderLayout.CENTER);

        JButton detalhes = Tema.botaoPrimario("Ver Detalhes");
        detalhes.addActionListener(e -> verDetalhes());
        JPanel rodape = Tema.painelTransparente(new BorderLayout());
        rodape.add(resumo, BorderLayout.WEST);
        rodape.add(detalhes, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel criarFiltros() {
        filtroStatus.setPreferredSize(new Dimension(220, 38));
        filtroStatus.addActionListener(e -> atualizar());
        busca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizar(); }
            public void removeUpdate(DocumentEvent e) { atualizar(); }
            public void changedUpdate(DocumentEvent e) { atualizar(); }
        });
        JPanel filtros = Tema.painelTransparente(new BorderLayout(14, 0));
        filtros.add(Tema.comRotulo("Buscar por cliente", busca), BorderLayout.CENTER);
        JPanel status = Tema.painelTransparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        status.add(Tema.comRotulo("Status", filtroStatus));
        filtros.add(status, BorderLayout.EAST);
        return filtros;
    }

    @Override
    public void atualizar() {
        Mensagens.tentar(this, () -> {
            Object selecionado = filtroStatus.getSelectedItem();
            StatusPedido status = selecionado instanceof StatusPedido s ? s : null;
            pedidos = controller.listar(busca.getText(), status);
            modelo.setRowCount(0);
            for (Pedido pedido : pedidos) {
                modelo.addRow(new Object[]{
                        Formatador.numeroPedido(pedido.getId()),
                        Formatador.dataHora(pedido.getDataPedido()),
                        pedido.getCliente().getNome(),
                        pedido.getQuantidadeTotalItens(),
                        Formatador.moeda(pedido.calcularTotal()),
                        pedido.getStatus()});
            }
            resumo.setText(pedidos.size() + " pedido(s) encontrado(s) · dê duplo clique em uma linha para ver os detalhes");
        });
    }

    private void verDetalhes() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Mensagens.aviso(this, "Selecione um pedido na lista.");
            return;
        }
        telaPrincipal.abrirDetalhesPedido(pedidos.get(linha));
    }
}
