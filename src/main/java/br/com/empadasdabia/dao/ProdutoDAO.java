package br.com.empadasdabia.dao;

import br.com.empadasdabia.database.ConexaoBD;
import br.com.empadasdabia.model.Categoria;
import br.com.empadasdabia.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela produto. */
public class ProdutoDAO {

    private static final String SELECT_BASE = """
            SELECT p.id_produto, p.nome, p.descricao, p.preco, p.ativo, p.imagem,
                   c.id_categoria, c.nome AS categoria_nome, c.descricao AS categoria_descricao
            FROM produto p
            JOIN categoria c ON c.id_categoria = p.id_categoria
            """;

    public void inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, descricao, preco, ativo, imagem, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(ps, produto);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, preco = ?, ativo = ?, imagem = ?, id_categoria = ? WHERE id_produto = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencherParametros(ps, produto);
            ps.setInt(7, produto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void alterarAtivo(int id, boolean ativo) {
        String sql = "UPDATE produto SET ativo = ? WHERE id_produto = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setBoolean(1, ativo);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao alterar status do produto: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id_produto = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (DAOException.violouRestricao(e)) {
                throw new DAOException("Este produto já aparece em pedidos e não pode ser excluído.\n"
                        + "Use \"Ativar/Desativar\" para tirá-lo do cardápio.", e);
            }
            throw new DAOException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    /**
     * Lista os produtos com filtros opcionais.
     * @param nome         parte do nome (ou null para todos)
     * @param idCategoria  categoria (ou null para todas)
     * @param somenteAtivos true para trazer apenas os produtos do cardápio
     */
    public List<Produto> listar(String nome, Integer idCategoria, boolean somenteAtivos) {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append(" WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (nome != null && !nome.isBlank()) {
            sql.append(" AND p.nome LIKE ?");
            parametros.add("%" + nome.trim() + "%");
        }
        if (idCategoria != null) {
            sql.append(" AND p.id_categoria = ?");
            parametros.add(idCategoria);
        }
        if (somenteAtivos) {
            sql.append(" AND p.ativo = TRUE");
        }
        sql.append(" ORDER BY c.id_categoria, p.id_produto");

        List<Produto> produtos = new ArrayList<>();
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    produtos.add(montar(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return produtos;
    }

    public int contarAtivos() {
        return contar("SELECT COUNT(*) FROM produto WHERE ativo = TRUE");
    }

    public int contarTodos() {
        return contar("SELECT COUNT(*) FROM produto");
    }

    private int contar(String sql) {
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DAOException("Erro ao contar produtos: " + e.getMessage(), e);
        }
    }

    private void preencherParametros(PreparedStatement ps, Produto produto) throws SQLException {
        ps.setString(1, produto.getNome());
        ps.setString(2, produto.getDescricao());
        ps.setBigDecimal(3, produto.getPreco());
        ps.setBoolean(4, produto.isAtivo());
        ps.setString(5, produto.getImagem());
        ps.setInt(6, produto.getCategoria().getId());
    }

    /** Converte uma linha do ResultSet em um objeto Produto (com sua Categoria). */
    static Produto montar(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getInt("id_produto"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPreco(rs.getBigDecimal("preco"));
        produto.setAtivo(rs.getBoolean("ativo"));
        produto.setImagem(rs.getString("imagem"));
        produto.setCategoria(new Categoria(rs.getInt("id_categoria"),
                rs.getString("categoria_nome"), rs.getString("categoria_descricao")));
        return produto;
    }
}
