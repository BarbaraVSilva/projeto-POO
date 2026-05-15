package com.retailhub.view;

import com.retailhub.dao.ConfiguracaoDAO;
import com.retailhub.dao.VendaDAO;
import com.retailhub.model.Venda;
import com.retailhub.model.ItemVenda;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.util.Map;

public class TelaNotaFiscal extends JPanel {

    private VendaDAO vendaDAO = new VendaDAO();
    private ConfiguracaoDAO configDAO = new ConfiguracaoDAO();
    private JTextField txtVendaId;
    private JTextArea areaNF;
    private Venda vendaAtual;

    public TelaNotaFiscal() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Topo: Busca de Venda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(UIUtils.COLOR_WHITE);
        searchPanel.add(UIUtils.createLabel("ID da Venda:"));
        txtVendaId = new JTextField(10);
        UIUtils.styleTextField(txtVendaId);
        searchPanel.add(txtVendaId);

        JButton btnGerar = UIUtils.createButton("Gerar Nota Fiscal", UIUtils.COLOR_ORANGE);
        btnGerar.addActionListener(e -> buscarEGerarNF());
        searchPanel.add(btnGerar);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Centro: Visualização da NF
        areaNF = new JTextArea();
        areaNF.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaNF.setEditable(false);
        areaNF.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JScrollPane scroll = new JScrollPane(areaNF);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Rodapé: Botão de Impressão
        JButton btnImprimir = UIUtils.createButton("🖨️ Imprimir Cupom Fiscal", UIUtils.COLOR_SUCCESS);
        btnImprimir.addActionListener(e -> imprimir());
        mainPanel.add(btnImprimir, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void buscarEGerarNF() {
        try {
            int id = Integer.parseInt(txtVendaId.getText());
            java.util.List<Venda> vendas = vendaDAO.listarComFiltros(null, null, "");
            vendaAtual = null;
            for (Venda v : vendas) {
                if (v.getId() == id) {
                    vendaAtual = v;
                    break;
                }
            }

            if (vendaAtual == null) {
                JOptionPane.showMessageDialog(this, "Venda não encontrada!");
                return;
            }

            gerarTextoNF();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido!");
        }
    }

    private void gerarTextoNF() {
        Map<String, String> configs = configDAO.listarTodas();
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("            ").append(configs.getOrDefault("loja_nome", "RetailHub")).append("\n");
        sb.append("       CNPJ: ").append(configs.getOrDefault("loja_cnpj", "00.000.000/0001-00")).append("\n");
        sb.append("  ").append(configs.getOrDefault("loja_endereco", "Endereço não cadastrado")).append("\n");
        sb.append("==========================================\n");
        sb.append("           CUPOM FISCAL SIMULADO          \n");
        sb.append("==========================================\n");
        sb.append("PEDIDO: ").append(vendaAtual.getId()).append("\n");
        sb.append("DATA:   ").append(vendaAtual.getDataVenda()).append("\n");
        sb.append("CLIENTE:").append(vendaAtual.getCliente() != null ? vendaAtual.getCliente().getNome() : "Consumidor Final").append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-20s %3s %10s\n", "ITEM", "QTD", "VALOR"));
        
        for (ItemVenda item : vendaAtual.getItens()) {
            String nome = item.getProduto().getNome();
            if (nome.length() > 18) nome = nome.substring(0, 18);
            sb.append(String.format("%-20s %3d %10.2f\n", nome, item.getQuantidade(), item.getPrecoUnitario()));
        }
        
        sb.append("------------------------------------------\n");
        sb.append(String.format("SUBTOTAL:                 R$ %10.2f\n", vendaAtual.getTotalComDesconto()));
        if (vendaAtual.getCupom() != null) {
            sb.append(String.format("DESCONTO (CUPOM):         %10.2f%%\n", vendaAtual.getCupom().getDescontoPercentual()));
        }
        sb.append("TOTAL A PAGAR:            R$ ").append(String.format("%.2f", vendaAtual.getTotalComDesconto())).append("\n");
        sb.append("FORMA PAGTO: ").append(vendaAtual.getMetodoPagamento()).append("\n");
        sb.append("==========================================\n");
        sb.append("      Obrigado pela preferência!          \n");
        sb.append("==========================================\n");
        
        areaNF.setText(sb.toString());
    }

    private void imprimir() {
        if (vendaAtual == null) {
            JOptionPane.showMessageDialog(this, "Gere uma nota fiscal antes de imprimir!");
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

            String[] lines = areaNF.getText().split("\n");
            int y = 20;
            for (String line : lines) {
                g2d.drawString(line, 10, y);
                y += 12;
            }

            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Erro na impressão: " + ex.getMessage());
            }
        }
    }
}
