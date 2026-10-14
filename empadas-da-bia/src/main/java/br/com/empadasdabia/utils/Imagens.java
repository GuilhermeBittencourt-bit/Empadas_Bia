package br.com.empadasdabia.utils;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Carrega as imagens dos produtos.
 * Procura primeiro na pasta "imagens" ao lado do sistema (fotos escolhidas pelo usuário)
 * e depois nas imagens que já vêm no projeto (src/main/resources/imagens).
 */
public final class Imagens {

    public static final String PADRAO = "padrao.png";
    private static final Path PASTA_USUARIO = Path.of("imagens");
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    private Imagens() {
    }

    /** Devolve a imagem redimensionada para caber em largura x altura, mantendo a proporção. */
    public static ImageIcon carregar(String nomeArquivo, int largura, int altura) {
        String nome = (nomeArquivo == null || nomeArquivo.isBlank()) ? PADRAO : nomeArquivo;
        String chave = nome + "@" + largura + "x" + altura;
        return CACHE.computeIfAbsent(chave, k -> redimensionar(lerImagem(nome), largura, altura));
    }

    /** Versão acinzentada, usada para produtos inativos. */
    public static ImageIcon cinza(ImageIcon icone) {
        return new ImageIcon(GrayFilter.createDisabledImage(icone.getImage()));
    }

    /**
     * Copia a imagem escolhida pelo usuário para a pasta "imagens" do sistema
     * e devolve o nome do arquivo, que é o que fica gravado no banco.
     */
    public static String copiarParaPastaDoSistema(File arquivo) {
        try {
            Files.createDirectories(PASTA_USUARIO);
            String nome = arquivo.getName().toLowerCase().replaceAll("[^a-z0-9._-]", "-");
            Path destino = PASTA_USUARIO.resolve(nome);
            if (Files.exists(destino) && !Files.isSameFile(destino, arquivo.toPath())) {
                nome = System.currentTimeMillis() + "-" + nome;
                destino = PASTA_USUARIO.resolve(nome);
            }
            Files.copy(arquivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            String nomeFinal = nome;
            CACHE.keySet().removeIf(chave -> chave.startsWith(nomeFinal + "@"));
            return nome;
        } catch (IOException e) {
            throw new ValidacaoException("Não foi possível copiar a imagem: " + e.getMessage());
        }
    }

    private static BufferedImage lerImagem(String nome) {
        try {
            Path arquivoUsuario = PASTA_USUARIO.resolve(nome);
            if (Files.exists(arquivoUsuario)) {
                BufferedImage imagem = ImageIO.read(arquivoUsuario.toFile());
                if (imagem != null) {
                    return imagem;
                }
            }
            try (InputStream entrada = Imagens.class.getResourceAsStream("/imagens/" + nome)) {
                if (entrada != null) {
                    return ImageIO.read(entrada);
                }
            }
        } catch (IOException e) {
            // Se a imagem estiver corrompida, usa a imagem padrão abaixo
        }
        if (PADRAO.equals(nome)) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        return lerImagem(PADRAO);
    }

    private static ImageIcon redimensionar(BufferedImage original, int largura, int altura) {
        double escala = Math.min((double) largura / original.getWidth(), (double) altura / original.getHeight());
        int novaLargura = Math.max(1, (int) Math.round(original.getWidth() * escala));
        int novaAltura = Math.max(1, (int) Math.round(original.getHeight() * escala));
        return new ImageIcon(original.getScaledInstance(novaLargura, novaAltura, Image.SCALE_SMOOTH));
    }
}
