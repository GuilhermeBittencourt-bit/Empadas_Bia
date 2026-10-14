package br.com.empadasdabia.controller;

import br.com.empadasdabia.dao.CategoriaDAO;
import br.com.empadasdabia.model.Categoria;
import br.com.empadasdabia.utils.ValidacaoException;
import br.com.empadasdabia.utils.Validador;

import java.util.List;

/** Recebe as ações da tela de Categorias, valida os dados e chama o DAO. */
public class CategoriaController {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }

    /** Cadastra (id nulo) ou atualiza (id preenchido) uma categoria. */
    public Categoria salvar(Integer id, String nome, String descricao) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(Validador.obrigatorio(nome, "Nome", 50));
        categoria.setDescricao(Validador.opcional(descricao, "Descrição", 150));

        if (id == null) {
            categoriaDAO.inserir(categoria);
        } else {
            categoriaDAO.atualizar(categoria);
        }
        return categoria;
    }

    public void excluir(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException("Selecione uma categoria na lista.");
        }
        if (categoria.getQuantidadeProdutos() > 0) {
            throw new ValidacaoException("Não é possível excluir: a categoria possui "
                    + categoria.getQuantidadeProdutos() + " produto(s).");
        }
        categoriaDAO.excluir(categoria.getId());
    }
}
