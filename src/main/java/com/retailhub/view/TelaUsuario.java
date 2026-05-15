package com.retailhub.view;

import com.retailhub.dao.UsuarioDAO;
import com.retailhub.exception.ValidacaoException;
import com.retailhub.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaUsuario extends JPanel {

    private UsuarioDAO dao = new UsuarioDAO();
    private JTable tabela;
    private DefaultTableModel tableModel;

    private JTextField txtId, txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<String> cbPerfil;
    private JComboBox<String> cbStatus;

    public TelaUsuario() {
        if (com.retailhub.model.Sessao.getUsuarioLogado() == null || !com.retailhub.model.Sessao.getUsuarioLogado().isAdmin()) {
            setLayout(new BorderLayout());
            add(new JLabel("Acesso Negado: Apenas administradores podem acessar esta tela.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Título Superior
        JLabel lblTitle = new JLabel("Cadastro de Usuários", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Painel Superior (Formulário + Botões)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(UIUtils.COLOR_WHITE);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulário
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBackground(UIUtils.COLOR_WHITE);

        formPanel.add(UIUtils.createLabel("ID (Automático):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        UIUtils.styleTextField(txtId);
        formPanel.add(txtId);

        formPanel.add(UIUtils.createLabel("Login:"));
        txtLogin = new JTextField();
        UIUtils.styleTextField(txtLogin);
        formPanel.add(txtLogin);

        formPanel.add(UIUtils.createLabel("Senha:"));
        txtSenha = new JPasswordField();
        UIUtils.styleTextField(txtSenha);
        formPanel.add(txtSenha);

        formPanel.add(UIUtils.createLabel("Nível de Acesso (Perfil):"));
        cbPerfil = new JComboBox<>(new String[]{"AFILIADO", "ADMIN"});
        formPanel.add(cbPerfil);
        
        formPanel.add(UIUtils.createLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"Ativo", "Inativo"});
        formPanel.add(cbStatus);

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
        tableModel = new DefaultTableModel(new String[]{"ID", "Login", "Perfil", "Status"}, 0);
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
            if (txtLogin.getText().trim().isEmpty()) {
                throw new ValidacaoException("O login é obrigatório!");
            }
            String senha = new String(txtSenha.getPassword()).trim();
            if (senha.isEmpty() && txtId.getText().isEmpty()) {
                throw new ValidacaoException("A senha é obrigatória para novos usuários!");
            }

            Usuario u = new Usuario(
                txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()),
                txtLogin.getText(),
                senha,
                cbPerfil.getSelectedItem().toString(),
                cbStatus.getSelectedIndex() == 0 ? 1 : 0
            );

            if (u.getId() == 0) {
                dao.inserir(u);
                LogService.registrar("Cadastrou o usuário: " + u.getLogin());
                JOptionPane.showMessageDialog(this, "Usuário inserido com sucesso!");
            } else {
                dao.atualizar(u);
                LogService.registrar("Atualizou o usuário: " + u.getLogin() + " (ID: " + u.getId() + ")");
                JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
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
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este usuário?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.excluir(Integer.parseInt(txtId.getText()));
                LogService.registrar("Excluiu o usuário ID: " + txtId.getText());
                limparFormulario();
                carregarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir.");
        }
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Usuario> lista = dao.listarTodos();
        for (Usuario u : lista) {
            tableModel.addRow(new Object[]{
                u.getId(), u.getLogin(), u.getPerfil(), u.getStatusAtivo() == 1 ? "Ativo" : "Inativo"
            });
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            txtId.setText(tabela.getValueAt(row, 0).toString());
            txtLogin.setText(tabela.getValueAt(row, 1).toString());
            txtSenha.setText(""); // Não carregar senha na tela por segurança
            cbPerfil.setSelectedItem(tabela.getValueAt(row, 2).toString());
            cbStatus.setSelectedItem(tabela.getValueAt(row, 3).toString());
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        cbPerfil.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        tabela.clearSelection();
    }
}
