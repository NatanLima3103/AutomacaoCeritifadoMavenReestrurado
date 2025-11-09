package br.com.NatanLima.certificados;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConversorHtmlParaPdf {

    public static void converter(String html, String caminhoSaidaPdf) throws IOException {
        try (FileOutputStream os = new FileOutputStream(new File(caminhoSaidaPdf))) {

            // ✅ CORREÇÃO 2: Encontrar o caminho para a pasta 'resources'
            // Isso permite que o HTML encontre o 'fundo_certificado.png'
            java.net.URI baseUri = ConversorHtmlParaPdf.class.getClassLoader().getResource(".").toURI();

            PdfRendererBuilder builder = new PdfRendererBuilder();

            // Passamos a 'baseUri' para ele saber onde procurar imagens
            builder.withHtmlContent(html, baseUri.toString());

            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            throw new IOException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }
}