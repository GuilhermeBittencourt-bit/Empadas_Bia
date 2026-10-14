package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.DashboardController;
import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Imagens;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.CardIndicador;
import br.com.empadasdabia.view.componentes.Componentes;
import br.com.empadasdabia.view.componentes.PainelArredondado;
import br.com.empadasdabia.view.componentes.RendererStatus;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Tela inicial: números do dia, últimos pedidos e empadas mais pedidas. */
public class PainelDashboard extends JPanel implements Atualizavel {

    private final TelaPrincipal telaPrincipal;
    private final DashboardController controller = new DashboardController();

    private final JLabel data = Tema.subtitulo(" ");
    private final CardIndicador cardPedidos = new CardIndicador("Pedidos hoje", Tema.AZUL);
    private final CardIndicador cardFaturamento = new CardIndicador("Faturamento do mês", Tema.ROSA);
    private final CardIndicador cardClientes = new CardIndicador("Clientes cadastrados", Tema.AZUL);
    private final CardIndicador cardProdutos = new CardIndicador("Produtos ativos", Tema.AZUL);

    private final DefaultTableModel modeloPedidos =
            Componentes.modeloSomenteLeitura("Nº", "Data", "Cliente", "Total", "Status");
    private final JTable tabelaPedidos = Componentes.tabela(modeloPedidos);
    private List<Pedido> ultimosPedidos = new ArrayList<>();
    private final JPanel listaMaisPedidas = Tema.painelTransparente(null);

    public PainelDashboard(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        setLayout(new BorderLayout(0, 22));
        setBorder(BorderFactory.createEmptyBorder(26, 32, 26, 32));

        JButton novoPedido = Tema.botaoDestaque("+  Novo Pedido");
        novoPedido.addActionListener(e -> telaPrincipal.abrirNovoPedido());

        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 22));
        topo.add(Componentes.cabecalhoTela("Olá, Bia!", data, novoPedido), BorderLayout.NORTH);

        JPanel indicadores = Tema.painelTransparente(new GridLayout(1, 4, 18, 0));
        indicadores.add(cardPedidos);
        indicadores.add(cardFaturamento);
        indicadores.add(cardClientes);
        indicadores.add(cardProdutos);
        topo.add(indicadores, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        JPanel inferior = Tema.painelTransparente(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 2;
        c.insets = new Insets(0, 0, 0, 18);
        inferior.add(criarCartaoUltimosPedidos(), c);
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        inferior.add(criarCartaoMaisPedidas(), c);
        add(inferior, BorderLayout.CENTER);
    }

    private JPanel criarCartaoUltimosPedidos() {
        PainelArredondado cartao = Componentes.cartao(new BorderLayout());
        cartao.setBorder(BorderFactory.createEmptyBorder(6, 2, 8, 2));
        cartao.setPreferredSize(new Dimension(600, 300));

        JPanel titulo = Tema.painelTransparente(new BorderLayout());
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        titulo.add(Tema.tituloSecao("Últimos pedidos"), BorderLayout.WEST);
        JButton verTodos = Tema.botaoLink("Ver todos");
        verTodos.addActionListener(e -> telaPrincipal.mostrar(TelaPrincipal.PEDIDOS));
        titulo.add(verTodos, BorderLayout.EAST);
        cartao.add(titulo, BorderLayout.NORTH);

        tabelaPedidos.getColumnModel().getColumn(4).setCellRenderer(new RendererStatus());
        Componentes.largurasColunas(tabelaPedidos, 70, 120, 200, 110, 130);
        Componentes.aoDuploClique(tabelaPedidos, () ->
                telaPrincipal.abrirDetalhesPedido(ultimosPedidos.get(tabelaPedidos.getSelectedRow())));
        JScrollPane rolagem = new JScrollPane(tabelaPedidos);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getViewport().setBackground(Tema.BRANCO);
        cartao.add(rolagem, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarCartaoMaisPedidas() {
        PainelArredondado cartao = Componentes.cartao(new BorderLayout(0, 10));
        cartao.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        cartao.setPreferredSize(new Dimension(300, 300));
        cartao.add(Tema.tituloSecao("Mais pedidas da semana"), BorderLayout.NORTH);
        listaMaisPedidas.setLayout(new BoxLayout(listaMaisPedidas, BoxLayout.Y_AXIS));
        cartao.add(listaMaisPedidas, BorderLayout.CENTER);
        JButton verCardapio = Tema.botaoLink("Ver cardápio completo");
        verCardapio.addActionListener(e -> telaPrincipal.mostrar(TelaPrincipal.PRODUTOS));
        JPanel rodape = Tema.painelTransparente(new BorderLayout());
        rodape.add(verCardapio, BorderLayout.WEST);
        cartao.add(rodape, BorderLayout.SOUTH);
        return cartao;
    }

    @Override
    public void atualizar() {
        Mensagens.tentar(this, () -> {
            String hoje = Formatador.dataPorExtenso(LocalDateTime.now());
            data.setText("Resumo do dia · " + hoje);

            cardPedidos.setValores(String.valueOf(controller.pedidosDeHoje()),
                    controller.pendentesDeHoje() + " pendente(s) de entrega");
            cardFaturamento.setValores(Formatador.moeda(controller.faturamentoDoMes()), "Sem pedidos cancelados");
            cardClientes.setValores(String.valueOf(controller.totalDeClientes()), "Total na base");
            cardProdutos.setValores(String.valueOf(controller.produtosAtivos()),
                    "de " + controller.produtosCadastrados() + " no cardápio");

            ultimosPedidos = controller.ultimosPedidos();
            modeloPedidos.setRowCount(0);
            for (Pedido pedido : ultimosPedidos) {
                modeloPedidos.addRow(new Object[]{
                        Formatador.numeroPedido(pedido.getId()),
                        Formatador.dataCurta(pedido.getDataPedido()),
                        pedido.getCliente().getNome(),
                        Formatador.moeda(pedido.calcularTotal()),
                        pedido.getStatus()});
            }
            montarMaisPedidas(controller.maisPedidasDaSemana());
        });
    }

    private void montarMaisPedidas(Map<Produto, Integer> ranking) {
        listaMaisPedidas.removeAll();
        if (ranking.isEmpty()) {
            listaMaisPedidas.add(Tema.subtitulo("Nenhuma venda nos últimos 7 dias."));
        }
        int posicao = 1;
        for (Map.Entry<Produto, Integer> entrada : ranking.entrySet()) {
            JPanel linha = Tema.painelTransparente(new BorderLayout(14, 0));
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);
            linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            linha.add(new JLabel(Imagens.carregar(entrada.getKey().getImagem(), 72, 61)), BorderLayout.WEST);

            JPanel textos = Tema.painelTransparente(new GridLayout(2, 1));
            JLabel nome = new JLabel(entrada.getKey().getNome().replace("Empada de ", "").replace("Empada ", ""));
            nome.setFont(Tema.fonteCardapio(Font.BOLD, 15));
            textos.add(nome);
            textos.add(Tema.texto(entrada.getValue() + " unidades", Font.PLAIN, 13, Tema.TEXTO_SUAVE));
            linha.add(textos, BorderLayout.CENTER);
            linha.add(Tema.preco(posicao + "º", 20), BorderLayout.EAST);

            listaMaisPedidas.add(linha);
            listaMaisPedidas.add(Box.createVerticalStrut(8));
            posicao++;
        }
        listaMaisPedidas.revalidate();
        listaMaisPedidas.repaint();
    }
}
