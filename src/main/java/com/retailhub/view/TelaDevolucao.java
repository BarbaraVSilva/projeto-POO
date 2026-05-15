package com.retailhub.view;

import com.retailhub.dao.ConnectionFactory;
import com.retailhub.dao.ProdutoDAO;
import com.retailhub.dao.VendaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class TelaDevolucao extends JPanel {

    private JTextField txtVendaId;
    private JTable tabelaItens;
    private DefaultTableModel model;
    private VendaDAO vendaDAO = new VendaDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    public TelaDevolucao() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("Trocas e Devoluções", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        add(lblTitle, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(new JLabel("ID da Venda:"));
        txtVendaId = new JTextField(10);
        UIUtils.styleTextField(txtVendaId);
        topPanel.add(txtVendaId);
        
        JButton btnBuscar = UIUtils.createButton("Buscar Venda");
        btnBuscar.addActionListener(e -> buscarVenda());
        topPanel.add(btnBuscar);
        
        add(topPanel, BorderLayout.CENTER); // Will be rearranged

        // Re-arranging with a split or nested panels
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID Item", "Produto", "Qtd", "Preço Unit."}, 0);
        tabelaItens = new JTable(model);
        UIUtils.styleTable(tabelaItens);
        centerPanel.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JButton btnDevolver = UIUtils.createButton("Processar Devolução Selecionada", UIUtils.COLOR_DANGER);
        btnDevolver.addActionListener(e -> processarDevolucao());
        add(btnDevolver, BorderLayout.SOUTH);
    }

    private void buscarVenda() {
        String idStr = txtVendaId.getText().trim();
        if (idStr.isEmpty()) return;

        model.setRowCount(0);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT iv.id, p.nome, iv.quantidade, iv.preco_unitario " +
                 "FROM itens_venda iv JOIN produtos p ON iv.produto_id = p.id " +
                 "WHERE iv.venda_id = ?")) {
            
            stmt.setInt(1, Integer.parseInt(idStr));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getInt("quantidade"),
                    rs.getDouble("preco_unitario")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar venda: " + e.getMessage());
        }
    }

    private void processarDevolucao() {
        int row = tabelaItens.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item para devolver.");
            return;
        }

        int itemId = (int) model.getValueAt(row, 0);
        int qtd = (int) model.getValueAt(row, 2);
        String produtoNome = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja confirmar a devolução de " + qtd + " unidade(s) de '" + produtoNome + "'?\nO estoque será restaurado.", 
            "Confirmar Devolução", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = ConnectionFactory.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // 1. Achar o produto_id
                    int produtoId = -1;
                    try (PreparedStatement ps = conn.prepareStatement("SELECT produto_id FROM itens_venda WHERE id = ?")) {
                        ps.setInt(1, itemId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) produtoId = rs.getInt("produto_id");
                    }

                    // 2. Restaurar estoque
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE produtos SET quantidade_estoque = quantidade_estoque + ? WHERE id = ?")) {
                        ps.setInt(1, qtd);
                        ps.setInt(2, produtoId);
                        ps.executeUpdate();
                    }

                    // 3. Remover item da venda (ou marcar como devolvido se houvesse coluna)
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM itens_venda WHERE id = ?")) {
                        ps.setInt(1, itemId);
                        ps.executeUpdate();
                    }

                    conn.commit();
                    LogService.registrar("Processou devolução do item ID " + itemId + " (" + produtoNome + ")");
                    JOptionPane.showMessageDialog(this, "Devolução processada com sucesso!");
                    buscarVenda();
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao processar devolução: " + e.getMessage());
            }
        }
    }
}
