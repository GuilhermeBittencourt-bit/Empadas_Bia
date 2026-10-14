package br.com.empadasdabia.model;

import br.com.empadasdabia.utils.Formatador;

import java.util.Objects;

/** Cliente da Empadas da Bia. O endereço fica na própria classe (um endereço por cliente). */
public class Cliente {

    private Integer id;
    private String nome;
    /** Apenas números, ex.: 47998123344 */
    private String telefone;
    private String email;
    /** Apenas números, ex.: 89201000 */
    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    /** Monta o endereço em uma linha, ignorando partes não preenchidas. */
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (temTexto(logradouro)) {
            sb.append(logradouro);
            if (temTexto(numero)) {
                sb.append(", ").append(numero);
            }
        }
        if (temTexto(bairro)) {
            sb.append(sb.isEmpty() ? "" : " – ").append(bairro);
        }
        if (temTexto(cidade)) {
            sb.append(sb.isEmpty() ? "" : " – ").append(cidade);
        }
        return sb.isEmpty() ? "Endereço não informado" : sb.toString();
    }

    private static boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    /** Usado no JComboBox do Novo Pedido. */
    @Override
    public String toString() {
        return nome + " – " + Formatador.telefone(telefone);
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Cliente c && id != null && id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
