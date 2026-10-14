package br.com.empadasdabia.dao;

import br.com.empadasdabia.database.ConexaoBD;
import br.com.empadasdabia.model.ItemPedido;
import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.model.StatusPedido;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela pedido (e, por meio do ItemPedidoDAO, aos seus itens). */
public class PedidoDAO {

    private final ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();

    private static final String SELECT_BASE = "SELECT pe.id_pedido, pe.data_pedido, pe.status, pe.observacao, "
            + ClienteDAO.COLUNAS + " FROM pedido pe JOIN cliente c ON c.id_cliente = pe.id_cliente";

    /**
     * Salva o pedido e todos os itens em uma TRANSAÇÃO:
     * se algum item falhar, nada é gravado (rollback).
     */
    public void salvar(Pedido pedido) {
        String sql = "INSERT INTO pedido (id_cliente, data_pedido, status, observacao) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.getConexao()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, pedido.getCliente().getId());
                ps.setObject(2, pedido.getDataPedido());
                ps.setString(3, pedido.getStatus().name());
                ps.setString(4, pedido.getObservacao());
                ps.executeUpdate();
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    chaves.next();
                    pedido.setId(chaves.getInt(1));
                }
                for (ItemPedido item : pedido.getItens()) {
                    itemPedidoDAO.inserir(conexao, pedido.getId(), item);
                }
                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                pedido.setId(null);
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar o pedido. Nenhuma informação foi gravada.\n" + e.getMessage(), e);
        }
    }

    public void atualizarStatus(int idPedido, StatusPedido status) {
        String sql = "UPDATE pedido SET status = ? WHERE id_pedido = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar o status do pedido: " + e.getMessage(), e);
        }
    }

    /** Lista os pedidos (mais recentes primeiro) já com cliente e itens. Filtros opcionais. */
    public List<Pedido> listar(String nomeCliente, StatusPedido status) {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append(" WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (nomeCliente != null && !nomeCliente.isBlank()) {
            sql.append(" AND c.nome LIKE ?");
            parametros.add("%" + nomeCliente.trim() + "%");
        }
        if (status != null) {
            sql.append(" AND pe.status = ?");
            parametros.add(status.name());
        }
        sql.append(" ORDER BY pe.data_pedido DESC, pe.id_pedido DESC");
        return consultar(sql.toString(), parametros);
    }

    public List<Pedido> listarRecentes(int quantidade) {
        String sql = SELECT_BASE + " ORDER BY pe.data_pedido DESC, pe.id_pedido DESC LIMIT ?";
        return consultar(sql, List.of(quantidade));
    }

    public Pedido buscarPorId(int idPedido) {
        List<Pedido> pedidos = consultar(SELECT_BASE + " WHERE pe.id_pedido = ?", List.of(idPedido));
        return pedidos.isEmpty() ? null : pedidos.get(0);
    }

    public int contarPedidosDeHoje() {
        return contar("SELECT COUNT(*) FROM pedido WHERE DATE(data_pedido) = CURDATE() AND status <> 'CANCELADO'");
    }

    public int contarPendentesDeHoje() {
        return contar("SELECT COUNT(*) FROM pedido WHERE DATE(data_pedido) = CURDATE() AND status = 'PENDENTE'");
    }

    /** Soma dos itens de todos os pedidos não cancelados do mês atual. */
    public BigDecimal faturamentoDoMes() {
        String sql = """
                SELECT COALESCE(SUM(i.quantidade * i.preco_unitario), 0)
                FROM pedido pe
                JOIN item_pedido i ON i.id_pedido = pe.id_pedido
                WHERE pe.status <> 'CANCELADO'
                  AND YEAR(pe.data_pedido) = YEAR(CURDATE())
                  AND MONTH(pe.data_pedido) = MONTH(CURDATE())
                """;
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular o faturamento: " + e.getMessage(), e);
        }
    }

    private int contar(String sql) {
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DAOException("Erro ao contar pedidos: " + e.getMessage(), e);
        }
    }

    /** Executa a consulta e carrega os itens de cada pedido usando a mesma conexão. */
    private List<Pedido> consultar(String sql, List<Object> parametros) {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("id_pedido"));
                    pedido.setDataPedido(rs.getObject("data_pedido", LocalDateTime.class));
                    pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
                    pedido.setObservacao(rs.getString("observacao"));
                    pedido.setCliente(ClienteDAO.montar(rs));
                    pedidos.add(pedido);
                }
            }
            for (Pedido pedido : pedidos) {
                for (ItemPedido item : itemPedidoDAO.listarPorPedido(conexao, pedido.getId())) {
                    pedido.adicionarItemCarregado(item);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao consultar pedidos: " + e.getMessage(), e);
        }
        return pedidos;
    }
}
