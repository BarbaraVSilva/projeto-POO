package com.retailhub.view;

import com.retailhub.dao.VendaDAO;
import com.retailhub.model.Venda;
import com.retailhub.model.ItemVenda;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TelaPedidoFinanceiro extends JPanel {

    private VendaDAO vendaDAO = new VendaDAO();
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JTextField txtFiltroPedido, txtFiltroData;
    private JComboBox<String> cbFiltroStatus;

    public TelaPedidoFinanceiro() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Título
        JLabel lblTitle = new JLabel("Painel Financeiro - Seller Center (Simulado)", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Painel de Filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(UIUtils.COLOR_WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros Dinâmicos"));

        filterPanel.add(UIUtils.createLabel("Código Pedido:"));
        txtFiltroPedido = new JTextField(10);
        UIUtils.styleTextField(txtFiltroPedido);
        filterPanel.add(txtFiltroPedido);

        filterPanel.add(UIUtils.createLabel("Status Logística:"));
        cbFiltroStatus = new JComboBox<>(new String[]{"Todos", "A Enviar", "Enviado", "Entregue"});
        filterPanel.add(cbFiltroStatus);

        filterPanel.add(UIUtils.createLabel("Data (AAAA-MM-DD):"));
        txtFiltroData = new JTextField(10);
        UIUtils.styleTextField(txtFiltroData);
        filterPanel.add(txtFiltroData);

        JButton btnFiltrar = UIUtils.createButton("Filtrar e Calcular Lucro");
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        filterPanel.add(btnFiltrar);

        mainPanel.add(filterPanel, BorderLayout.CENTER); // Temporariamente no center para layout

        // Tabela
        tableModel = new DefaultTableModel(new String[]{
            "Pedido #", "Data", "Cliente", "Status Logístico", "Faturamento Bruto", "Comissão Retida", "Faturamento Líquido"
        }, 0);
        tabela = new JTable(tableModel);
        UIUtils.styleTable(tabela);
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        
        // Ajustando layout para filtros no topo e tabela embaixo
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIUtils.COLOR_WHITE);
        container.add(filterPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(container, BorderLayout.CENTER);

        add(mainPanel);
        aplicarFiltros(); // Carga inicial
    }

    private void aplicarFiltros() {
        String idBusca = txtFiltroPedido.getText().trim();
        String statusBusca = cbFiltroStatus.getSelectedItem().toString();
        String dataBusca = txtFiltroData.getText().trim();

        List<Venda> vendas = vendaDAO.listarComFiltros(null, null, dataBusca);
        
        // Filtros em memória para simular processamento dinâmico
        List<Venda> filtradas = vendas.stream()
            .filter(v -> idBusca.isEmpty() || String.valueOf(v.getId()).contains(idBusca))
            .filter(v -> statusBusca.equals("Todos") || v.getStatusLogistica().equals(statusBusca))
            .collect(Collectors.toList());

        atualizarTabela(filtradas);
    }

    private void atualizarTabela(List<Venda> lista) {
        tableModel.setRowCount(0);
        for (Venda v : lista) {
            double bruto = v.getTotalComDesconto();
            double comissaoTotal = 0;
            
            // Calcula comissão item a item baseada na categoria
            for (ItemVenda item : v.getItens()) {
                double taxa = item.getProduto().getCategoria().getTaxaComissao();
                comissaoTotal += (item.getSubtotal() * (taxa / 100.0));
            }
            
            // Se houver cupom, a comissão é calculada sobre o valor com desconto? 
            // Geralmente sim no marketplace, mas para simplificar mantemos sobre o subtotal dos itens.
            
            double liquido = bruto - comissaoTotal;

            tableModel.addRow(new Object[]{
                v.getId(),
                v.getDataVenda(),
                v.getCliente() != null ? v.getCliente().getNome() : "Consumidor Final",
                v.getStatusLogistica(),
                String.format("R$ %.2f", bruto),
                String.format("R$ %.2f", comissaoTotal),
                String.format("R$ %.2f", liquido)
            });
        }
    }
}
