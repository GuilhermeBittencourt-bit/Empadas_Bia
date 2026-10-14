package br.com.empadasdabia.controller;

import br.com.empadasdabia.dao.CategoriaDAO;
import br.com.empadasdabia.dao.ProdutoDAO;
import br.com.empadasdabia.model.Categoria;
import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.ValidacaoException;
import br.com.empadasdabia.utils.Validador;

import java.util.List;

/** Recebe as ações das telas de Produtos, valida os dados e chama o DAO. */
public class ProdutoController {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    /** Todos os produtos (ativos e inativos), com filtros opcionais. */
    public List<Produto> listar(String nome, Categoria categoria) {
        return produtoDAO.listar(nome, categoria == null ? null : categoria.getId(), false);
    }

    /** Apenas os produtos ativos: é o cardápio usado no Novo Pedido. */
    public List<Produto> listarCardapio(String nome, Categoria categoria) {
        return produtoDAO.listar(nome, categoria == null ? null : categoria.getId(), true);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    /** Cadastra (id nulo) ou atualiza (id preenchido) um produto. */
    public Produto salvar(Integer id, String nome, String descricao, String precoTexto,
                          Categoria categoria, boolean ativo, String imagem) {
        if (categoria == null) {
            throw new ValidacaoException("Selecione a categoria do produto.");
        }
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(Validador.obrigatorio(nome, "Nome", 100));
        produto.setDescricao(Validador.opcional(descricao, "Descrição", 255));
        produto.setPreco(Validador.preco(precoTexto));
        produto.setCategoria(categoria);
        produto.setAtivo(ativo);
        produto.setImagem(imagem);

        if (id == null) {
            produtoDAO.inserir(produto);
        } else {
            produtoDAO.atualizar(produto);
        }
        return produto;
    }

    /** Liga/desliga o produto no cardápio e devolve a nova situação. */
    public boolean alternarAtivo(Produto produto) {
        exigirSelecao(produto);
        boolean novoValor = !produto.isAtivo();
        produtoDAO.alterarAtivo(produto.getId(), novoValor);
        produto.setAtivo(novoValor);
        return novoValor;
    }

    public void excluir(Produto produto) {
        exigirSelecao(produto);
        produtoDAO.excluir(produto.getId());
    }

    private void exigirSelecao(Produto produto) {
        if (produto == null) {
            throw new ValidacaoException("Clique em um produto do cardápio para selecioná-lo.");
        }
    }
}
