package br.com.empadasdabia.model;

/** Situações possíveis de um pedido. Os nomes são os mesmos do ENUM no banco. */
public enum StatusPedido {
    PENDENTE("Pendente"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
