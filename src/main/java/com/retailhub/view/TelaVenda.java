package com.retailhub.view;

import com.retailhub.dao.ClienteDAO;
import com.retailhub.dao.CupomDAO;
import com.retailhub.dao.ProdutoDAO;
import com.retailhub.dao.VendaDAO;
import com.retailhub.model.Cliente;
import com.retailhub.model.Cupom;
import com.retailhub.model.ItemVenda;
import com.retailhub.model.Produto;
import com.retailhub.model.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TelaVenda extends JPanel {

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private CupomDAO cupomDAO = new CupomDAO();
    private VendaDAO vendaDAO = new VendaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    
    private JComboBox<String> cbProdutos;
    private JTextField txtQuantidade;
    
    private JComboBox<Cliente> cbClientes;
    private JComboBox<String> cbCupons;
    private JComboBox<String> cbMetodoPagamento;
    
    private JLabel lblTotal, lblImagemProduto;
    private DefaultTableModel tableModel;
    private JTable tabelaCarrinho;
    
    private List<Produto> listaProdutos;
    private List<Cupom> listaCupons;
    private List<ItemVenda> carrinho = new ArrayList<>();

    public TelaVenda() {
        setLayout(new BorderLayout(10, 10));
        // Removido setBackground para permitir que o FlatLaf gerencie a cor do tema
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        listaCupons = cupomDAO.listarAtivos();

        // Título Superior
        JLabel lblTitle = new JLabel("PDV - Caixa (Carrinho de Compras)", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        add(lblTitle, BorderLayout.NORTH);

        // Painel Dividido: Esquerda (Adicionar Item) e Direita (Carrinho)
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        splitPanel.setBackground(UIUtils.COLOR_WHITE);

        // -- LADO ESQUERDO: Adicionar Itens --
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(UIUtils.COLOR_WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Adicionar Produto"));
        
        JPanel formItemPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        formItemPanel.setBackground(UIUtils.COLOR_WHITE);
        
        JPanel panelProd = new JPanel(new BorderLayout(5, 0));
        panelProd.setBackground(UIUtils.COLOR_WHITE);
        panelProd.add(UIUtils.createLabel("Produto:"), BorderLayout.NORTH);
        cbProdutos = new JComboBox<>();
        carregarProdutos();
        cbProdutos.addActionListener(e -> atualizarImagemProduto());
        panelProd.add(cbProdutos, BorderLayout.CENTER);
        formItemPanel.add(panelProd);
        
        JPanel panelQtd = new JPanel(new BorderLayout(5, 0));
        panelQtd.setBackground(UIUtils.COLOR_WHITE);
        panelQtd.add(UIUtils.createLabel("Quantidade:"), BorderLayout.NORTH);
        txtQuantidade = new JTextField("1");
        UIUtils.styleTextField(txtQuantidade);
        panelQtd.add(txtQuantidade, BorderLayout.CENTER);
        formItemPanel.add(panelQtd);
        
        JButton btnAdicionar = UIUtils.createButton("Adicionar ao Carrinho");
        btnAdicionar.addActionListener(e -> adicionarItem());
        formItemPanel.add(btnAdicionar);
        
        leftPanel.add(formItemPanel, BorderLayout.NORTH);
        
        // Espaço para Imagem do Produto Selecionado
        lblImagemProduto = new JLabel("Sem Imagem", SwingConstants.CENTER);
        lblImagemProduto.setPreferredSize(new Dimension(200, 200));
        lblImagemProduto.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        leftPanel.add(lblImagemProduto, BorderLayout.CENTER);
        
        splitPanel.add(leftPanel);

        // -- LADO DIREITO: Carrinho --
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(UIUtils.COLOR_WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Itens no Carrinho"));
        
        tableModel = new DefaultTableModel(new String[]{"Produto", "Qtd", "Preço (R$)", "Subtotal"}, 0);
        tabelaCarrinho = new JTable(tableModel);
        UIUtils.styleTable(tabelaCarrinho);
        rightPanel.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        
        JButton btnRemover = UIUtils.createButton("Remover Selecionado", UIUtils.COLOR_DANGER);
        btnRemover.addActionListener(e -> removerItem());
        rightPanel.add(btnRemover, BorderLayout.SOUTH);
        
        splitPanel.add(rightPanel);
        add(splitPanel, BorderLayout.CENTER);

        // -- PAINEL INFERIOR: Checkout --
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(UIUtils.COLOR_WHITE);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Finalizar Venda"));
        
        JPanel checkoutOptionsPanel = new JPanel(new GridBagLayout());
        checkoutOptionsPanel.setBackground(UIUtils.COLOR_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.gridx = 0; checkoutOptionsPanel.add(UIUtils.createLabel("Cliente:"), gbc);
        gbc.gridx = 1; checkoutOptionsPanel.add(UIUtils.createLabel("Cupom:"), gbc);
        gbc.gridx = 2; checkoutOptionsPanel.add(UIUtils.createLabel("Pagamento:"), gbc);

        gbc.gridy = 1; gbc.gridx = 0;
        cbClientes = new JComboBox<>(); carregarClientes();
        checkoutOptionsPanel.add(cbClientes, gbc);

        gbc.gridx = 1;
        cbCupons = new JComboBox<>();
        cbCupons.addItem("Sem Cupom");
        for (Cupom c : listaCupons) {
            cbCupons.addItem(c.getCodigo() + " (" + c.getDescontoPercentual() + "%)");
        }
        cbCupons.addActionListener(e -> atualizarTotal());
        checkoutOptionsPanel.add(cbCupons, gbc);

        gbc.gridx = 2;
        cbMetodoPagamento = new JComboBox<>(new String[]{"DINHEIRO", "CARTAO", "PIX"});
        checkoutOptionsPanel.add(cbMetodoPagamento, gbc);
        
        bottomPanel.add(checkoutOptionsPanel, BorderLayout.NORTH);
        
        JPanel finalizePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        finalizePanel.setBackground(UIUtils.COLOR_WHITE);
        
        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(UIUtils.FONT_TITLE);
        lblTotal.setForeground(UIUtils.COLOR_ORANGE);
        finalizePanel.add(lblTotal);
        
        JButton btnFinalizar = UIUtils.createButton("Concluir Pedido", UIUtils.COLOR_SUCCESS);
        btnFinalizar.setPreferredSize(new Dimension(200, 45));
        btnFinalizar.addActionListener(e -> registrarVenda());
        finalizePanel.add(btnFinalizar);
        
        bottomPanel.add(finalizePanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void carregarProdutos() {
        cbProdutos.removeAllItems();
        listaProdutos = produtoDAO.listarTodos();
        if (listaProdutos.isEmpty()) {
            cbProdutos.addItem("Nenhum produto cadastrado");
        } else {
            for (Produto p : listaProdutos) {
                cbProdutos.addItem(p.getNome() + " (Estoque: " + p.getQuantidadeEstoque() + ") - R$ " + String.format("%.2f", p.getPreco()));
            }
        }
        atualizarImagemProduto();
    }

    private void atualizarImagemProduto() {
        int index = cbProdutos.getSelectedIndex();
        if (index >= 0 && index < listaProdutos.size()) {
            Produto p = listaProdutos.get(index);
            if (p.getImagemPath() != null && !p.getImagemPath().isEmpty()) {
                try {
                    File imgFile = new File(p.getImagemPath());
                    if (imgFile.exists()) {
                        ImageIcon icon = new ImageIcon(p.getImagemPath());
                        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        lblImagemProduto.setIcon(new ImageIcon(img));
                        lblImagemProduto.setText("");
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar imagem: " + e.getMessage());
                }
            }
        }
        lblImagemProduto.setIcon(null);
        lblImagemProduto.setText("Sem Imagem");
    }

    private void carregarClientes() {
        cbClientes.removeAllItems();
        cbClientes.addItem(null); // Option for no customer (anonymous)
        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente c : clientes) {
            cbClientes.addItem(c);
        }
    }

    private void adicionarItem() {
        if (cbProdutos.getSelectedIndex() < 0) return;
        
        try {
            int qtd = Integer.parseInt(txtQuantidade.getText());
            if (qtd <= 0) throw new com.retailhub.exception.ValidacaoException("Quantidade deve ser maior que zero.");
            
            Produto p = listaProdutos.get(cbProdutos.getSelectedIndex());
            
            // Verifica se o item já está no carrinho para somar a quantidade
            int qtdNoCarrinho = 0;
            for(ItemVenda iv : carrinho) {
                if(iv.getProduto().getId() == p.getId()) {
                    qtdNoCarrinho += iv.getQuantidade();
                }
            }

            if (p.getQuantidadeEstoque() < (qtd + qtdNoCarrinho)) {
                throw new com.retailhub.exception.ValidacaoException("Estoque insuficiente! Disponível: " + p.getQuantidadeEstoque());
            }

            // Adiciona ou atualiza no carrinho
            boolean found = false;
            for(ItemVenda iv : carrinho) {
                if(iv.getProduto().getId() == p.getId()) {
                    iv.setQuantidade(iv.getQuantidade() + qtd);
                    found = true;
                    break;
                }
            }
            if(!found) {
                carrinho.add(new ItemVenda(p, qtd, p.getPreco()));
            }

            atualizarTabelaCarrinho();
            txtQuantidade.setText("1");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (com.retailhub.exception.ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removerItem() {
        int row = tabelaCarrinho.getSelectedRow();
        if (row >= 0) {
            carrinho.remove(row);
            atualizarTabelaCarrinho();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.");
        }
    }

    private void atualizarTabelaCarrinho() {
        tableModel.setRowCount(0);
        for (ItemVenda iv : carrinho) {
            tableModel.addRow(new Object[]{
                iv.getProduto().getNome(),
                iv.getQuantidade(),
                String.format("%.2f", iv.getPrecoUnitario()),
                String.format("%.2f", iv.getSubtotal())
            });
        }
        atualizarTotal();
    }

    private void atualizarTotal() {
        double subtotal = 0;
        for (ItemVenda iv : carrinho) {
            subtotal += iv.getSubtotal();
        }
        
        if (cbCupons.getSelectedIndex() > 0) {
            Cupom c = listaCupons.get(cbCupons.getSelectedIndex() - 1);
            subtotal -= (subtotal * (c.getDescontoPercentual() / 100.0));
        }
        lblTotal.setText(String.format("Total: R$ %.2f", subtotal));
    }

    private void registrarVenda() {
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio!", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Venda venda = new Venda();
            venda.setVendedor(com.retailhub.model.Sessao.getUsuarioLogado());
            
            Cliente cliente = (Cliente) cbClientes.getSelectedItem();
            venda.setCliente(cliente);

            if (cbCupons.getSelectedIndex() > 0) {
                venda.setCupom(listaCupons.get(cbCupons.getSelectedIndex() - 1));
            }
            
            venda.setMetodoPagamento(cbMetodoPagamento.getSelectedItem().toString());
            venda.setStatusVenda("CONCLUIDA");
            
            // Clona os itens para a venda
            for (ItemVenda iv : carrinho) {
                venda.adicionarItem(iv);
            }

            vendaDAO.registrarVenda(venda);
            
            // Exibe o Cupom Fiscal
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow instanceof JFrame) {
                new TelaCupomFiscal((JFrame) parentWindow, venda).setVisible(true);
            }

            // Opcional: Atualizar estoque local para a próxima venda, pois o banco já foi atualizado
            carregarProdutos();
            
            LogService.registrar("Realizou venda de " + carrinho.size() + " itens (Valor: R$ " + String.format("%.2f", venda.getTotalComDesconto()) + ")");
            // JOptionPane.showMessageDialog(this, "Pedido finalizado com sucesso!"); // Substituído pelo Cupom
            
            carrinho.clear();
            atualizarTabelaCarrinho();
            cbCupons.setSelectedIndex(0);
            cbClientes.setSelectedIndex(0);

        } catch (com.retailhub.exception.PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar venda no banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
