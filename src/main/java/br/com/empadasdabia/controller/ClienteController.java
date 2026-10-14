package br.com.empadasdabia.controller;

import br.com.empadasdabia.dao.ClienteDAO;
import br.com.empadasdabia.model.Cliente;
import br.com.empadasdabia.utils.ValidacaoException;
import br.com.empadasdabia.utils.Validador;

import java.util.List;

/** Recebe as ações das telas de Clientes, valida os dados e chama o DAO. */
public class ClienteController {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public List<Cliente> listar(String filtro) {
        return clienteDAO.listar(filtro);
    }

    /** Cadastra (id nulo) ou atualiza (id preenchido) um cliente. */
    public Cliente salvar(Integer id, String nome, String telefone, String email, String cep,
                          String logradouro, String numero, String bairro, String cidade) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome(Validador.obrigatorio(nome, "Nome", 100));
        cliente.setTelefone(Validador.telefone(telefone));
        cliente.setEmail(Validador.email(email));
        cliente.setCep(Validador.cep(cep));
        cliente.setLogradouro(Validador.opcional(logradouro, "Logradouro", 120));
        cliente.setNumero(Validador.opcional(numero, "Número", 10));
        cliente.setBairro(Validador.opcional(bairro, "Bairro", 60));
        cliente.setCidade(Validador.opcional(cidade, "Cidade", 60));

        if (id == null) {
            clienteDAO.inserir(cliente);
        } else {
            clienteDAO.atualizar(cliente);
        }
        return cliente;
    }

    public void excluir(Cliente cliente) {
        if (cliente == null) {
            throw new ValidacaoException("Selecione um cliente na lista.");
        }
        clienteDAO.excluir(cliente.getId());
    }
}
