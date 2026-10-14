package br.com.empadasdabia.model;

import br.com.empadasdabia.utils.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testa as regras do pedido que ficam no Model (não precisa de banco de dados). */
class PedidoTest {

    private Produto frango;
    private Produto doceDeLeite;

    @BeforeEach
    void criarProdutos() {
        frango = produto(1, "Empada de Frango", "8.00");
        doceDeLeite = produto(6, "Empada Doce de Leite", "9.00");
    }

    @Test
    void calculaSubtotalETotal() {
        Pedido pedido = new Pedido();
        pedido.adicionarItem(frango, 3);
        pedido.adicionarItem(doceDeLeite, 2);

        assertEquals(new BigDecimal("24.00"), pedido.getItens().get(0).getSubtotal());
        assertEquals(new BigDecimal("42.00"), pedido.calcularTotal());
        assertEquals(5, pedido.getQuantidadeTotalItens());
    }

    @Test
    void somaQuantidadeQuandoProdutoJaEstaNoPedido() {
        Pedido pedido = new Pedido();
        pedido.adicionarItem(frango, 1);
        pedido.adicionarItem(frango, 2);

        assertEquals(1, pedido.getItens().size());
        assertEquals(3, pedido.quantidadeDoProduto(frango));
    }

    @Test
    void guardaPrecoDoMomentoDaVenda() {
        Pedido pedido = new Pedido();
        pedido.adicionarItem(frango, 2);
        frango.setPreco(new BigDecimal("10.00"));

        assertEquals(new BigDecimal("16.00"), pedido.calcularTotal());
    }

    @Test
    void quantidadeZeroRemoveOItem() {
        Pedido pedido = new Pedido();
        pedido.adicionarItem(frango, 2);
        pedido.alterarQuantidade(0, 0);

        assertTrue(pedido.getItens().isEmpty());
        assertEquals(BigDecimal.ZERO, pedido.calcularTotal());
    }

    @Test
    void naoAceitaQuantidadeInvalida() {
        Pedido pedido = new Pedido();
        assertThrows(ValidacaoException.class, () -> pedido.adicionarItem(frango, 0));
        assertThrows(ValidacaoException.class, () -> pedido.adicionarItem(null, 1));
    }

    @Test
    void apenasPedidoPendentePodeMudarDeStatus() {
        Pedido pedido = new Pedido();
        assertTrue(pedido.podeAlterarStatus());
        pedido.setStatus(StatusPedido.ENTREGUE);
        assertFalse(pedido.podeAlterarStatus());
    }

    private static Produto produto(int id, String nome, String preco) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setPreco(new BigDecimal(preco));
        return produto;
    }
}
