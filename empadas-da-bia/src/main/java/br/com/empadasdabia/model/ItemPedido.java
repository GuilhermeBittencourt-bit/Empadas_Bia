package br.com.empadasdabia.model;

import java.math.BigDecimal;

/**
 * Um produto dentro de um pedido.
 * Guarda o preço unitário do momento da venda: se o preço do produto mudar
 * depois, os pedidos antigos continuam com o valor correto.
 */
public class ItemPedido {

    private Integer id;
    private Produto produto;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemPedido() {
    }

    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
    }

    /** Subtotal = quantidade x preço unitário (não é guardado no banco). */
    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
}
