package br.com.empadasdabia.model;

import java.util.Objects;

/** Agrupa os produtos do cardápio (ex.: Empadas Salgadas, Empadas Doces). */
public class Categoria {

    private Integer id;
    private String nome;
    private String descricao;
    /** Preenchido apenas nas listagens, para mostrar quantos produtos a categoria possui. */
    private int quantidadeProdutos;

    public Categoria() {
    }

    public Categoria(Integer id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getQuantidadeProdutos() { return quantidadeProdutos; }
    public void setQuantidadeProdutos(int quantidadeProdutos) { this.quantidadeProdutos = quantidadeProdutos; }

    /** Usado pelos JComboBox para exibir a categoria. */
    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Categoria c && id != null && id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
