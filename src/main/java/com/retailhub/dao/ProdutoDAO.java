package com.retailhub.dao;

import com.retailhub.exception.PersistenciaException;
import com.retailhub.model.Categoria;
import com.retailhub.model.Produto;
import com.retailhub.model.ProdutoDigital;
import com.retailhub.model.ProdutoFisico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.retailhub.exception.SkuDuplicadoException;

public class ProdutoDAO {

    public void inserir(Produto produto) {
        if (skuExiste(produto.getSku(), 0)) {
            throw new SkuDuplicadoException("O SKU '" + produto.getSku() + "' já está cadastrado em outro produto!");
        }
        String sql = "INSERT INTO produtos (nome, sku, descricao, preco, preco_costo, categoria_id, tipo_produto, peso, frete, quantidade_estoque, imagem_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getSku());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setDouble(5, produto.getPrecoCusto());
            stmt.setInt(6, produto.getCategoria().getId());
            
            if (produto instanceof ProdutoFisico) {
                ProdutoFisico pf = (ProdutoFisico) produto;
                stmt.setString(7, "FISICO");
                stmt.setDouble(8, pf.getPeso());
                stmt.setDouble(9, pf.getFrete());
            } else {
                stmt.setString(7, "DIGITAL");
                stmt.setDouble(8, 0.0);
                stmt.setDouble(9, 0.0);
            }
            
            stmt.setInt(10, produto.getQuantidadeEstoque());
            stmt.setString(11, produto.getImagemPath());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir produto: " + e.getMessage(), e);
        }
    }

    public void atualizar(Produto produto) {
        if (skuExiste(produto.getSku(), produto.getId())) {
            throw new SkuDuplicadoException("O SKU '" + produto.getSku() + "' já está sendo usado por outro produto!");
        }
        String sql = "UPDATE produtos SET nome = ?, sku = ?, descricao = ?, preco = ?, preco_costo = ?, categoria_id = ?, tipo_produto = ?, peso = ?, frete = ?, quantidade_estoque = ?, imagem_path = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getSku());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setDouble(5, produto.getPrecoCusto());
            stmt.setInt(6, produto.getCategoria().getId());
            
            if (produto instanceof ProdutoFisico) {
                ProdutoFisico pf = (ProdutoFisico) produto;
                stmt.setString(7, "FISICO");
                stmt.setDouble(8, pf.getPeso());
                stmt.setDouble(9, pf.getFrete());
            } else {
                stmt.setString(7, "DIGITAL");
                stmt.setDouble(8, 0.0);
                stmt.setDouble(9, 0.0);
            }
            
            stmt.setInt(10, produto.getQuantidadeEstoque());
            stmt.setString(11, produto.getImagemPath());
            stmt.setInt(12, produto.getId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as categoria_nome, c.descricao as categoria_descricao, " +
                     "c.status_ativo, c.taxa_comissao, c.prazo_envio, c.data_cadastro " +
                     "FROM produtos p JOIN categorias c ON p.categoria_id = c.id";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Categoria c = new Categoria(
                    rs.getInt("categoria_id"),
                    rs.getString("categoria_nome"),
                    rs.getString("categoria_descricao"),
                    rs.getInt("status_ativo"),
                    rs.getDouble("taxa_comissao"),
                    rs.getInt("prazo_envio"),
                    rs.getString("data_cadastro")
                );
                
                String tipo = rs.getString("tipo_produto");
                Produto p;
                
                if ("FISICO".equals(tipo)) {
                    p = new ProdutoFisico(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("sku"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getDouble("preco_costo"),
                        c,
                        rs.getDouble("peso"),
                        rs.getDouble("frete"),
                        rs.getInt("quantidade_estoque"),
                        rs.getString("imagem_path")
                    );
                } else {
                    p = new ProdutoDigital(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("sku"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getDouble("preco_costo"),
                        c,
                        rs.getInt("quantidade_estoque"),
                        rs.getString("imagem_path")
                    );
                }
                produtos.add(p);
            }
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return produtos;
    }

    public void baixarEstoque(int idProduto, int quantidade) {
        String sql = "UPDATE produtos SET quantidade_estoque = quantidade_estoque - ? WHERE id = ? AND quantidade_estoque >= ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantidade);
            stmt.setInt(2, idProduto);
            stmt.setInt(3, quantidade);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new PersistenciaException("Estoque insuficiente ou produto não encontrado para o ID: " + idProduto);
            }
            
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao baixar estoque: " + e.getMessage(), e);
        }
    }

    private boolean skuExiste(String sku, int idParaIgnorar) {
        if (sku == null || sku.trim().isEmpty()) return false;
        String sql = "SELECT id FROM produtos WHERE sku = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sku);
            stmt.setInt(2, idParaIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
