package com.retailhub.view;

import com.retailhub.dao.ConfiguracaoDAO;
import com.retailhub.model.Sessao;
import com.retailhub.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TelaConfiguracoes extends JPanel {

    private ConfiguracaoDAO configDAO = new ConfiguracaoDAO();
    private JTextField txtLojaNome, txtLojaCNPJ, txtLojaEndereco, txtLojaTelefone;
    private JComboBox<String> cbTema;

    public TelaConfiguracoes() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // --- LADO ESQUERDO: Perfil do Usuário ---
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(new Color(250, 250, 250));
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        Usuario user = Sessao.getUsuarioLogado();
        JLabel lblAvatar = new JLabel("👤", SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(lblAvatar);

        profilePanel.add(Box.createVerticalStrut(10));
        JLabel lblUserTitle = new JLabel("Perfil do Operador");
        lblUserTitle.setFont(UIUtils.FONT_TITLE);
        lblUserTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(lblUserTitle);

        profilePanel.add(Box.createVerticalStrut(20));
        profilePanel.add(createProfileLabel("Usuário: " + (user != null ? user.getLogin() : "N/A")));
        profilePanel.add(createProfileLabel("Perfil: " + (user != null ? user.getPerfil() : "N/A")));
        profilePanel.add(createProfileLabel("ID: " + (user != null ? user.getId() : "N/A")));
        profilePanel.add(createProfileLabel("Status: Ativo ✅"));

        mainPanel.add(profilePanel);

        // --- LADO DIREITO: Dados da Loja ---
        JPanel storePanel = new JPanel(new BorderLayout(10, 10));
        storePanel.setBackground(UIUtils.COLOR_WHITE);
        storePanel.setBorder(BorderFactory.createTitledBorder("Configurações da Loja"));

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        formPanel.setBackground(UIUtils.COLOR_WHITE);

        formPanel.add(UIUtils.createLabel("Nome da Unidade:"));
        txtLojaNome = new JTextField();
        UIUtils.styleTextField(txtLojaNome);
        formPanel.add(txtLojaNome);

        formPanel.add(UIUtils.createLabel("CNPJ:"));
        txtLojaCNPJ = new JTextField();
        UIUtils.styleTextField(txtLojaCNPJ);
        formPanel.add(txtLojaCNPJ);

        formPanel.add(UIUtils.createLabel("Endereço Fiscal:"));
        txtLojaEndereco = new JTextField();
        UIUtils.styleTextField(txtLojaEndereco);
        formPanel.add(txtLojaEndereco);

        formPanel.add(UIUtils.createLabel("Telefone:"));
        txtLojaTelefone = new JTextField();
        UIUtils.styleTextField(txtLojaTelefone);
        formPanel.add(txtLojaTelefone);

        formPanel.add(UIUtils.createLabel("Tema Visual:"));
        cbTema = new JComboBox<>(new String[]{"Light", "Dark"});
        formPanel.add(cbTema);

        storePanel.add(formPanel, BorderLayout.CENTER);

        JButton btnSalvar = UIUtils.createButton("Salvar Configurações", UIUtils.COLOR_SUCCESS);
        btnSalvar.addActionListener(e -> salvarConfigs());
        storePanel.add(btnSalvar, BorderLayout.SOUTH);

        mainPanel.add(storePanel);

        add(mainPanel, BorderLayout.CENTER);
        carregarConfigs();
    }

    private JLabel createProfileLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void carregarConfigs() {
        Map<String, String> configs = configDAO.listarTodas();
        txtLojaNome.setText(configs.getOrDefault("loja_nome", ""));
        txtLojaCNPJ.setText(configs.getOrDefault("loja_cnpj", ""));
        txtLojaEndereco.setText(configs.getOrDefault("loja_endereco", ""));
        txtLojaTelefone.setText(configs.getOrDefault("loja_telefone", ""));
        String tema = configs.getOrDefault("loja_tema", "Light");
        cbTema.setSelectedItem(tema);
    }

    private void salvarConfigs() {
        configDAO.salvar("loja_nome", txtLojaNome.getText());
        configDAO.salvar("loja_cnpj", txtLojaCNPJ.getText());
        configDAO.salvar("loja_endereco", txtLojaEndereco.getText());
        configDAO.salvar("loja_telefone", txtLojaTelefone.getText());
        
        String temaSelecionado = cbTema.getSelectedItem().toString();
        configDAO.salvar("loja_tema", temaSelecionado);
        
        UIUtils.applyTheme(temaSelecionado);
        
        JOptionPane.showMessageDialog(this, "Configurações salvas e tema aplicado com sucesso!");
    }
}
