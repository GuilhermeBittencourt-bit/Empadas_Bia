package br.com.empadasdabia.dao;

import br.com.empadasdabia.database.ConexaoBD;
import br.com.empadasdabia.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela categoria. Todo o SQL de categorias fica aqui. */
public class CategoriaDAO {

    public void inserir(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome, descricao) VALUES (?, ?)";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    categoria.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw erroAoSalvar(e);
        }
    }

    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nome = ?, descricao = ? WHERE id_categoria = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.setInt(3, categoria.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw erroAoSalvar(e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (DAOException.violouRestricao(e)) {
                throw new DAOException("Não é possível excluir: existem produtos nesta categoria.", e);
            }
            throw new DAOException("Erro ao excluir categoria: " + e.getMessage(), e);
        }
    }

    /** Lista as categorias com a quantidade de produtos de cada uma. */
    public List<Categoria> listar() {
        String sql = """
                SELECT c.id_categoria, c.nome, c.descricao, COUNT(p.id_produto) AS qtd_produtos
                FROM categoria c
                LEFT JOIN produto p ON p.id_categoria = c.id_categoria
                GROUP BY c.id_categoria, c.nome, c.descricao
                ORDER BY c.id_categoria
                """;
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("nome"), rs.getString("descricao"));
                categoria.setQuantidadeProdutos(rs.getInt("qtd_produtos"));
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar categorias: " + e.getMessage(), e);
        }
        return categorias;
    }

    private DAOException erroAoSalvar(SQLException e) {
        if (DAOException.violouRestricao(e)) {
            return new DAOException("Já existe uma categoria com esse nome.", e);
        }
        return new DAOException("Erro ao salvar categoria: " + e.getMessage(), e);
    }
}
