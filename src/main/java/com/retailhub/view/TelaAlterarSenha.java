package com.retailhub.view;

import com.retailhub.dao.UsuarioDAO;
import com.retailhub.model.Sessao;
import com.retailhub.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaAlterarSenha extends JPanel {

    private JPasswordField txtSenhaAtual, txtNovaSenha, txtConfirmarSenha;
    private UsuarioDAO dao = new UsuarioDAO();

    public TelaAlterarSenha() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Título
        JLabel lblTitle = new JLabel("Alterar Minha Senha", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulário centralizado
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        formPanel.setBackground(UIUtils.COLOR_WHITE);

        formPanel.add(UIUtils.createLabel("Senha Atual:"));
        txtSenhaAtual = new JPasswordField();
        txtSenhaAtual.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(txtSenhaAtual);

        formPanel.add(UIUtils.createLabel("Nova Senha:"));
        txtNovaSenha = new JPasswordField();
        txtNovaSenha.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(txtNovaSenha);

        formPanel.add(UIUtils.createLabel("Confirmar Nova Senha:"));
        txtConfirmarSenha = new JPasswordField();
        txtConfirmarSenha.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(txtConfirmarSenha);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Botão de Salvar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(UIUtils.COLOR_WHITE);
        JButton btnSalvar = UIUtils.createButton("Atualizar Senha");
        btnSalvar.setPreferredSize(new Dimension(200, 45));
        btnSalvar.addActionListener(e -> alterarSenha());
        btnPanel.add(btnSalvar);
        
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void alterarSenha() {
        String atual = new String(txtSenhaAtual.getPassword());
        String nova = new String(txtNovaSenha.getPassword());
        String confirmar = new String(txtConfirmarSenha.getPassword());

        if (atual.isEmpty() || nova.isEmpty() || confirmar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioLogado = Sessao.getUsuarioLogado();
        
        // Verifica se a senha atual digitada bate com a do banco
        Usuario checkUser = dao.autenticar(usuarioLogado.getLogin(), atual);
        if (checkUser == null) {
            JOptionPane.showMessageDialog(this, "A senha atual está incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!nova.equals(confirmar)) {
            JOptionPane.showMessageDialog(this, "A nova senha e a confirmação não coincidem!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            usuarioLogado.setSenha(nova);
            dao.atualizar(usuarioLogado); // O DAO atualiza pelo ID
            LogService.registrar("Alterou a própria senha com sucesso.");
            JOptionPane.showMessageDialog(this, "Senha atualizada com sucesso!\nNo próximo acesso, utilize a nova senha.");
            
            // Fecha a janela/diálogo pai se existir
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow != null) {
                if (parentWindow instanceof TelaPrincipal) {
                    // Se for a tela principal, faz logout completo
                    Sessao.setUsuarioLogado(null);
                    new TelaLogin().setVisible(true);
                }
                parentWindow.dispose();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar senha: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
