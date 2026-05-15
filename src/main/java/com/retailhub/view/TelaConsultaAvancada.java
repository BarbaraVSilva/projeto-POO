package com.retailhub.view;

import com.retailhub.dao.CategoriaDAO;
import com.retailhub.dao.ProdutoDAO;
import com.retailhub.model.Categoria;
import com.retailhub.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TelaConsultaAvancada extends JPanel {

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private List<Produto> todosProdutos;
    
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JComboBox<String> cbCategoriaFiltro;
    private JComboBox<String> cbFaixaPreco;

    public TelaConsultaAvancada() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        todosProdutos = produtoDAO.listarTodos();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Título Superior
        JLabel lblTitle = new JLabel("Consulta Avançada de Produtos", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Painel de Filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(UIUtils.COLOR_WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros de Busca Avançada"));

        filterPanel.add(UIUtils.createLabel("Categoria:"));
        cbCategoriaFiltro = new JComboBox<>();
        cbCategoriaFiltro.addItem("Todas");
        for (Categoria c : categoriaDAO.listarTodas()) {
            cbCategoriaFiltro.addItem(c.getNome());
        }
        filterPanel.add(cbCategoriaFiltro);

        filterPanel.add(UIUtils.createLabel("Faixa de Preço:"));
        cbFaixaPreco = new JComboBox<>(new String[]{
            "Qualquer Preço",
            "Até R$ 50,00",
            "R$ 50,01 a R$ 200,00",
            "Acima de R$ 200,00"
        });
        filterPanel.add(cbFaixaPreco);

        JButton btnFiltrar = UIUtils.createButton("Aplicar Filtros");
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        filterPanel.add(btnFiltrar);

        JButton btnExportar = UIUtils.createButton("Exportar CSV");
        btnExportar.addActionListener(e -> exportarParaCSV());
        filterPanel.add(btnExportar);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(new String[]{"ID", "Produto", "Categoria", "Preço (R$)", "Status Categoria"}, 0);
        tabela = new JTable(tableModel);
        UIUtils.styleTable(tabela);
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        
        // Carrega tabela inicial sem filtros
        atualizarTabela(todosProdutos);
    }

    private void aplicarFiltros() {
        String categoriaSelecionada = cbCategoriaFiltro.getSelectedItem().toString();
        int indicePreco = cbFaixaPreco.getSelectedIndex();

        // Utilizando Streams e Collections do Java para filtrar em memória (conforme requisito)
        List<Produto> filtrados = todosProdutos.stream()
            .filter(p -> {
                // Filtro de Categoria
                if (!categoriaSelecionada.equals("Todas")) {
                    return p.getCategoria().getNome().equals(categoriaSelecionada);
                }
                return true;
            })
            .filter(p -> {
                // Filtro de Preço
                double preco = p.getPreco();
                switch (indicePreco) {
                    case 1: return preco <= 50.0;
                    case 2: return preco > 50.0 && preco <= 200.0;
                    case 3: return preco > 200.0;
                    default: return true;
                }
            })
            .collect(Collectors.toList());

        atualizarTabela(filtrados);
    }

    private void exportarParaCSV() {
        try {
            java.io.File file = new java.io.File("relatorio_produtos.csv");
            try (java.io.FileWriter fw = new java.io.FileWriter(file);
                 java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
                 
                bw.write("ID;Produto;Categoria;Preco;Status Categoria\n");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        bw.write(tableModel.getValueAt(i, j).toString() + ";");
                    }
                    bw.write("\n");
                }
            }
            JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso em: " + file.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar: " + e.getMessage());
        }
    }

    private void atualizarTabela(List<Produto> lista) {
        tableModel.setRowCount(0);
        for (Produto p : lista) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getNome(),
                p.getCategoria().getNome(),
                String.format("%.2f", p.getPreco()),
                p.getCategoria().getStatusAtivo() == 1 ? "Ativa" : "Inativa"
            });
        }
    }
}
