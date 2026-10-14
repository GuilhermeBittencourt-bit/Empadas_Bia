package br.com.empadasdabia.model;

import br.com.empadasdabia.utils.ValidacaoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pedido feito por um cliente. Contém um ou mais itens.
 * As regras de montagem do pedido (somar itens repetidos, calcular total)
 * ficam aqui, no Model, para valerem em qualquer tela.
 */
public class Pedido {

    private Integer id;
    private Cliente cliente;
    private LocalDateTime dataPedido;
    private StatusPedido status = StatusPedido.PENDENTE;
    private String observacao;
    private final List<ItemPedido> itens = new ArrayList<>();

    /**
     * Adiciona um produto ao pedido. Se o produto já estiver no pedido,
     * apenas soma a quantidade em vez de criar uma linha repetida.
     */
    public void adicionarItem(Produto produto, int quantidade) {
        if (produto == null) {
            throw new ValidacaoException("Selecione um produto.");
        }
        if (quantidade <= 0) {
            throw new ValidacaoException("A quantidade deve ser maior que zero.");
        }
        for (ItemPedido item : itens) {
            if (item.getProduto().equals(produto)) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        itens.add(new ItemPedido(produto, quantidade));
    }

    /** Altera a quantidade de um item. Quantidade zero remove o item. */
    public void alterarQuantidade(int indice, int novaQuantidade) {
        if (novaQuantidade <= 0) {
            removerItem(indice);
        } else {
            itens.get(indice).setQuantidade(novaQuantidade);
        }
    }

    public void removerItem(int indice) {
        itens.remove(indice);
    }

    /** Usado pelo DAO ao carregar um pedido já salvo. */
    public void adicionarItemCarregado(ItemPedido item) {
        itens.add(item);
    }

    /** Total = soma dos subtotais dos itens. */
    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public int getQuantidadeTotalItens() {
        int total = 0;
        for (ItemPedido item : itens) {
            total += item.getQuantidade();
        }
        return total;
    }

    /** Quantas unidades de um produto já estão no pedido (usado no cardápio do Novo Pedido). */
    public int quantidadeDoProduto(Produto produto) {
        for (ItemPedido item : itens) {
            if (item.getProduto().equals(produto)) {
                return item.getQuantidade();
            }
        }
        return 0;
    }

    /** Só pedidos pendentes podem ser entregues ou cancelados. */
    public boolean podeAlterarStatus() {
        return status == StatusPedido.PENDENTE;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    /** Lista somente leitura: os itens devem ser alterados pelos métodos acima. */
    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }
}
