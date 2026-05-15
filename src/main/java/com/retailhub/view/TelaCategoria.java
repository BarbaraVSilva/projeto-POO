package com.retailhub.view;

import com.retailhub.dao.CategoriaDAO;
import com.retailhub.exception.ValidacaoException;
import com.retailhub.model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaCategoria extends JPanel {

    private CategoriaDAO dao = new CategoriaDAO();
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JTextField txtId, txtNome, txtDescricao, txtComissao, txtPrazo, txtDataCadastro;
    private JComboBox<String> cbStatus;

    public TelaCategoria() {
        if (com.retailhub.model.Sessao.getUsuarioLogado() == null || !com.retailhub.model.Sessao.getUsuarioLogado().isAdmin()) {
            setLayout(new BorderLayout());
            add(new JLabel("Acesso Negado: Apenas administradores podem gerenciar categorias.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Painel Superior (Título, Formulário, Botões)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(UIUtils.COLOR_WHITE);
        
        // Título Superior
        JLabel lblTitle = new JLabel("Gerenciamento de Categorias", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulário (7 campos)
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBackground(UIUtils.COLOR_WHITE);
        
        formPanel.add(UIUtils.createLabel("ID (Automático):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        UIUtils.styleTextField(txtId);
        formPanel.add(txtId);

        formPanel.add(UIUtils.createLabel("Nome:"));
        txtNome = new JTextField();
        UIUtils.styleTextField(txtNome);
        formPanel.add(txtNome);

        formPanel.add(UIUtils.createLabel("Descrição:"));
        txtDescricao = new JTextField();
        UIUtils.styleTextField(txtDescricao);
        formPanel.add(txtDescricao);

        formPanel.add(UIUtils.createLabel("Taxa Comissão (%):"));
        txtComissao = new JTextField("0.0");
        UIUtils.styleTextField(txtComissao);
        formPanel.add(txtComissao);

        formPanel.add(UIUtils.createLabel("Prazo Envio (Dias):"));
        txtPrazo = new JTextField("0");
        UIUtils.styleTextField(txtPrazo);
        formPanel.add(txtPrazo);

        formPanel.add(UIUtils.createLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"Ativo", "Inativo"});
        formPanel.add(cbStatus);

        formPanel.add(UIUtils.createLabel("Data Cadastro:"));
        txtDataCadastro = new JTextField(LocalDate.now().toString());
        UIUtils.styleTextField(txtDataCadastro);
        formPanel.add(txtDataCadastro);

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
        tableModel = new DefaultTableModel(new String[]{"ID", "Nome", "Comissão %", "Prazo", "Status", "Data"}, 0);
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
            if (txtNome.getText().trim().isEmpty()) {
                throw new ValidacaoException("O nome da categoria é obrigatório!");
            }

            Categoria c = new Categoria();
            c.setNome(txtNome.getText());
            c.setDescricao(txtDescricao.getText());
            c.setTaxaComissao(Double.parseDouble(txtComissao.getText().replace(",", ".")));
            c.setPrazoEnvio(Integer.parseInt(txtPrazo.getText()));
            c.setStatusAtivo(cbStatus.getSelectedIndex() == 0 ? 1 : 0);
            c.setDataCadastro(txtDataCadastro.getText());

            if (!txtId.getText().isEmpty()) {
                c.setId(Integer.parseInt(txtId.getText()));
            }

            if (c.getId() == 0) {
                dao.inserir(c);
                LogService.registrar("Cadastrou a categoria: " + c.getNome());
                JOptionPane.showMessageDialog(this, "Categoria inserida com sucesso!");
            } else {
                dao.atualizar(c);
                LogService.registrar("Atualizou a categoria: " + c.getNome() + " (ID: " + c.getId() + ")");
                JOptionPane.showMessageDialog(this, "Categoria atualizada com sucesso!");
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
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir esta categoria?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.excluir(Integer.parseInt(txtId.getText()));
                LogService.registrar("Excluiu a categoria ID: " + txtId.getText());
                limparFormulario();
                carregarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para excluir.");
        }
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Categoria> lista = dao.listarTodas();
        for (Categoria c : lista) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getNome(), c.getTaxaComissao(), c.getPrazoEnvio(), c.getStatusAtivo() == 1 ? "Ativo" : "Inativo", c.getDataCadastro()
            });
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            txtId.setText(tabela.getValueAt(row, 0).toString());
            txtNome.setText(tabela.getValueAt(row, 1).toString());
            txtComissao.setText(tabela.getValueAt(row, 2).toString());
            txtPrazo.setText(tabela.getValueAt(row, 3).toString());
            cbStatus.setSelectedItem(tabela.getValueAt(row, 4).toString());
            txtDataCadastro.setText(tabela.getValueAt(row, 5).toString());
            
            // Busca descrição no DAO ou mantém cache (simplificado aqui como recarregar do DAO)
            // Para simplicidade na tabela, não mostramos a descrição longa
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtDescricao.setText("");
        txtComissao.setText("0.0");
        txtPrazo.setText("0");
        cbStatus.setSelectedIndex(0);
        tabela.clearSelection();
    }
}
