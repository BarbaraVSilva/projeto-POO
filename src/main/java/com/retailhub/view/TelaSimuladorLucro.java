package com.retailhub.view;

import com.retailhub.exception.ValidacaoException;
import com.retailhub.model.SimuladorItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaSimuladorLucro extends JPanel {

    // Coleção sem persistência no banco
    private List<SimuladorItem> listaSimulacao = new ArrayList<>();
    
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JTextField txtNomeProduto, txtValorProduto, txtComissao;

    public TelaSimuladorLucro() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Painel Superior (Título, Formulário, Botões)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(UIUtils.COLOR_WHITE);

        // Título Superior
        JLabel lblTitle = new JLabel("Simulador de Lucro", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulário
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBackground(UIUtils.COLOR_WHITE);
        
        formPanel.add(UIUtils.createLabel("Nome do Produto:"));
        txtNomeProduto = new JTextField("Ex: Fone Sem Fio");
        formPanel.add(txtNomeProduto);

        formPanel.add(UIUtils.createLabel("Valor Base (R$):"));
        txtValorProduto = new JTextField("150.00");
        formPanel.add(txtValorProduto);

        formPanel.add(UIUtils.createLabel("Comissão Shopee (%):"));
        txtComissao = new JTextField("20.0"); // Valor padrão da shopee
        formPanel.add(txtComissao);

        topPanel.add(formPanel, BorderLayout.CENTER);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(UIUtils.COLOR_WHITE);
        
        JButton btnAdicionar = UIUtils.createButton("Adicionar à Simulação");
        btnAdicionar.addActionListener(e -> adicionarItem());
        
        JButton btnLimparLista = UIUtils.createButton("Limpar Simulação");
        btnLimparLista.addActionListener(e -> limparLista());

        btnPanel.add(btnAdicionar);
        btnPanel.add(btnLimparLista);
        
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(new String[]{"Produto", "Valor Base", "Taxa (%)", "Desconto (R$)", "Lucro Líquido"}, 0);
        tabela = new JTable(tableModel);
        UIUtils.styleTable(tabela);
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void adicionarItem() {
        try {
            String nome = txtNomeProduto.getText().trim();
            if (nome.isEmpty()) throw new ValidacaoException("O nome do produto é obrigatório!");

            double valor;
            double comissao;
            try {
                valor = Double.parseDouble(txtValorProduto.getText().replace(",", "."));
                comissao = Double.parseDouble(txtComissao.getText().replace(",", "."));
                
                if (valor < 0) throw new ValidacaoException("O valor não pode ser negativo.");
                if (comissao < 0 || comissao > 100) throw new ValidacaoException("A comissão deve ser entre 0 e 100%.");
                
            } catch (NumberFormatException ex) {
                throw new ValidacaoException("Valores numéricos inválidos!");
            }

            SimuladorItem item = new SimuladorItem(nome, valor, comissao);
            listaSimulacao.add(item); // Adiciona na coleção
            
            atualizarTabela();
            
            txtNomeProduto.setText("");
            txtValorProduto.setText("");

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparLista() {
        listaSimulacao.clear();
        atualizarTabela();
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        for (SimuladorItem item : listaSimulacao) {
            tableModel.addRow(new Object[]{
                item.getNomeProduto(),
                String.format("R$ %.2f", item.getValorProduto()),
                String.format("%.1f%%", item.getComissaoShopeePercentual()),
                String.format("- R$ %.2f", item.getValorComissao()),
                String.format("R$ %.2f", item.getLucroLiquido())
            });
        }
    }
}
