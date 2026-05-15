package com.retailhub.view;

import com.retailhub.dao.Migration;
import com.retailhub.dao.UsuarioDAO;
import com.retailhub.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public TelaLogin() {
        setTitle("RetailHub - Acesso");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        UIUtils.styleFrame(this);

        // Migração já executada no main

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Logo ou Título
        JLabel lblTitulo = new JLabel("🛒 RetailHub", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(UIUtils.COLOR_ORANGE);
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        // Formulário
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 5));
        formPanel.setBackground(UIUtils.COLOR_WHITE);

        formPanel.add(UIUtils.createLabel("Usuário:"));
        txtLogin = new JTextField();
        UIUtils.styleTextField(txtLogin);
        formPanel.add(txtLogin);

        formPanel.add(UIUtils.createLabel("Senha:"));
        txtSenha = new JPasswordField();
        UIUtils.styleTextField(txtSenha);
        formPanel.add(txtSenha);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Botão
        JButton btnEntrar = UIUtils.createButton("Entrar");
        btnEntrar.setPreferredSize(new Dimension(0, 40));
        btnEntrar.addActionListener(e -> realizarLogin());
        mainPanel.add(btnEntrar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void realizarLogin() {
        try {
            String login = txtLogin.getText();
            String senha = new String(txtSenha.getPassword());
            
            if (login.trim().isEmpty() || senha.trim().isEmpty()) {
                throw new com.retailhub.exception.ValidacaoException("Usuário e Senha são obrigatórios!");
            }
            
            Usuario usuario = usuarioDAO.autenticar(login, senha);
            if (usuario != null) {
                if (usuario.getStatusAtivo() == 0) {
                    JOptionPane.showMessageDialog(this, "Este usuário foi desativado pelo administrador.", "Acesso Bloqueado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                com.retailhub.model.Sessao.setUsuarioLogado(usuario);
                LogService.registrar("Efetuou login no sistema");
                
                // Verificar expiração de senha (90 dias)
                if (com.retailhub.util.SecurityUtils.isPasswordExpired(usuario.getDataUltimaSenha())) {
                    JOptionPane.showMessageDialog(this, "Sua senha expirou (limite de 90 dias).\nVocê deve alterá-la antes de acessar o sistema.", "Segurança", JOptionPane.WARNING_MESSAGE);
                    
                    // Abrir diálogo de troca de senha (bloqueante)
                    JDialog dialog = new JDialog(this, "Troca de Senha Obrigatória", true);
                    dialog.setSize(500, 400);
                    dialog.setLocationRelativeTo(this);
                    dialog.add(new TelaAlterarSenha());
                    dialog.setVisible(true);
                    
                    // Após fechar o diálogo, não entra no sistema. 
                    // Limpa a sessão e limpa os campos de senha para o usuário logar com a nova.
                    com.retailhub.model.Sessao.setUsuarioLogado(null);
                    txtSenha.setText("");
                    return;
                }
                
                new TelaPrincipal(usuario).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos!", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
            }
        } catch (com.retailhub.exception.ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (com.retailhub.exception.PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "Erro de Banco de Dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Migration.inicializar();
        String tema = new com.retailhub.dao.ConfiguracaoDAO().listarTodas().getOrDefault("loja_tema", "Light");
        UIUtils.applyTheme(tema);
        
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
