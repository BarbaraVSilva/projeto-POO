package com.retailhub.view;

import com.retailhub.dao.CategoriaDAO;
import com.retailhub.dao.ProdutoDAO;
import com.retailhub.exception.ValidacaoException;
import com.retailhub.model.Categoria;
import com.retailhub.model.Produto;
import com.retailhub.model.ProdutoDigital;
import com.retailhub.model.ProdutoFisico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class TelaProduto extends JPanel {

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private JTextField txtId, txtNome, txtSku, txtDescricao, txtPreco, txtPrecoCusto, txtPeso, txtFrete, txtEstoque;
    private JComboBox<Categoria> cbCategoria;
    private JRadioButton rbFisico, rbDigital;
    private JLabel lblPeso, lblFrete, lblPreviewImagem;
    private String selectedImagePath = null;

    public TelaProduto() {
        if (com.retailhub.model.Sessao.getUsuarioLogado() == null || !com.retailhub.model.Sessao.getUsuarioLogado().isAdmin()) {
            setLayout(new BorderLayout());
            add(new JLabel("Acesso Negado: Apenas administradores podem gerenciar produtos.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(UIUtils.COLOR_WHITE);

        // Painel Superior (Título, Formulário, Botões)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(UIUtils.COLOR_WHITE);

        // Título Superior
        JLabel lblTitle = new JLabel("Gerenciamento de Produtos", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulário (Usando GridBagLayout para melhor redimensionamento)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.COLOR_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 5, 1, 5);
        gbc.weightx = 1.0;

        int rowCount = 0;

        // ID
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("ID (Automático):"), gbc);
        txtId = new JTextField(); txtId.setEditable(false); UIUtils.styleTextField(txtId);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtId, gbc);

        // Tipo
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Tipo de Produto:"), gbc);
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(UIUtils.COLOR_WHITE);
        rbFisico = new JRadioButton("Físico"); rbDigital = new JRadioButton("Digital");
        rbFisico.setBackground(UIUtils.COLOR_WHITE); rbDigital.setBackground(UIUtils.COLOR_WHITE);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbFisico); bg.add(rbDigital);
        rbFisico.setSelected(true);
        rbFisico.addActionListener(e -> toggleCamposFisicos(true));
        rbDigital.addActionListener(e -> toggleCamposFisicos(false));
        typePanel.add(rbFisico); typePanel.add(rbDigital);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(typePanel, gbc);

        // Nome
        txtNome = new JTextField(); UIUtils.styleTextField(txtNome);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Título do Anúncio:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtNome, gbc);

        // SKU
        txtSku = new JTextField(); UIUtils.styleTextField(txtSku);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("SKU Interno:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtSku, gbc);

        // Descrição
        txtDescricao = new JTextField(); UIUtils.styleTextField(txtDescricao);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtDescricao, gbc);

        // Preço
        txtPreco = new JTextField(); UIUtils.styleTextField(txtPreco);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Preço de Venda:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtPreco, gbc);

        // Custo
        txtPrecoCusto = new JTextField(); UIUtils.styleTextField(txtPrecoCusto);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Preço de Custo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtPrecoCusto, gbc);

        // Estoque
        txtEstoque = new JTextField("0"); UIUtils.styleTextField(txtEstoque);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Estoque:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtEstoque, gbc);

        // Categoria
        cbCategoria = new JComboBox<>(); carregarComboCategorias();
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(cbCategoria, gbc);

        // Peso
        lblPeso = UIUtils.createLabel("Peso (kg):");
        txtPeso = new JTextField(); UIUtils.styleTextField(txtPeso);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(lblPeso, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtPeso, gbc);

        // Frete
        lblFrete = UIUtils.createLabel("Frete (R$):");
        txtFrete = new JTextField(); UIUtils.styleTextField(txtFrete);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(lblFrete, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(txtFrete, gbc);

        // Imagem
        JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imgPanel.setBackground(UIUtils.COLOR_WHITE);
        JButton btnSelecionar = new JButton("📷 Selecionar");
        lblPreviewImagem = new JLabel("Nenhuma imagem");
        lblPreviewImagem.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        btnSelecionar.addActionListener(e -> selecionarImagem());
        imgPanel.add(btnSelecionar); imgPanel.add(lblPreviewImagem);
        gbc.gridy = rowCount++; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(UIUtils.createLabel("Imagem:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formPanel.add(imgPanel, gbc);

        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // Espaçador
        JPanel spacer = new JPanel(); spacer.setBackground(UIUtils.COLOR_WHITE);
        gbc.gridy = rowCount++; gbc.weighty = 1.0;
        formPanel.add(spacer, gbc);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(UIUtils.COLOR_WHITE);
        
        JButton btnSalvar = UIUtils.createButton("Salvar", UIUtils.COLOR_SUCCESS);
        btnSalvar.addActionListener(e -> salvar());
        
        JButton btnExcluir = UIUtils.createButton("Excluir", UIUtils.COLOR_DANGER);
        btnExcluir.addActionListener(e -> excluir());
        
        JButton btnLimpar = UIUtils.createButton("Limpar");
        btnLimpar.addActionListener(e -> limparFormulario());

        JButton btnImportar = UIUtils.createButton("Importar CSV", new Color(100, 100, 255));
        btnImportar.addActionListener(e -> importarCSV());

        btnPanel.add(btnSalvar);
        btnPanel.add(btnExcluir);
        btnPanel.add(btnLimpar);
        btnPanel.add(btnImportar);
        
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(new String[]{"ID", "Tipo", "Nome", "Descrição", "Preço", "Categoria", "Estoque", "Taxa Calc."}, 0);
        tabela = new JTable(tableModel);
        UIUtils.styleTable(tabela);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherFormulario());
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        carregarTabela();
    }

    private void toggleCamposFisicos(boolean enabled) {
        txtPeso.setEnabled(enabled);
        txtFrete.setEnabled(enabled);
        if (!enabled) {
            txtPeso.setText("");
            txtFrete.setText("");
        }
    }

    private void selecionarImagem() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            lblPreviewImagem.setText(selectedFile.getName());
            lblPreviewImagem.setForeground(UIUtils.COLOR_SUCCESS);
        }
    }

    private String salvarImagem(String pathOriginal) {
        if (pathOriginal == null || pathOriginal.isEmpty()) return null;
        try {
            File original = new File(pathOriginal);
            if (!original.exists()) return null;

            File uploadDir = new File("uploads/products");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String extensao = "";
            int i = pathOriginal.lastIndexOf('.');
            if (i > 0) extensao = pathOriginal.substring(i);
            
            String novoNome = UUID.randomUUID().toString() + extensao;
            File destino = new File(uploadDir, novoNome);
            
            Files.copy(original.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "uploads/products/" + novoNome;
        } catch (Exception e) {
            System.err.println("Erro ao salvar imagem: " + e.getMessage());
            return null;
        }
    }

    private void carregarComboCategorias() {
        cbCategoria.removeAllItems();
        List<Categoria> categorias = categoriaDAO.listarTodas();
        for (Categoria c : categorias) {
            cbCategoria.addItem(c);
        }
    }

    private void salvar() {
        try {
            if (txtNome.getText().trim().isEmpty()) {
                throw new ValidacaoException("O nome do produto é obrigatório!");
            }
            if (cbCategoria.getSelectedItem() == null) {
                throw new ValidacaoException("Selecione uma categoria válida!");
            }
            double preco, precoCusto;
            try {
                preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
                precoCusto = Double.parseDouble(txtPrecoCusto.getText().replace(",", "."));
                if (preco < 0 || precoCusto < 0) throw new ValidacaoException("Os preços não podem ser negativos.");
            } catch (NumberFormatException ex) {
                throw new ValidacaoException("Preço(s) inválido(s)!");
            }

            int quantidadeEstoque = 0;
            try {
                if (!txtEstoque.getText().trim().isEmpty()) {
                    quantidadeEstoque = Integer.parseInt(txtEstoque.getText().trim());
                    if (quantidadeEstoque < 0) throw new ValidacaoException("O estoque não pode ser negativo.");
                }
            } catch (NumberFormatException ex) {
                throw new ValidacaoException("Estoque inválido!");
            }

            Produto p;
            if (rbFisico.isSelected()) {
                double peso = txtPeso.getText().isEmpty() ? 0 : Double.parseDouble(txtPeso.getText().replace(",", "."));
                double frete = txtFrete.getText().isEmpty() ? 0 : Double.parseDouble(txtFrete.getText().replace(",", "."));
                ProdutoFisico pf = new ProdutoFisico();
                pf.setPrecoCusto(precoCusto);
                pf.setPeso(peso);
                pf.setFrete(frete);
                p = pf;
            } else {
                p = new ProdutoDigital();
                p.setPrecoCusto(precoCusto);
            }

            if (txtNome.getText().length() > 120) {
                throw new com.retailhub.exception.ValidacaoException("O título do anúncio deve ter no máximo 120 caracteres!");
            }

            p.setNome(txtNome.getText());
            p.setSku(txtSku.getText());
            p.setDescricao(txtDescricao.getText());
            p.setPreco(preco);
            p.setCategoria((Categoria) cbCategoria.getSelectedItem());
            p.setQuantidadeEstoque(quantidadeEstoque);
            
            // Lógica de Imagem
            if (selectedImagePath != null) {
                String novoPath = salvarImagem(selectedImagePath);
                if (novoPath != null) p.setImagemPath(novoPath);
            }

            if (txtId.getText().isEmpty()) {
                produtoDAO.inserir(p);
                LogService.registrar("Cadastrou o produto: " + p.getNome());
                JOptionPane.showMessageDialog(this, "Produto inserido com sucesso!");
            } else {
                p.setId(Integer.parseInt(txtId.getText()));
                produtoDAO.atualizar(p);
                LogService.registrar("Atualizou o produto: " + p.getNome() + " (ID: " + p.getId() + ")");
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            }
            limparFormulario();
            carregarTabela();
        } catch (com.retailhub.exception.SkuDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "SKU Duplicado", JOptionPane.WARNING_MESSAGE);
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
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este produto?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                produtoDAO.excluir(Integer.parseInt(txtId.getText()));
                LogService.registrar("Excluiu o produto ID: " + txtId.getText());
                limparFormulario();
                carregarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
        }
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Produto> lista = produtoDAO.listarTodos();
        for (Produto p : lista) {
            String tipo = (p instanceof ProdutoFisico) ? "Físico" : "Digital";
            // Polimorfismo aqui: p.calcularTaxa() invoca o método correto baseado na instância
            tableModel.addRow(new Object[]{
                p.getId(), tipo, p.getNome(), p.getDescricao(), 
                String.format("R$ %.2f", p.getPreco()), 
                p.getCategoria().getNome(),
                p.getQuantidadeEstoque(),
                String.format("R$ %.2f", p.calcularTaxa())
            });
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            int id = Integer.parseInt(tabela.getValueAt(row, 0).toString());
            Produto p = null;
            // Procurar na lista para obter a instância real
            for (Produto prod : produtoDAO.listarTodos()) {
                if (prod.getId() == id) {
                    p = prod;
                    break;
                }
            }

            if (p != null) {
                txtId.setText(String.valueOf(p.getId()));
                txtNome.setText(p.getNome());
                txtSku.setText(p.getSku());
                txtDescricao.setText(p.getDescricao());
                txtPreco.setText(String.valueOf(p.getPreco()));
                txtPrecoCusto.setText(String.valueOf(p.getPrecoCusto()));
                txtEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));
                
                String nomeCategoria = tabela.getValueAt(row, 5).toString();
                for (int i = 0; i < cbCategoria.getItemCount(); i++) {
                    if (cbCategoria.getItemAt(i).getNome().equals(nomeCategoria)) {
                        cbCategoria.setSelectedIndex(i);
                        break;
                    }
                }

                if (p instanceof ProdutoFisico) {
                    rbFisico.setSelected(true);
                    toggleCamposFisicos(true);
                    txtPeso.setText(String.valueOf(((ProdutoFisico) p).getPeso()));
                    txtFrete.setText(String.valueOf(((ProdutoFisico) p).getFrete()));
                } else {
                    rbDigital.setSelected(true);
                    toggleCamposFisicos(false);
                }
                
                if (p.getImagemPath() != null && !p.getImagemPath().isEmpty()) {
                    lblPreviewImagem.setText("Imagem carregada ✅");
                    lblPreviewImagem.setForeground(UIUtils.COLOR_SUCCESS);
                    selectedImagePath = null; // Não resetar para null se quiser manter a mesma
                } else {
                    lblPreviewImagem.setText("Sem imagem");
                    lblPreviewImagem.setForeground(Color.GRAY);
                }
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtSku.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtPrecoCusto.setText("");
        txtEstoque.setText("0");
        txtPeso.setText("");
        txtFrete.setText("");
        rbFisico.setSelected(true);
        toggleCamposFisicos(true);
        if (cbCategoria.getItemCount() > 0) cbCategoria.setSelectedIndex(0);
        selectedImagePath = null;
        lblPreviewImagem.setText("Nenhuma imagem");
        lblPreviewImagem.setForeground(Color.GRAY);
        tabela.clearSelection();
    }

    private void importarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos CSV", "csv"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(selectedFile))) {
                String line;
                int count = 0;
                int errors = 0;
                br.readLine(); // Pular cabeçalho: Nome;SKU;Descricao;Preco;PrecoCusto;Categoria;Estoque;Tipo
                
                List<Categoria> categorias = categoriaDAO.listarTodas();
                
                while ((line = br.readLine()) != null) {
                    try {
                        String[] data = line.split(";");
                        if (data.length < 8) continue;

                        String nome = data[0].trim();
                        String sku = data[1].trim();
                        String desc = data[2].trim();
                        double preco = Double.parseDouble(data[3].replace(",", "."));
                        double precoCusto = Double.parseDouble(data[4].replace(",", "."));
                        String catNome = data[5].trim();
                        int estoque = Integer.parseInt(data[6].trim());
                        String tipo = data[7].trim();

                        Categoria cat = null;
                        for (Categoria c : categorias) {
                            if (c.getNome().equalsIgnoreCase(catNome)) {
                                cat = c;
                                break;
                            }
                        }
                        if (cat == null) {
                            // Criar categoria padrão se não existir
                            cat = new Categoria(0, catNome, "Importada automaticamente", 1, 10.0, 7, "");
                            categoriaDAO.inserir(cat);
                            categorias = categoriaDAO.listarTodas(); // Atualiza lista
                            for (Categoria c : categorias) {
                                if (c.getNome().equalsIgnoreCase(catNome)) { cat = c; break; }
                            }
                        }

                        Produto p = tipo.equalsIgnoreCase("FISICO") ? new ProdutoFisico() : new ProdutoDigital();
                        p.setNome(nome);
                        p.setSku(sku);
                        p.setDescricao(desc);
                        p.setPreco(preco);
                        p.setPrecoCusto(precoCusto);
                        p.setCategoria(cat);
                        p.setQuantidadeEstoque(estoque);
                        
                        produtoDAO.inserir(p);
                        count++;
                    } catch (Exception ex) {
                        errors++;
                    }
                }
                carregarTabela();
                JOptionPane.showMessageDialog(this, "Importação finalizada!\nSucesso: " + count + "\nErros: " + errors);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao ler arquivo: " + ex.getMessage());
            }
        }
    }
}
