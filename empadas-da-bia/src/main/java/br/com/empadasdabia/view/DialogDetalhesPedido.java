package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.PedidoController;
import br.com.empadasdabia.model.Cliente;
import br.com.empadasdabia.model.ItemPedido;
import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Imagens;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.Componentes;
import br.com.empadasdabia.view.componentes.PainelArredondado;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** Detalhes de um pedido salvo, com as ações de marcar como entregue ou cancelar. */
public class DialogDetalhesPedido extends JDialog {

    private final PedidoController controller = new PedidoController();
    private final Pedido pedido;

    private final JPanel areaSelo = Tema.painelTransparente(new FlowLayout(FlowLayout.LEFT, 0, 6));
    private final JButton entregar = Tema.botaoPrimario("Marcar como Entregue");
    private final JButton cancelar = Tema.botaoPerigo("Cancelar Pedido");

    public DialogDetalhesPedido(JFrame dono, Pedido pedido) {
        super(dono, "Detalhes do Pedido", true);
        this.pedido = pedido;

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBorder(BorderFactory.createEmptyBorder(22, 28, 20, 28));
        JPanel topo = Tema.painelTransparente(new BorderLayout(0, 16));
        topo.add(criarCabecalho(), BorderLayout.NORTH);
        topo.add(criarCartoesCliente(), BorderLayout.CENTER);
        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(criarItens(), BorderLayout.CENTER);
        raiz.add(criarBotoes(), BorderLayout.SOUTH);
        setContentPane(raiz);

        atualizarStatus();
        setSize(860, 680);
        setLocationRelativeTo(dono);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = Tema.painelTransparente(new BorderLayout(12, 0));
        JLabel titulo = Tema.titulo("Pedido " + Formatador.numeroPedido(pedido.getId()));
        titulo.setFont(Tema.fonteCardapio(Font.BOLD, 26));
        JPanel esquerda = Tema.painelTransparente(new FlowLayout(FlowLayout.LEFT, 12, 0));
        esquerda.add(titulo);
        esquerda.add(areaSelo);
        cabecalho.add(esquerda, BorderLayout.WEST);
        cabecalho.add(Tema.subtitulo(Formatador.dataHora(pedido.getDataPedido()).replace(" ", " às ")), BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarCartoesCliente() {
        Cliente cliente = pedido.getCliente();
        JPanel linha = Tema.painelTransparente(new GridLayout(1, 2, 14, 0));
        linha.add(cartaoInformacao("CLIENTE", cliente.getNome(),
                Formatador.telefone(cliente.getTelefone()), Formatador.textoOuTraco(cliente.getEmail())));
        String cep = cliente.getCep() == null ? "CEP não informado" : "CEP " + Formatador.cep(cliente.getCep());
        linha.add(cartaoInformacao("ENDEREÇO DE ENTREGA", cliente.getEnderecoCompleto(), cep, " "));
        return linha;
    }

    private JPanel cartaoInformacao(String titulo, String principal, String linha2, String linha3) {
        PainelArredondado cartao = Componentes.cartao(new GridLayout(4, 1, 0, 2));
        cartao.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        cartao.add(Tema.texto(titulo, Font.BOLD, 12, Tema.TEXTO_SUAVE));
        cartao.add(Tema.texto(principal, Font.BOLD, 15, Tema.TEXTO));
        cartao.add(Tema.texto(linha2, Font.PLAIN, 14, Tema.TEXTO));
        cartao.add(Tema.texto(linha3, Font.PLAIN, 14, new Color(0x4A443D)));
        return cartao;
    }

    private JPanel criarItens() {
        DefaultTableModel modelo = Componentes.modeloSomenteLeitura("Produto", "Qtd", "Preço un.", "Subtotal");
        for (ItemPedido item : pedido.getItens()) {
            modelo.addRow(new Object[]{item.getProduto(), item.getQuantidade(),
                    Formatador.moeda(item.getPrecoUnitario()), Formatador.moeda(item.getSubtotal())});
        }
        JTable tabela = Componentes.tabela(modelo);
        tabela.setRowHeight(54);
        tabela.getColumnModel().getColumn(0).setCellRenderer(new RendererProdutoComFoto());
        Componentes.largurasColunas(tabela, 380, 70, 130, 130);

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getViewport().setBackground(Tema.BRANCO);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Tema.CABECALHO_TABELA);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDA),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        String obs = pedido.getObservacao() == null ? "Sem observações" : "Observação: " + pedido.getObservacao();
        rodape.add(Tema.texto(obs, Font.PLAIN, 14, new Color(0x4A443D)), BorderLayout.WEST);
        JPanel totalPainel = Tema.painelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        totalPainel.add(Tema.texto("TOTAL", Font.BOLD, 13, Tema.TEXTO_SUAVE));
        totalPainel.add(Tema.preco(Formatador.moeda(pedido.calcularTotal()), 26));
        rodape.add(totalPainel, BorderLayout.EAST);

        PainelArredondado cartao = Componentes.cartao(new BorderLayout());
        cartao.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        cartao.add(rolagem, BorderLayout.CENTER);
        cartao.add(rodape, BorderLayout.SOUTH);
        return cartao;
    }

    private JPanel criarBotoes() {
        JButton fechar = Tema.botaoSecundario("Fechar");
        fechar.addActionListener(e -> dispose());
        entregar.addActionListener(e -> marcarComoEntregue());
        cancelar.addActionListener(e -> cancelarPedido());

        JPanel botoes = Tema.painelTransparente(new BorderLayout());
        botoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDA),
                BorderFactory.createEmptyBorder(14, 0, 0, 0)));
        botoes.add(cancelar, BorderLayout.WEST);
        JPanel direita = Tema.painelTransparente(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.add(fechar);
        direita.add(entregar);
        botoes.add(direita, BorderLayout.EAST);
        return botoes;
    }

    private void marcarComoEntregue() {
        if (Mensagens.tentar(this, () -> controller.marcarComoEntregue(pedido))) {
            atualizarStatus();
        }
    }

    private void cancelarPedido() {
        if (Mensagens.confirmar(this, "Cancelar o pedido " + Formatador.numeroPedido(pedido.getId())
                + "?\nO pedido continuará no histórico, mas não entrará no faturamento.", "Cancelar pedido")
                && Mensagens.tentar(this, () -> controller.cancelar(pedido))) {
            atualizarStatus();
        }
    }

    /** Atualiza o selo de status e habilita os botões apenas para pedidos pendentes. */
    private void atualizarStatus() {
        Color[] cores = Tema.coresStatus(pedido.getStatus());
        areaSelo.removeAll();
        areaSelo.add(Componentes.selo(pedido.getStatus().name(), cores[0], cores[1]));
        areaSelo.revalidate();
        areaSelo.repaint();
        entregar.setEnabled(pedido.podeAlterarStatus());
        cancelar.setEnabled(pedido.podeAlterarStatus());
    }

    /** Mostra a miniatura da empada ao lado do nome do produto. */
    private static class RendererProdutoComFoto extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado,
                                                       boolean foco, int linha, int coluna) {
            Produto produto = (Produto) valor;
            JLabel rotulo = (JLabel) super.getTableCellRendererComponent(tabela, produto.getNome(),
                    selecionado, foco, linha, coluna);
            rotulo.setIcon(Imagens.carregar(produto.getImagem(), 52, 44));
            rotulo.setIconTextGap(12);
            return rotulo;
        }
    }
}
