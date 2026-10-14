package br.com.empadasdabia.dao;

import br.com.empadasdabia.database.ConexaoBD;
import br.com.empadasdabia.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela cliente. */
public class ClienteDAO {

    static final String COLUNAS = "c.id_cliente, c.nome, c.telefone, c.email, c.cep, c.logradouro, c.numero, c.bairro, c.cidade";

    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, telefone, email, cep, logradouro, numero, bairro, cidade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(ps, cliente);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar cliente: " + e.getMessage(), e);
        }
    }

    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ?, cep = ?, logradouro = ?, numero = ?, bairro = ?, cidade = ? WHERE id_cliente = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencherParametros(ps, cliente);
            ps.setInt(9, cliente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (DAOException.violouRestricao(e)) {
                throw new DAOException("Este cliente possui pedidos registrados e não pode ser excluído.", e);
            }
            throw new DAOException("Erro ao excluir cliente: " + e.getMessage(), e);
        }
    }

    /** Lista clientes filtrando por parte do nome ou do telefone (filtro opcional). */
    public List<Cliente> listar(String filtro) {
        String termo = filtro == null ? "" : filtro.trim();
        String numeros = termo.replaceAll("\\D", "");
        // Se o usuário digitou números, procura também no telefone
        String sql = "SELECT " + COLUNAS + " FROM cliente c WHERE c.nome LIKE ?"
                + (numeros.isEmpty() ? "" : " OR c.telefone LIKE ?")
                + " ORDER BY c.nome";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            if (!numeros.isEmpty()) {
                ps.setString(2, "%" + numeros + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(montar(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    public int contar() {
        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement ps = conexao.prepareStatement("SELECT COUNT(*) FROM cliente");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DAOException("Erro ao contar clientes: " + e.getMessage(), e);
        }
    }

    private void preencherParametros(PreparedStatement ps, Cliente cliente) throws SQLException {
        ps.setString(1, cliente.getNome());
        ps.setString(2, cliente.getTelefone());
        ps.setString(3, cliente.getEmail());
        ps.setString(4, cliente.getCep());
        ps.setString(5, cliente.getLogradouro());
        ps.setString(6, cliente.getNumero());
        ps.setString(7, cliente.getBairro());
        ps.setString(8, cliente.getCidade());
    }

    /** Converte uma linha do ResultSet em Cliente (também usado pelo PedidoDAO). */
    static Cliente montar(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id_cliente"));
        cliente.setNome(rs.getString("nome"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setEmail(rs.getString("email"));
        cliente.setCep(rs.getString("cep"));
        cliente.setLogradouro(rs.getString("logradouro"));
        cliente.setNumero(rs.getString("numero"));
        cliente.setBairro(rs.getString("bairro"));
        cliente.setCidade(rs.getString("cidade"));
        return cliente;
    }
}
