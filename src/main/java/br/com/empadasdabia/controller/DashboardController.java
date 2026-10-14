package br.com.empadasdabia.controller;

import br.com.empadasdabia.dao.ClienteDAO;
import br.com.empadasdabia.dao.ItemPedidoDAO;
import br.com.empadasdabia.dao.PedidoDAO;
import br.com.empadasdabia.dao.ProdutoDAO;
import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.model.Produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Reúne os números exibidos na tela inicial. */
public class DashboardController {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();

    public int pedidosDeHoje() {
        return pedidoDAO.contarPedidosDeHoje();
    }

    public int pendentesDeHoje() {
        return pedidoDAO.contarPendentesDeHoje();
    }

    public BigDecimal faturamentoDoMes() {
        return pedidoDAO.faturamentoDoMes();
    }

    public int totalDeClientes() {
        return clienteDAO.contar();
    }

    public int produtosAtivos() {
        return produtoDAO.contarAtivos();
    }

    public int produtosCadastrados() {
        return produtoDAO.contarTodos();
    }

    public List<Pedido> ultimosPedidos() {
        return pedidoDAO.listarRecentes(5);
    }

    public Map<Produto, Integer> maisPedidasDaSemana() {
        return itemPedidoDAO.maisVendidosDaSemana(3);
    }
}
