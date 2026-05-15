package com.retailhub.dao;

import com.retailhub.exception.PersistenciaException;
import com.retailhub.model.Cupom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CupomDAO {

    public List<Cupom> listarAtivos() {
        List<Cupom> cupons = new ArrayList<>();
        String sql = "SELECT * FROM cupons WHERE status_ativo = 1";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cupons.add(new Cupom(
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getDouble("desconto_percentual"),
                    rs.getInt("status_ativo")
                ));
            }
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar cupons: " + e.getMessage(), e);
        }
        return cupons;
    }
}
