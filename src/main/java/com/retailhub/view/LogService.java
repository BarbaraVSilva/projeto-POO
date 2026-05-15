package com.retailhub.view;

import com.retailhub.model.Sessao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogService {

    public static void registrar(String acao) {
        try (FileWriter fw = new FileWriter("auditoria.log", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
             
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            String user = "SISTEMA";
            if (Sessao.getUsuarioLogado() != null) {
                user = Sessao.getUsuarioLogado().getLogin().toUpperCase();
            }
            
            String linhaLog = String.format("[%s] | USUARIO: %s | ACAO: %s", dataHora, user, acao);
            bw.write(linhaLog);
            bw.newLine();
            
        } catch (IOException e) {
            System.err.println("Erro ao gravar log: " + e.getMessage());
        }
    }
}
