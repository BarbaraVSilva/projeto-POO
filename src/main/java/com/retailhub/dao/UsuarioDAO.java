package com.retailhub.dao;

import com.retailhub.exception.PersistenciaException;
import com.retailhub.model.Usuario;
import com.retailhub.util.SecurityUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashSalvo = rs.getString("senha");
                    if (SecurityUtils.checkPassword(senha, hashSalvo)) {
                        return new Usuario(
                            rs.getInt("id"),
                            rs.getString("login"),
                            hashSalvo,
                            rs.getString("perfil"),
                            rs.getInt("status_ativo"),
                            rs.getString("data_ultima_senha")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao autenticar usuário.", e);
        }
        return null;
    }

    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (login, senha, perfil, status_ativo, data_ultima_senha) VALUES (?, ?, ?, ?, CURRENT_DATE)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, SecurityUtils.hashPassword(usuario.getSenha()));
            stmt.setString(3, usuario.getPerfil());
            stmt.setInt(4, usuario.getStatusAtivo());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }

    public void atualizar(Usuario usuario) {
        boolean updateSenha = usuario.getSenha() != null && !usuario.getSenha().trim().isEmpty();
        String sql = updateSenha ? 
            "UPDATE usuarios SET login = ?, senha = ?, perfil = ?, status_ativo = ?, data_ultima_senha = CURRENT_DATE WHERE id = ?" :
            "UPDATE usuarios SET login = ?, perfil = ?, status_ativo = ? WHERE id = ?";
            
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int index = 1;
            stmt.setString(index++, usuario.getLogin());
            if (updateSenha) {
                stmt.setString(index++, SecurityUtils.hashPassword(usuario.getSenha()));
            }
            stmt.setString(index++, usuario.getPerfil());
            stmt.setInt(index++, usuario.getStatusAtivo());
            stmt.setInt(index++, usuario.getId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    public java.util.List<Usuario> listarTodos() {
        java.util.List<Usuario> usuarios = new java.util.ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("perfil"),
                    rs.getInt("status_ativo"),
                    rs.getString("data_ultima_senha")
                ));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return usuarios;
    }
}
