package br.com.NatanLima.certificados;

/**
 * Representa uma linha/registro da planilha de participantes.
 * Esta versão fornece construtor vazio + getters/setters para compatibilidade
 * com LeitorPlanilhas e GeradorCertificados.
 */
public class Registro {

    private int linha; // índice da linha na planilha (0-based)
    private String nomeCompleto;
    private String email;
    private String nomeDoCurso;
    private String cargaHoraria;
    private String dataFinal; // formato esperado: dd/MM/yyyy ou yyyy-MM-dd dependendo do seu fluxo
    private String localDaAula;
    private String certificadoEnviado;

    // Construtor sem argumentos (necessário para uso com setters)
    public Registro() {
    }

    // Construtor completo (opcional)
    public Registro(int linha, String nomeCompleto, String email, String nomeDoCurso,
                    String cargaHoraria, String dataFinal, String localDaAula, String certificadoEnviado) {
        this.linha = linha;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.nomeDoCurso = nomeDoCurso;
        this.cargaHoraria = cargaHoraria;
        this.dataFinal = dataFinal;
        this.localDaAula = localDaAula;
        this.certificadoEnviado = certificadoEnviado;
    }

    // getters / setters
    public int getLinha() { return linha; }
    public void setLinha(int linha) { this.linha = linha; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomeDoCurso() { return nomeDoCurso; }
    public void setNomeDoCurso(String nomeDoCurso) { this.nomeDoCurso = nomeDoCurso; }

    public String getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(String cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public String getDataFinal() { return dataFinal; }
    public void setDataFinal(String dataFinal) { this.dataFinal = dataFinal; }

    public String getLocalDaAula() { return localDaAula; }
    public void setLocalDaAula(String localDaAula) { this.localDaAula = localDaAula; }

    public String getCertificadoEnviado() { return certificadoEnviado; }
    public void setCertificadoEnviado(String certificadoEnviado) { this.certificadoEnviado = certificadoEnviado; }

    // aliases úteis (caso outras classes usem nomes diferentes)
    public String getNome() { return getNomeCompleto(); }
    public String getCurso() { return getNomeDoCurso(); }
    public String getDataFimCurso() { return getDataFinal(); }

    @Override
    public String toString() {
        return "Registro{" +
                "linha=" + linha +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", email='" + email + '\'' +
                ", nomeDoCurso='" + nomeDoCurso + '\'' +
                ", cargaHoraria='" + cargaHoraria + '\'' +
                ", dataFinal='" + dataFinal + '\'' +
                ", localDaAula='" + localDaAula + '\'' +
                ", certificadoEnviado='" + certificadoEnviado + '\'' +
                '}';
    }
}
