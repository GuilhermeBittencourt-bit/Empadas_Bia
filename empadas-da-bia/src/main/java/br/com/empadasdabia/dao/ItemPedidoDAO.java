package br.com.empadasdabia.dao;

import br.com.empadasdabia.database.ConexaoBD;
import br.com.empadasdabia.model.ItemPedido;
import br.com.empadasdabia.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Acesso à tabela item_pedido.
 * Os métodos de gravação recebem a Connection do PedidoDAO para que
 * o pedido e seus itens sejam salvos na mesma transação.
 */
public class ItemPedidoDAO {

    public void inserir(Connection conexao, int idPedido, ItemPedido item) throws SQLException {
        String sql = "INSERT INTO item_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, item.getProduto().getId());
            ps.setInt(3, item.getQuantidade());
            ps.setBigDecimal(4, item.getPrecoUnitario());
            ps.executeUpdate();
        }
    }

    public List<ItemPedido> listarPorPedido(Connection conexao, int idPedido) throws SQLException {
        String sql = """
                SELECT i.id_item, i.quantidade, i.preco_unitario,
                       p.id_produto, p.nome, p.descricao, p.preco, p.ativo, p.imagem,
                       c.id_categoria, c.nome AS categoria_nome, c.descricao AS categoria_descricao
                FROM item_pedido i
                JOIN produto p ON p.id_produto = i.id_produto
                JOIN categoria c ON c.id_categoria = p.id_categoria
                WHERE i.id_pedido = ?
                ORDER BY i.id_item
                """;
        List<ItemPedido> itens = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemPedido item = new ItemPedido();
                    item.setId(rs.getInt("id_item"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
                    item.setProduto(ProdutoDAO.montar(rs));
                    itens.add(item);
                }
            }
        }
        return itens;
    }

    /** Produtos mais vendidos nos últimos 7 dias (pedidos cancelados não contam). */
    public Map<Produto, Integer> maisVendidosDaSemana(int limite) {
        String sql = """
                SELECT p.id_produto, p.nome, p.descricao, p.preco, p.ativo, p.imagem,
                       c.id_categoria, c.nome AS categoria_nome, c.descricao AS categoria_descricao,
                       SUM(i.quantidade) AS total_vendido
                FROM item_pedido i
                JOIN pedido pe ON pe.id_pedido = i.id_pedido
                JOIN produto p ON p.id_produto = i.id_produto
                JOIN categoria c ON c.id_categoria = p.id_categoria
                WHERE pe.status <> 'CANCELADO'
                  AND pe.data_pedido >= NOW() - INTERVAL 7 DAY
                GROUP BY p.id_produto, p.nome, p.descricao, p.preco, p.ativo, p.imagem,
                         c.id_categoria, c.nome, c.descricao
                ORDER BY total_vendido DESC
                LIMIT ?
                """;
        Map<Produto, Integer> ranking = new LinkedHashMap<>();
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ranking.put(ProdutoDAO.montar(rs), rs.getInt("total_vendido"));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar os mais vendidos: " + e.getMessage(), e);
        }
        return ranking;
    }
}
