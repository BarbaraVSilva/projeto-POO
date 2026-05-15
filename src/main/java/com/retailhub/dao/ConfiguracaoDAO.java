package com.retailhub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracaoDAO {

    public Map<String, String> listarTodas() {
        Map<String, String> configs = new HashMap<>();
        String sql = "SELECT * FROM configuracoes";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                configs.put(rs.getString("chave"), rs.getString("valor"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configs;
    }

    public void salvar(String chave, String valor) {
        String sql = "INSERT OR REPLACE INTO configuracoes (chave, valor) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chave);
            stmt.setString(2, valor);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
