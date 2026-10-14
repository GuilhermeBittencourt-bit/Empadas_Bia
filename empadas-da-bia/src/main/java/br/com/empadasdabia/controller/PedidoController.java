package br.com.empadasdabia.controller;

import br.com.empadasdabia.dao.PedidoDAO;
import br.com.empadasdabia.model.Cliente;
import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.model.StatusPedido;
import br.com.empadasdabia.utils.ValidacaoException;
import br.com.empadasdabia.utils.Validador;

import java.time.LocalDateTime;
import java.util.List;

/** Regras do fluxo de pedidos: validar, salvar e mudar o status. */
public class PedidoController {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public List<Pedido> listar(String nomeCliente, StatusPedido status) {
        return pedidoDAO.listar(nomeCliente, status);
    }

    public Pedido buscar(int idPedido) {
        return pedidoDAO.buscarPorId(idPedido);
    }

    /** Confere se o pedido pode ser revisado/salvo: precisa de cliente e de pelo menos um item. */
    public void validar(Pedido pedido, Cliente cliente) {
        if (cliente == null) {
            throw new ValidacaoException("Selecione o cliente do pedido.");
        }
        if (pedido.getItens().isEmpty()) {
            throw new ValidacaoException("Adicione pelo menos um produto ao pedido.");
        }
    }

    /** Valida o pedido montado na tela e grava no banco (pedido + itens). */
    public void salvar(Pedido pedido, Cliente cliente, String observacao) {
        validar(pedido, cliente);
        pedido.setCliente(cliente);
        pedido.setObservacao(Validador.opcional(observacao, "Observação", 255));
        pedido.setDataPedido(LocalDateTime.now().withNano(0));
        pedido.setStatus(StatusPedido.PENDENTE);
        pedidoDAO.salvar(pedido);
    }

    public void marcarComoEntregue(Pedido pedido) {
        alterarStatus(pedido, StatusPedido.ENTREGUE);
    }

    /** Cancelamento substitui a exclusão: o pedido continua no histórico. */
    public void cancelar(Pedido pedido) {
        alterarStatus(pedido, StatusPedido.CANCELADO);
    }

    private void alterarStatus(Pedido pedido, StatusPedido novoStatus) {
        if (!pedido.podeAlterarStatus()) {
            throw new ValidacaoException("Apenas pedidos pendentes podem ser alterados. Este pedido está "
                    + pedido.getStatus().getDescricao().toLowerCase() + ".");
        }
        pedidoDAO.atualizarStatus(pedido.getId(), novoStatus);
        pedido.setStatus(novoStatus);
    }
}
