package br.com.empadasdabia.model;

import java.math.BigDecimal;
import java.util.Objects;

/** Um item do cardápio: uma empada ou um combo. */
public class Produto {

    private Integer id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private boolean ativo = true;
    /** Nome do arquivo da imagem (ex.: empada-frango.png). */
    private String imagem;
    private Categoria categoria;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Produto p && id != null && id.equals(p.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
