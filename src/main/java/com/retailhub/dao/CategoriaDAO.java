package com.retailhub.dao;

import com.retailhub.exception.PersistenciaException;
import com.retailhub.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void inserir(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, descricao, status_ativo, taxa_comissao, prazo_envio, data_cadastro) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getStatusAtivo());
            stmt.setDouble(4, categoria.getTaxaComissao());
            stmt.setInt(5, categoria.getPrazoEnvio());
            stmt.setString(6, categoria.getDataCadastro());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir categoria: " + e.getMessage(), e);
        }
    }

    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, descricao = ?, status_ativo = ?, taxa_comissao = ?, prazo_envio = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getStatusAtivo());
            stmt.setDouble(4, categoria.getTaxaComissao());
            stmt.setInt(5, categoria.getPrazoEnvio());
            stmt.setInt(6, categoria.getId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir categoria: " + e.getMessage(), e);
        }
    }

    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Categoria c = new Categoria(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getInt("status_ativo"),
                    rs.getDouble("taxa_comissao"),
                    rs.getInt("prazo_envio"),
                    rs.getString("data_cadastro")
                );
                categorias.add(c);
            }
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar categorias: " + e.getMessage(), e);
        }
        return categorias;
    }
}
