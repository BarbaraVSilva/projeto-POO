package com.retailhub.view;

import com.retailhub.dao.VendaDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TelaDashBI extends JPanel {

    private VendaDAO vendaDAO = new VendaDAO();

    public TelaDashBI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitle = new JLabel("Dashboard de BI - Performance de Vendas", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Painel de Gráficos
        JPanel chartPanelContainer = new JPanel(new GridLayout(1, 2, 20, 20));

        // 1. Gráfico de Barras: Vendas por Dia
        chartPanelContainer.add(createBarChart());

        // 2. Gráfico de Pizza: Categorias
        chartPanelContainer.add(createPieChart());

        mainPanel.add(chartPanelContainer, BorderLayout.CENTER);

        // Botão de Atualizar
        JButton btnRefresh = UIUtils.createButton("🔄 Atualizar Indicadores");
        btnRefresh.addActionListener(e -> {
            chartPanelContainer.removeAll();
            chartPanelContainer.add(createBarChart());
            chartPanelContainer.add(createPieChart());
            chartPanelContainer.revalidate();
            chartPanelContainer.repaint();
        });
        mainPanel.add(btnRefresh, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> dados = vendaDAO.getVendasPorDia();
        
        if (dados.isEmpty()) {
            return new JPanel(new BorderLayout()) {{
                add(new JLabel("Sem dados de vendas para o gráfico de barras.", SwingConstants.CENTER));
            }};
        }

        for (Map.Entry<String, Double> entry : dados.entrySet()) {
            dataset.addValue(entry.getValue(), "Vendas", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Faturamento Bruto (Últimos 10 dias)",
                "Data",
                "Valor (R$)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        return new ChartPanel(barChart, true);
    }

    private JPanel createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> dados = vendaDAO.getVendasPorCategoria();

        if (dados.isEmpty()) {
            return new JPanel(new BorderLayout()) {{
                add(new JLabel("Sem dados de vendas para o gráfico de pizza.", SwingConstants.CENTER));
            }};
        }

        for (Map.Entry<String, Integer> entry : dados.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Distribuição por Categoria (Volume)",
                dataset,
                true, true, false);

        return new ChartPanel(pieChart, true);
    }
}
