package br.com.NatanLima.certificados;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ConversorHtmlParaPdf {

    public static void converter(String htmlContent, String caminhoSaida) {
        try (InputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
             PdfWriter writer = new PdfWriter(new FileOutputStream(caminhoSaida));
             PdfDocument pdfDoc = new PdfDocument(writer)) {

            ConverterProperties props = new ConverterProperties();
            HtmlConverter.convertToPdf(inputStream, pdfDoc, props);
            System.out.println("PDF gerado: " + caminhoSaida);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
