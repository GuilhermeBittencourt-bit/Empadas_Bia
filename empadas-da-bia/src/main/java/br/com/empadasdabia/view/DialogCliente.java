package br.com.empadasdabia.view;

import br.com.empadasdabia.controller.ClienteController;
import br.com.empadasdabia.model.Cliente;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.componentes.Componentes;
import br.com.empadasdabia.view.componentes.PainelArredondado;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** Formulário de cadastro e edição de cliente. */
public class DialogCliente extends JDialog {

    private final ClienteController controller = new ClienteController();
    private final Cliente clienteOriginal;
    private boolean salvo;

    private final JTextField nome = Tema.campoTexto();
    private final JTextField telefone = Tema.campoTexto();
    private final JTextField email = Tema.campoTexto();
    private final JTextField cep = Tema.campoTexto();
    private final JTextField logradouro = Tema.campoTexto();
    private final JTextField numero = Tema.campoTexto();
    private final JTextField bairro = Tema.campoTexto();
    private final JTextField cidade = Tema.campoTexto();

    public DialogCliente(Component pai, Cliente cliente) {
        super(SwingUtilities.getWindowAncestor(pai), cliente == null ? "Novo Cliente" : "Editar Cliente",
                ModalityType.APPLICATION_MODAL);
        this.clienteOriginal = cliente;

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBorder(BorderFactory.createEmptyBorder(22, 28, 20, 28));

        JPanel textos = Tema.painelTransparente(new GridLayout(2, 1, 0, 2));
        JLabel titulo = Tema.titulo(cliente == null ? "Novo Cliente" : "Editar Cliente");
        titulo.setFont(Tema.fonteCardapio(Font.BOLD, 26));
        textos.add(titulo);
        textos.add(Tema.subtitulo("Campos com * são obrigatórios"));
        raiz.add(textos, BorderLayout.NORTH);

        JPanel centro = Tema.painelTransparente(null);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(criarCartaoDados());
        centro.add(Box.createVerticalStrut(14));
        centro.add(criarCartaoEndereco());
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(criarBotoes(), BorderLayout.SOUTH);
        setContentPane(raiz);

        telefone.putClientProperty("JTextField.placeholderText", "(47) 99999-9999");
        email.putClientProperty("JTextField.placeholderText", "nome@email.com");
        cep.putClientProperty("JTextField.placeholderText", "89201-000");
        preencher(cliente);

        setSize(660, 600);
        setResizable(false);
        setLocationRelativeTo(pai);
    }

    public boolean foiSalvo() {
        return salvo;
    }

    private JComponent criarCartaoDados() {
        PainelArredondado cartao = Componentes.cartao(new BorderLayout(0, 12));
        cartao.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        cartao.add(rotuloSecao("DADOS DO CLIENTE"), BorderLayout.NORTH);
        JPanel campos = Tema.painelTransparente(new BorderLayout(0, 12));
        campos.add(Tema.comRotulo("Nome *", nome), BorderLayout.NORTH);
        JPanel linha = Tema.painelTransparente(new GridLayout(1, 2, 12, 0));
        linha.add(Tema.comRotulo("Telefone *", telefone));
        linha.add(Tema.comRotulo("E-mail", email));
        campos.add(linha, BorderLayout.CENTER);
        cartao.add(campos, BorderLayout.CENTER);
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cartao;
    }

    private JComponent criarCartaoEndereco() {
        PainelArredondado cartao = Componentes.cartao(new BorderLayout(0, 12));
        cartao.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        cartao.add(rotuloSecao("ENDEREÇO"), BorderLayout.NORTH);

        JPanel linha1 = Tema.painelTransparente(new BorderLayout(12, 0));
        JPanel campoCep = Tema.comRotulo("CEP", cep);
        campoCep.setPreferredSize(new Dimension(140, 64));
        JPanel campoNumero = Tema.comRotulo("Número", numero);
        campoNumero.setPreferredSize(new Dimension(90, 64));
        linha1.add(campoCep, BorderLayout.WEST);
        linha1.add(Tema.comRotulo("Logradouro", logradouro), BorderLayout.CENTER);
        linha1.add(campoNumero, BorderLayout.EAST);

        JPanel linha2 = Tema.painelTransparente(new GridLayout(1, 2, 12, 0));
        linha2.add(Tema.comRotulo("Bairro", bairro));
        linha2.add(Tema.comRotulo("Cidade", cidade));

        JPanel campos = Tema.painelTransparente(new BorderLayout(0, 12));
        campos.add(linha1, BorderLayout.NORTH);
        campos.add(linha2, BorderLayout.CENTER);
        cartao.add(campos, BorderLayout.CENTER);
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cartao;
    }

    private JLabel rotuloSecao(String texto) {
        return Tema.texto(texto, Font.BOLD, 12.5f, Tema.TEXTO_SUAVE);
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

    private void preencher(Cliente cliente) {
        if (cliente == null) {
            return;
        }
        nome.setText(cliente.getNome());
        telefone.setText(Formatador.telefone(cliente.getTelefone()));
        email.setText(cliente.getEmail());
        cep.setText(cliente.getCep() == null ? "" : Formatador.cep(cliente.getCep()));
        logradouro.setText(cliente.getLogradouro());
        numero.setText(cliente.getNumero());
        bairro.setText(cliente.getBairro());
        cidade.setText(cliente.getCidade());
    }

    private void salvar() {
        Integer id = clienteOriginal == null ? null : clienteOriginal.getId();
        boolean ok = Mensagens.tentar(this, () -> controller.salvar(id, nome.getText(), telefone.getText(),
                email.getText(), cep.getText(), logradouro.getText(), numero.getText(),
                bairro.getText(), cidade.getText()));
        if (ok) {
            salvo = true;
            dispose();
        }
    }
}
