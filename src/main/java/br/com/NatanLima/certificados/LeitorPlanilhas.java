package br.com.NatanLima.certificados;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe responsável por ler e atualizar os dados da planilha Excel.
 * ESTA É A VERSÃO FINAL CORRIGIDA.
 */
public class LeitorPlanilhas {

    public static List<Registro> lerPlanilha(String caminhoArquivo) {
        List<Registro> registros = new ArrayList<>();
        // Usamos um try-with-resources para garantir que o ficheiro é fechado após a leitura
        try (FileInputStream file = new FileInputStream(new File(caminhoArquivo));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            // Pular cabeçalho
            if (iterator.hasNext()) iterator.next();

            while (iterator.hasNext()) {
                Row row = iterator.next();
                Registro r = new Registro();

                // Os valores são lidos e limpos pelo getValorCelula()
                r.setNomeCompleto(getValorCelula(row.getCell(0)));         // Coluna A
                r.setEmail(getValorCelula(row.getCell(1)));                // Coluna B
                r.setNomeDoCurso(getValorCelula(row.getCell(2)));          // Coluna C
                r.setCargaHoraria(getValorCelula(row.getCell(3)));         // Coluna D
                r.setDataFinal(getValorCelula(row.getCell(4)));            // Coluna E
                r.setLocalDaAula(getValorCelula(row.getCell(5)));          // Coluna F
                r.setCertificadoEnviado(getValorCelula(row.getCell(6)));   // Coluna G

                r.setLinha(row.getRowNum()); // Guarda o número da linha
                registros.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao LER a planilha: " + e.getMessage());
        }
        return registros;
    }

    /**
     * Atualiza a coluna "Certificado Enviado" (G) para um registro específico.
     * ESTA É A VERSÃO FINAL CORRIGIDA (lê primeiro, depois escreve).
     */
    public static void atualizarStatusEnvio(String caminhoArquivo, Registro registro) {
        File file = new File(caminhoArquivo);
        Workbook workbook;

        // --- PASSO 1: LER O FICHEIRO ---
        try (FileInputStream in = new FileInputStream(file)) {
            workbook = new XSSFWorkbook(in);
        } catch (Exception e) {
            System.err.println("❌ Erro ao LER a planilha para atualização: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // --- PASSO 2: MODIFICAR NA MEMÓRIA ---
        try {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(registro.getLinha());
            if (row == null) {
                row = sheet.createRow(registro.getLinha());
            }
            Cell cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellValue(registro.getCertificadoEnviado());

        } catch (Exception e) {
            System.err.println("❌ Erro ao MODIFICAR a planilha na memória: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // --- PASSO 3: ESCREVER AS ALTERAÇÕES ---
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        } catch (Exception e) {
            System.err.println("❌ Erro ao ESCREVER na planilha. O ficheiro está aberto no Excel?");
            e.printStackTrace();
            return;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) { /* ignora */ }
        }

        System.out.println("📝 Planilha atualizada para: " + registro.getNomeCompleto());
    }


    /**
     * Método auxiliar para pegar o valor da célula como String.
     * ESTA É A VERSÃO FINAL CORRIGIDA (faz .trim() em tudo).
     */
    private static String getValorCelula(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim(); // A CORREÇÃO PRINCIPAL

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    long num = (long) cell.getNumericCellValue();
                    return String.valueOf(num);
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    try {
                        long num = (long) cell.getNumericCellValue();
                        return String.valueOf(num);
                    } catch (Exception e2) {
                        return cell.getCellFormula().trim();
                    }
                }

            case BLANK:
                return "";

            default:
                return "";
        }
    }
}