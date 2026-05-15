package com.retailhub.view;

import com.retailhub.model.PacoteTriagem;
import com.retailhub.model.Sessao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaExpedicao extends JPanel {

    private static List<PacoteTriagem> esteiraDia = new ArrayList<>();
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JTextField txtPedido, txtPeso, txtOperador, txtDestinatario, txtEndereco;
    private JComboBox<String> cbEmbalagem;
    private JCheckBox chkBolha;

    public TelaExpedicao() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel Superior
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        JLabel lblTitle = new JLabel("Triagem & Expedição (Esteira do Dia)", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulário
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 2, 2));
        
        formPanel.add(UIUtils.createLabel("Código do Pedido:"));
        txtPedido = new JTextField();
        UIUtils.styleTextField(txtPedido);
        formPanel.add(txtPedido);

        formPanel.add(UIUtils.createLabel("Destinatário:"));
        txtDestinatario = new JTextField();
        UIUtils.styleTextField(txtDestinatario);
        formPanel.add(txtDestinatario);

        formPanel.add(UIUtils.createLabel("Endereço de Entrega:"));
        txtEndereco = new JTextField();
        UIUtils.styleTextField(txtEndereco);
        formPanel.add(txtEndereco);

        formPanel.add(UIUtils.createLabel("Peso Estimado (kg):"));
        txtPeso = new JTextField();
        UIUtils.styleTextField(txtPeso);
        formPanel.add(txtPeso);

        formPanel.add(UIUtils.createLabel("Tipo de Embalagem:"));
        cbEmbalagem = new JComboBox<>(new String[]{"Saco Plástico", "Caixa de Papelão"});
        formPanel.add(cbEmbalagem);

        formPanel.add(UIUtils.createLabel("Plástico Bolha?"));
        chkBolha = new JCheckBox("Sim, precisa de proteção extra");
        chkBolha.setBackground(UIUtils.COLOR_WHITE);
        formPanel.add(chkBolha);

        formPanel.add(UIUtils.createLabel("Operador:"));
        txtOperador = new JTextField(Sessao.getUsuarioLogado() != null ? Sessao.getUsuarioLogado().getLogin() : "Logístico");
        txtOperador.setEditable(false);
        UIUtils.styleTextField(txtOperador);
        formPanel.add(txtOperador);

        topPanel.add(formPanel, BorderLayout.CENTER);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(UIUtils.COLOR_WHITE);
        
        JButton btnAdicionar = UIUtils.createButton("Adicionar à Esteira", UIUtils.COLOR_SUCCESS);
        btnAdicionar.addActionListener(e -> adicionar());
        
        JButton btnImprimirEtiqueta = UIUtils.createButton("🖨️ Imprimir Etiqueta", UIUtils.COLOR_ORANGE);
        btnImprimirEtiqueta.addActionListener(e -> imprimirEtiquetaSelecionada());

        JButton btnLimpar = UIUtils.createButton("Limpar Lista", UIUtils.COLOR_DANGER);
        btnLimpar.addActionListener(e -> limparLista());

        btnPanel.add(btnAdicionar);
        btnPanel.add(btnImprimirEtiqueta);
        btnPanel.add(btnLimpar);
        
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(new String[]{"Pedido", "Peso", "Embalagem", "Bolha", "Operador"}, 0);
        tabela = new JTable(tableModel);
        UIUtils.styleTable(tabela);
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        atualizarTabela();
    }

    private void adicionar() {
        try {
            if (txtPedido.getText().trim().isEmpty()) throw new Exception("Código do pedido obrigatório.");
            
            PacoteTriagem p = new PacoteTriagem();
            p.setCodigoPedido(txtPedido.getText());
            p.setDestinatario(txtDestinatario.getText());
            p.setEndereco(txtEndereco.getText());
            p.setPeso(Double.parseDouble(txtPeso.getText().replace(",", ".")));
            p.setTipoEmbalagem(cbEmbalagem.getSelectedItem().toString());
            p.setPrecisaPlasticoBolha(chkBolha.isSelected());
            p.setOperador(txtOperador.getText());
            
            esteiraDia.add(p);
            atualizarTabela();
            limparForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparLista() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja limpar toda a esteira do dia?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            esteiraDia.clear();
            atualizarTabela();
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        for (PacoteTriagem p : esteiraDia) {
            tableModel.addRow(new Object[]{
                p.getCodigoPedido(), p.getPeso() + " kg", p.getTipoEmbalagem(), p.isPrecisaPlasticoBolha() ? "Sim" : "Não", p.getOperador()
            });
        }
    }

    private void limparForm() {
        txtPedido.setText("");
        txtDestinatario.setText("");
        txtEndereco.setText("");
        txtPeso.setText("");
        chkBolha.setSelected(false);
        cbEmbalagem.setSelectedIndex(0);
    }

    private void imprimirEtiquetaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote na tabela para imprimir a etiqueta.");
            return;
        }

        PacoteTriagem p = esteiraDia.get(row);
        java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Desenhar Borda da Etiqueta
            g2d.drawRect(10, 10, 200, 300);

            // Cabeçalho
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.drawString("RETAILHUB SHIPPING", 20, 30);
            g2d.drawLine(10, 40, 210, 40);

            // Dados
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2d.drawString("PEDIDO: " + p.getCodigoPedido(), 20, 60);
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.drawString("DESTINATÁRIO:", 20, 90);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2d.drawString(p.getDestinatario(), 20, 105);

            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            String end = p.getEndereco();
            if (end.length() > 30) {
                g2d.drawString(end.substring(0, 30), 20, 130);
                g2d.drawString(end.substring(30), 20, 145);
            } else {
                g2d.drawString(end, 20, 130);
            }

            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.drawString("PESO: " + p.getPeso() + " kg", 20, 180);
            g2d.drawString("EMBALAGEM: " + p.getTipoEmbalagem(), 20, 200);

            // Simulação de Código de Barras
            g2d.fillRect(20, 230, 180, 40);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 8));
            g2d.drawString("* " + p.getCodigoPedido() + " *", 60, 285);

            return java.awt.print.Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Erro na impressão: " + ex.getMessage());
            }
        }
    }
}
