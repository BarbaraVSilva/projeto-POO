package com.retailhub.view;

import com.retailhub.model.ItemVenda;
import com.retailhub.model.Venda;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class TelaCupomFiscal extends JDialog {

    public TelaCupomFiscal(JFrame parent, Venda venda) {
        super(parent, "Cupom Fiscal", true);
        setSize(400, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTextArea txtCupom = new JTextArea();
        txtCupom.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtCupom.setEditable(false);
        txtCupom.setBackground(new Color(255, 255, 240)); // Papel térmico
        txtCupom.setMargin(new Insets(20, 20, 20, 20));

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("               RETAIL HUB                 \n");
        sb.append("         Sua Loja de Tecnologia           \n");
        sb.append("==========================================\n");
        sb.append("Data: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())).append("\n");
        sb.append("Vendedor: ").append(venda.getVendedor().getLogin()).append("\n");
        sb.append("Cliente: ").append(venda.getCliente() != null ? venda.getCliente().getNome() : "Consumidor Final").append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-20s %3s %8s %8s\n", "Item", "Qtd", "Unit", "Sub"));
        
        for (ItemVenda iv : venda.getItens()) {
            String nome = iv.getProduto().getNome();
            if (nome.length() > 20) nome = nome.substring(0, 17) + "...";
            sb.append(String.format("%-20s %3d %8.2f %8.2f\n", 
                nome, 
                iv.getQuantidade(), 
                iv.getPrecoUnitario(), 
                iv.getSubtotal()));
        }
        
        sb.append("------------------------------------------\n");
        sb.append(String.format("SUBTOTAL:                    R$ %8.2f\n", venda.getTotalBruto()));
        if (venda.getCupom() != null) {
            sb.append(String.format("DESCONTO (%s):           -R$ %8.2f\n", 
                venda.getCupom().getCodigo(), 
                venda.getTotalBruto() - venda.getTotalComDesconto()));
        }
        sb.append(String.format("TOTAL:                       R$ %8.2f\n", venda.getTotalComDesconto()));
        sb.append("------------------------------------------\n");
        sb.append("PAGAMENTO: ").append(venda.getMetodoPagamento()).append("\n");
        sb.append("==========================================\n");
        sb.append("        OBRIGADO PELA PREFERENCIA!        \n");
        sb.append("==========================================\n");

        txtCupom.setText(sb.toString());
        add(new JScrollPane(txtCupom), BorderLayout.CENTER);

        JButton btnFechar = UIUtils.createButton("Fechar e Continuar");
        btnFechar.addActionListener(e -> dispose());
        add(btnFechar, BorderLayout.SOUTH);
    }
}
