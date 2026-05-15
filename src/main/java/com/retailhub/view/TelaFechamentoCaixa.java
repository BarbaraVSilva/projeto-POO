package com.retailhub.view;

import com.retailhub.dao.ConnectionFactory;
import com.retailhub.model.Sessao;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TelaFechamentoCaixa extends JPanel {

    public TelaFechamentoCaixa() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Fechamento de Caixa Diário", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setOpaque(false);

        // Painel de Totais
        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setBackground(UIUtils.COLOR_WHITE);
        totalsPanel.setBorder(BorderFactory.createTitledBorder("Resumo de Vendas (Hoje)"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;

        double totalDinheiro = 0, totalCartao = 0, totalPix = 0;
        int qtdVendas = 0;

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String sql = "SELECT metodo_pagamento, sum(iv.quantidade * iv.preco_unitario) as total, count(DISTINCT v.id) as qtd " +
                         "FROM vendas v JOIN itens_venda iv ON v.id = iv.venda_id " +
                         "WHERE date(v.data_venda) = date('now') AND v.status_venda = 'CONCLUIDA' " +
                         "GROUP BY metodo_pagamento";
            
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String metodo = rs.getString("metodo_pagamento");
                double total = rs.getDouble("total");
                qtdVendas += rs.getInt("qtd");
                
                if ("DINHEIRO".equals(metodo)) totalDinheiro = total;
                else if ("CARTAO".equals(metodo)) totalCartao = total;
                else if ("PIX".equals(metodo)) totalPix = total;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        gbc.gridy = 0; totalsPanel.add(new JLabel("Vendas em Dinheiro:"), gbc);
        gbc.gridy = 1; totalsPanel.add(new JLabel("Vendas em Cartão:"), gbc);
        gbc.gridy = 2; totalsPanel.add(new JLabel("Vendas em PIX:"), gbc);
        gbc.gridy = 3; totalsPanel.add(new JSeparator(), gbc);
        gbc.gridy = 4; totalsPanel.add(new JLabel("TOTAL GERAL:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 0; totalsPanel.add(new JLabel(String.format("R$ %.2f", totalDinheiro)), gbc);
        gbc.gridy = 1; totalsPanel.add(new JLabel(String.format("R$ %.2f", totalCartao)), gbc);
        gbc.gridy = 2; totalsPanel.add(new JLabel(String.format("R$ %.2f", totalPix)), gbc);
        gbc.gridy = 4; 
        JLabel lblTotal = new JLabel(String.format("R$ %.2f", totalDinheiro + totalCartao + totalPix));
        lblTotal.setFont(UIUtils.FONT_BOLD);
        lblTotal.setForeground(UIUtils.COLOR_SUCCESS);
        totalsPanel.add(lblTotal, gbc);

        mainPanel.add(totalsPanel);

        // Painel de Ação
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        actionPanel.setBackground(UIUtils.COLOR_WHITE);
        actionPanel.setBorder(BorderFactory.createTitledBorder("Concluir Turno"));

        JTextArea txtObs = new JTextArea("Sem observações.");
        txtObs.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        actionPanel.add(new JLabel("Observações de Fechamento:"), BorderLayout.NORTH);
        actionPanel.add(new JScrollPane(txtObs), BorderLayout.CENTER);

        JButton btnFecharCaixa = UIUtils.createButton("Realizar Fechamento e Logoff", UIUtils.COLOR_SUCCESS);
        btnFecharCaixa.setPreferredSize(new Dimension(0, 50));
        btnFecharCaixa.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Deseja realmente fechar o caixa e encerrar a sessão?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                LogService.registrar("Realizou fechamento de caixa. Total: R$ " + String.format("%.2f", totalDinheiro + totalCartao + totalPix));
                JOptionPane.showMessageDialog(this, "Caixa fechado com sucesso! Relatório enviado ao administrador.");
                Window win = SwingUtilities.getWindowAncestor(this);
                if (win instanceof TelaPrincipal) {
                    ((TelaPrincipal) win).dispose();
                    new TelaLogin().setVisible(true);
                }
            }
        });
        actionPanel.add(btnFecharCaixa, BorderLayout.SOUTH);

        mainPanel.add(actionPanel);

        add(mainPanel, BorderLayout.CENTER);
    }
}
