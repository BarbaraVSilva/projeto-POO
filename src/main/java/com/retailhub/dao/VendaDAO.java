package com.retailhub.dao;

import com.retailhub.exception.PersistenciaException;
import com.retailhub.model.Venda;
import com.retailhub.model.ItemVenda;
import com.retailhub.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendaDAO {

    public void registrarVenda(Venda venda) {
        String sqlVenda = "INSERT INTO vendas (cliente_id, cupom_id, usuario_id, status_venda, status_logistica, metodo_pagamento) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO itens_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        String sqlEstoque = "UPDATE produtos SET quantidade_estoque = quantidade_estoque - ? WHERE id = ? AND quantidade_estoque >= ?";
        
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia Transação
            
            int vendaId = 0;
            // Salvar Venda
            try (PreparedStatement stmt = conn.prepareStatement(sqlVenda, PreparedStatement.RETURN_GENERATED_KEYS)) {
                if (venda.getCliente() != null) stmt.setInt(1, venda.getCliente().getId()); else stmt.setNull(1, java.sql.Types.INTEGER);
                if (venda.getCupom() != null) stmt.setInt(2, venda.getCupom().getId()); else stmt.setNull(2, java.sql.Types.INTEGER);
                if (venda.getVendedor() != null) stmt.setInt(3, venda.getVendedor().getId()); else stmt.setNull(3, java.sql.Types.INTEGER);
                stmt.setString(4, venda.getStatusVenda());
                stmt.setString(5, venda.getStatusLogistica());
                stmt.setString(6, venda.getMetodoPagamento());
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        vendaId = rs.getInt(1);
                        venda.setId(vendaId);
                    }
                }
            }
            
            // Salvar Itens e Baixar Estoque
            try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
                 PreparedStatement stmtEstoque = conn.prepareStatement(sqlEstoque)) {
                 
                for (ItemVenda item : venda.getItens()) {
                    // Item
                    stmtItem.setInt(1, vendaId);
                    stmtItem.setInt(2, item.getProduto().getId());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getPrecoUnitario());
                    stmtItem.executeUpdate();
                    
                    // Estoque
                    stmtEstoque.setInt(1, item.getQuantidade());
                    stmtEstoque.setInt(2, item.getProduto().getId());
                    stmtEstoque.setInt(3, item.getQuantidade());
                    int affected = stmtEstoque.executeUpdate();
                    if (affected == 0) {
                        throw new SQLException("Estoque insuficiente para o produto ID: " + item.getProduto().getId());
                    }
                }
            }
            
            conn.commit(); // Confirma Transação
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            throw new PersistenciaException("Erro ao registrar venda: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
            }
        }
    }

    public java.util.List<Venda> listarComFiltros(String buscaProduto, String buscaVendedor, String buscaData) {
        java.util.List<Venda> vendas = new java.util.ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT v.id as venda_id, v.data_venda, v.status_venda, v.status_logistica, v.metodo_pagamento, " +
            "c.id as cli_id, c.nome as cli_nome, " +
            "cup.id as cupom_id, cup.codigo as cupom_codigo, cup.desconto_percentual, " +
            "u.id as user_id, u.login as user_login " +
            "FROM vendas v " +
            "LEFT JOIN clientes c ON v.cliente_id = c.id " +
            "LEFT JOIN cupons cup ON v.cupom_id = cup.id " +
            "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
            "WHERE 1=1 "
        );
        
        if (buscaProduto != null && !buscaProduto.trim().isEmpty()) {
            sql.append("AND v.id IN (SELECT iv.venda_id FROM itens_venda iv JOIN produtos p ON iv.produto_id = p.id WHERE p.nome LIKE ?) ");
        }
        if (buscaVendedor != null && !buscaVendedor.trim().isEmpty()) {
            sql.append("AND u.login LIKE ? ");
        }
        if (buscaData != null && !buscaData.trim().isEmpty()) {
            sql.append("AND v.data_venda LIKE ? ");
        }
        sql.append("ORDER BY v.data_venda DESC");
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
             
            int index = 1;
            if (buscaProduto != null && !buscaProduto.trim().isEmpty()) {
                stmt.setString(index++, "%" + buscaProduto.trim() + "%");
            }
            if (buscaVendedor != null && !buscaVendedor.trim().isEmpty()) {
                stmt.setString(index++, "%" + buscaVendedor.trim() + "%");
            }
            if (buscaData != null && !buscaData.trim().isEmpty()) {
                stmt.setString(index++, buscaData.trim() + "%");
            }
             
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venda venda = new Venda();
                    venda.setId(rs.getInt("venda_id"));
                    venda.setDataVenda(rs.getString("data_venda"));
                    venda.setStatusVenda(rs.getString("status_venda"));
                    venda.setStatusLogistica(rs.getString("status_logistica"));
                    venda.setMetodoPagamento(rs.getString("metodo_pagamento"));
                    
                    if (rs.getInt("cli_id") > 0) {
                        Cliente cliente = new Cliente();
                        cliente.setId(rs.getInt("cli_id"));
                        cliente.setNome(rs.getString("cli_nome"));
                        venda.setCliente(cliente);
                    }
                    
                    if (rs.getInt("cupom_id") > 0) {
                        com.retailhub.model.Cupom cupom = new com.retailhub.model.Cupom();
                        cupom.setId(rs.getInt("cupom_id"));
                        cupom.setCodigo(rs.getString("cupom_codigo"));
                        cupom.setDescontoPercentual(rs.getDouble("desconto_percentual"));
                        venda.setCupom(cupom);
                    }
                    
                    if (rs.getInt("user_id") > 0) {
                        com.retailhub.model.Usuario vendedor = new com.retailhub.model.Usuario(
                            rs.getInt("user_id"), rs.getString("user_login"), "", "", 1
                        );
                        venda.setVendedor(vendedor);
                    }
                    
                    // Buscar Itens da Venda com informações da Categoria
                    String sqlItens = "SELECT iv.quantidade, iv.preco_unitario, p.nome, c.taxa_comissao " +
                                     "FROM itens_venda iv " +
                                     "JOIN produtos p ON iv.produto_id = p.id " +
                                     "JOIN categorias c ON p.categoria_id = c.id " +
                                     "WHERE iv.venda_id = ?";
                    try (PreparedStatement stmtItens = conn.prepareStatement(sqlItens)) {
                        stmtItens.setInt(1, venda.getId());
                        try (ResultSet rsItens = stmtItens.executeQuery()) {
                            while (rsItens.next()) {
                                com.retailhub.model.Categoria cat = new com.retailhub.model.Categoria();
                                cat.setTaxaComissao(rsItens.getDouble("taxa_comissao"));
                                
                                com.retailhub.model.Produto p = new com.retailhub.model.ProdutoFisico();
                                p.setNome(rsItens.getString("nome"));
                                p.setCategoria(cat);
                                
                                ItemVenda item = new ItemVenda(p, rsItens.getInt("quantidade"), rsItens.getDouble("preco_unitario"));
                                venda.adicionarItem(item);
                            }
                        }
                    }
                    
                    vendas.add(venda);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar histórico de vendas: " + e.getMessage(), e);
        }
        return vendas;
    }

    public java.util.Map<String, Double> getVendasPorDia() {
        java.util.Map<String, Double> dados = new java.util.LinkedHashMap<>();
        String sql = "SELECT date(data_venda) as dia, SUM(p.preco * iv.quantidade) as total " +
                     "FROM vendas v " +
                     "JOIN itens_venda iv ON v.id = iv.venda_id " +
                     "JOIN produtos p ON iv.produto_id = p.id " +
                     "GROUP BY dia ORDER BY dia ASC LIMIT 10";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dados.put(rs.getString("dia"), rs.getDouble("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dados;
    }

    public java.util.Map<String, Integer> getVendasPorCategoria() {
        java.util.Map<String, Integer> dados = new java.util.HashMap<>();
        String sql = "SELECT c.nome, SUM(iv.quantidade) as qtd " +
                     "FROM itens_venda iv " +
                     "JOIN produtos p ON iv.produto_id = p.id " +
                     "JOIN categorias c ON p.categoria_id = c.id " +
                     "GROUP BY c.nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dados.put(rs.getString("nome"), rs.getInt("qtd"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dados;
    }
}
