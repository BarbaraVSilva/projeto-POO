package com.retailhub.view;

import com.retailhub.dao.VendaDAO;
import com.retailhub.model.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaHistoricoVendas extends JPanel {

    private VendaDAO vendaDAO = new VendaDAO();
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JTextField txtFiltroProduto;
    private JTextField txtFiltroVendedor;
    private JTextField txtFiltroData;

    public TelaHistoricoVendas() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Painel Superior (Título + Filtros)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIUtils.COLOR_WHITE);
        
        JLabel lblTitle = new JLabel("Histórico de Vendas", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        topPanel.add(lblTitle, BorderLayout.NORTH);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setBackground(UIUtils.COLOR_WHITE);
        
        filterPanel.add(UIUtils.createLabel("Data (Ex: 2026-05):"));
        txtFiltroData = new JTextField(10);
        UIUtils.styleTextField(txtFiltroData);
        filterPanel.add(txtFiltroData);
        
        filterPanel.add(UIUtils.createLabel("Produto:"));
        txtFiltroProduto = new JTextField(12);
        UIUtils.styleTextField(txtFiltroProduto);
        filterPanel.add(txtFiltroProduto);
        
        filterPanel.add(UIUtils.createLabel("Afiliado/Vendedor:"));
        txtFiltroVendedor = new JTextField(12);
        UIUtils.styleTextField(txtFiltroVendedor);
        filterPanel.add(txtFiltroVendedor);
        
        JButton btnFiltrar = UIUtils.createButton("Filtrar");
        btnFiltrar.setPreferredSize(new Dimension(100, 35));
        btnFiltrar.addActionListener(e -> carregarTabela());
        filterPanel.add(btnFiltrar);
        
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(new String[]{
            "Cod.", "Data", "Vendedor", "Cliente", "Qtd. Itens", "Cupom", "Status", "Metodo", "Total Pago"
        }, 0);
        tabela = new JTable(tableModel);
        UIUtils.styleTable(tabela);
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        carregarTabela();
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        try {
            String filtroData = txtFiltroData != null ? txtFiltroData.getText() : "";
            String filtroProd = txtFiltroProduto != null ? txtFiltroProduto.getText() : "";
            String filtroVend = txtFiltroVendedor != null ? txtFiltroVendedor.getText() : "";
            
            List<Venda> lista = vendaDAO.listarComFiltros(filtroProd, filtroVend, filtroData);
            for (Venda v : lista) {
                String cupomDesc = v.getCupom() != null ? v.getCupom().getCodigo() : "Nenhum";
                String vendedorNome = v.getVendedor() != null ? v.getVendedor().getLogin() : "Desconhecido";
                String clienteNome = v.getCliente() != null ? v.getCliente().getNome() : "Desconhecido";
                
                tableModel.addRow(new Object[]{
                    v.getId(),
                    v.getDataVenda(),
                    vendedorNome,
                    clienteNome,
                    v.getItens() != null ? v.getItens().size() : 0,
                    cupomDesc,
                    v.getStatusVenda(),
                    v.getMetodoPagamento(),
                    String.format("R$ %.2f", v.getTotalComDesconto())
                });
            }
        } catch (com.retailhub.exception.PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar vendas no banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
