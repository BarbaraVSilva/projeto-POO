package com.retailhub.view;

import com.retailhub.dao.ClienteDAO;
import com.retailhub.exception.ValidacaoException;
import com.retailhub.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCliente extends JPanel {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JTextField txtId, txtNome, txtCpf, txtEmail, txtTelefone, txtEndereco;

    public TelaCliente() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Painel Superior (Título, Formulário, Botões)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(UIUtils.COLOR_WHITE);

        // Título Superior
        JLabel lblTitle = new JLabel("Gerenciamento de Clientes", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulário
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBackground(UIUtils.COLOR_WHITE);
        
        formPanel.add(UIUtils.createLabel("ID (Automático):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        UIUtils.styleTextField(txtId);
        formPanel.add(txtId);

        formPanel.add(UIUtils.createLabel("Nome Completo:"));
        txtNome = new JTextField();
        UIUtils.styleTextField(txtNome);
        formPanel.add(txtNome);

        formPanel.add(UIUtils.createLabel("CPF:"));
        txtCpf = new JTextField();
        UIUtils.styleTextField(txtCpf);
        formPanel.add(txtCpf);

        formPanel.add(UIUtils.createLabel("E-mail:"));
        txtEmail = new JTextField();
        UIUtils.styleTextField(txtEmail);
        formPanel.add(txtEmail);

        formPanel.add(UIUtils.createLabel("Telefone:"));
        txtTelefone = new JTextField();
        UIUtils.styleTextField(txtTelefone);
        formPanel.add(txtTelefone);

        formPanel.add(UIUtils.createLabel("Endereço:"));
        txtEndereco = new JTextField();
        UIUtils.styleTextField(txtEndereco);
        formPanel.add(txtEndereco);

        topPanel.add(formPanel, BorderLayout.CENTER);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(UIUtils.COLOR_WHITE);
        
        JButton btnSalvar = UIUtils.createButton("Salvar", UIUtils.COLOR_SUCCESS);
        btnSalvar.addActionListener(e -> salvar());
        
        JButton btnExcluir = UIUtils.createButton("Excluir", UIUtils.COLOR_DANGER);
        btnExcluir.addActionListener(e -> excluir());
        
        JButton btnLimpar = UIUtils.createButton("Limpar");
        btnLimpar.addActionListener(e -> limparFormulario());

        btnPanel.add(btnSalvar);
        btnPanel.add(btnExcluir);
        btnPanel.add(btnLimpar);
        
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "E-mail", "Telefone", "Endereço", "Data Cad."}, 0);
        tabela = new JTable(tableModel);
        UIUtils.styleTable(tabela);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherFormulario());
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        carregarTabela();
    }

    private void salvar() {
        try {
            if (txtNome.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty()) {
                throw new ValidacaoException("Nome e CPF são obrigatórios!");
            }

            Cliente c = new Cliente();
            c.setNome(txtNome.getText());
            c.setCpf(txtCpf.getText());
            c.setEmail(txtEmail.getText());
            c.setTelefone(txtTelefone.getText());
            c.setEndereco(txtEndereco.getText());

            if (txtId.getText().isEmpty()) {
                clienteDAO.inserir(c);
                LogService.registrar("Cadastrou o cliente: " + c.getNome());
                JOptionPane.showMessageDialog(this, "Cliente inserido com sucesso!");
            } else {
                c.setId(Integer.parseInt(txtId.getText()));
                clienteDAO.atualizar(c);
                LogService.registrar("Atualizou o cliente: " + c.getNome());
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            }
            limparFormulario();
            carregarTabela();
        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (com.retailhub.exception.PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "Erro de Banco de Dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (!txtId.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                clienteDAO.excluir(Integer.parseInt(txtId.getText()));
                LogService.registrar("Excluiu o cliente ID: " + txtId.getText());
                limparFormulario();
                carregarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.");
        }
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Cliente> lista = clienteDAO.listarTodos();
        for (Cliente c : lista) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getNome(), c.getCpf(), c.getEmail(), c.getTelefone(), c.getEndereco(), c.getDataCadastro()
            });
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            txtId.setText(tabela.getValueAt(row, 0).toString());
            txtNome.setText(tabela.getValueAt(row, 1).toString());
            txtCpf.setText(tabela.getValueAt(row, 2) != null ? tabela.getValueAt(row, 2).toString() : "");
            txtEmail.setText(tabela.getValueAt(row, 3) != null ? tabela.getValueAt(row, 3).toString() : "");
            txtTelefone.setText(tabela.getValueAt(row, 4) != null ? tabela.getValueAt(row, 4).toString() : "");
            txtEndereco.setText(tabela.getValueAt(row, 5) != null ? tabela.getValueAt(row, 5).toString() : "");
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        tabela.clearSelection();
    }
}
