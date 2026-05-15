package com.retailhub.view;

import com.retailhub.model.Usuario;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.retailhub.dao.ConnectionFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TelaPrincipal extends JFrame {

    private Usuario usuarioLogado;
    private JPanel contentPanel;
    private Timer inactivityTimer;
    private Timer warningTimer;
    private static final int TIMEOUT_MINUTES = 15;
    private static final int WARNING_SECONDS = 30;

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        
        setTitle("RetailHub - " + usuario.getPerfil());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtils.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Menu Lateral
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Removido setBackground para permitir que o FlatLaf gerencie a cor do tema
        menuPanel.setPreferredSize(new Dimension(240, 0));
        menuPanel.setMinimumSize(new Dimension(260, 0));

        // Logo e Título
        ImageIcon logoIcon = UIUtils.loadScaledIcon("/icons/logo.png", 60, 60);
        if (logoIcon != null) {
            JLabel lblLogo = new JLabel(logoIcon);
            lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuPanel.add(lblLogo);
            menuPanel.add(Box.createVerticalStrut(10));
        }

        JLabel lblTitle = new JLabel("RetailHub");
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.COLOR_ORANGE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(lblTitle);
        menuPanel.add(Box.createVerticalStrut(5));
        
        JLabel lblUser = new JLabel("👤 " + usuario.getLogin());
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblUser.setForeground(Color.GRAY);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(lblUser);
        menuPanel.add(Box.createVerticalStrut(15));

        // --- SEÇÃO: CATÁLOGO ---
        menuPanel.add(UIUtils.createMenuLabel("Catálogo"));
        
        JButton btnCategorias = UIUtils.createButton("📁 Categorias");
        btnCategorias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnCategorias.addActionListener(e -> showPanel(new TelaCategoria()));
        menuPanel.add(btnCategorias);
        menuPanel.add(Box.createVerticalStrut(5));

        JButton btnProdutos = UIUtils.createButton("📦 Anúncios/Produtos");
        btnProdutos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnProdutos.addActionListener(e -> showPanel(new TelaProduto()));
        menuPanel.add(btnProdutos);
        menuPanel.add(Box.createVerticalStrut(5));

        // --- SEÇÃO: VENDAS ---
        menuPanel.add(UIUtils.createMenuLabel("Operações de Venda"));
        
        JButton btnVendas = UIUtils.createButton("🛒 Ponto de Venda (PDV)");
        btnVendas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnVendas.addActionListener(e -> showPanel(new TelaVenda()));
        menuPanel.add(btnVendas);
        menuPanel.add(Box.createVerticalStrut(5));

        JButton btnFechamento = UIUtils.createButton("💰 Fechamento de Caixa");
        btnFechamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnFechamento.addActionListener(e -> showPanel(new TelaFechamentoCaixa()));
        menuPanel.add(btnFechamento);
        menuPanel.add(Box.createVerticalStrut(5));

        JButton btnDevolucao = UIUtils.createButton("🔄 Trocas e Devoluções");
        btnDevolucao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnDevolucao.addActionListener(e -> showPanel(new TelaDevolucao()));
        menuPanel.add(btnDevolucao);
        menuPanel.add(Box.createVerticalStrut(5));

        // --- SEÇÃO: EXPEDIÇÃO ---
        menuPanel.add(UIUtils.createMenuLabel("Expedição"));
        
        JButton btnTriagem = UIUtils.createButton("🚚 Triagem (Expedição)");
        btnTriagem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnTriagem.addActionListener(e -> showPanel(new TelaExpedicao()));
        menuPanel.add(btnTriagem);
        menuPanel.add(Box.createVerticalStrut(5));

        // --- SEÇÃO: FINANCEIRO ---
        menuPanel.add(UIUtils.createMenuLabel("Financeiro"));
        
        JButton btnFinanceiro = UIUtils.createButton("📜 Consulta de Pedidos");
        btnFinanceiro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnFinanceiro.addActionListener(e -> showPanel(new TelaPedidoFinanceiro()));
        menuPanel.add(btnFinanceiro);
        menuPanel.add(Box.createVerticalStrut(5));

        JButton btnDashBI = UIUtils.createButton("📊 Dashboard BI");
        btnDashBI.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnDashBI.addActionListener(e -> showPanel(new TelaDashBI()));
        menuPanel.add(btnDashBI);
        menuPanel.add(Box.createVerticalStrut(5));

        JButton btnNotaFiscal = UIUtils.createButton("📝 Gerar Nota Fiscal");
        btnNotaFiscal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnNotaFiscal.addActionListener(e -> showPanel(new TelaNotaFiscal()));
        menuPanel.add(btnNotaFiscal);
        menuPanel.add(Box.createVerticalStrut(5));

        // --- SEÇÃO: CONFIGURAÇÕES ---
        menuPanel.add(UIUtils.createMenuLabel("Configurações"));
        
        if (usuario.isAdmin()) {
            JButton btnUsuarios = UIUtils.createButton("Gerenciar Usuários");
            btnUsuarios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            btnUsuarios.addActionListener(e -> showPanel(new TelaUsuario()));
            menuPanel.add(btnUsuarios);
            menuPanel.add(btnUsuarios);
            menuPanel.add(Box.createVerticalStrut(3));
        }

        JButton btnClientes = UIUtils.createButton("👥 Base de Clientes");
        btnClientes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnClientes.addActionListener(e -> showPanel(new TelaCliente()));
        menuPanel.add(btnClientes);
        menuPanel.add(Box.createVerticalStrut(5));

        JButton btnConfigLoja = UIUtils.createButton("⚙️ Configurações Loja");
        btnConfigLoja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnConfigLoja.addActionListener(e -> showPanel(new TelaConfiguracoes()));
        menuPanel.add(btnConfigLoja);

        // --- SEÇÃO: MINHA CONTA ---
        menuPanel.add(Box.createVerticalGlue()); // Empurra o restante para o final
        menuPanel.add(UIUtils.createMenuLabel("Minha Conta"));
        
        JButton btnAlterarSenha = UIUtils.createButton("🔑 Alterar Senha");
        btnAlterarSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnAlterarSenha.addActionListener(e -> showPanel(new TelaAlterarSenha()));
        menuPanel.add(btnAlterarSenha);
        menuPanel.add(Box.createVerticalStrut(5));

        JButton btnSair = UIUtils.createButton("🚪 Sair", UIUtils.COLOR_DANGER);
        btnSair.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnSair.addActionListener(e -> efetuarLogout("Fez logoff do sistema."));
        menuPanel.add(btnSair);

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setBorder(null);
        menuScroll.setOpaque(false);
        menuScroll.getViewport().setOpaque(false);
        menuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);
        menuScroll.setPreferredSize(new Dimension(240, 0));
        
        mainPanel.add(menuScroll, BorderLayout.WEST);

        // Barra Superior (Header) para o botão de esconder/abrir menu
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Removido background para seguir o tema
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200, 50))); 
        
        JButton btnToggleMenu = new JButton("☰ Menu");
        btnToggleMenu.setFont(UIUtils.FONT_TITLE);
        btnToggleMenu.setForeground(UIUtils.COLOR_ORANGE);
        btnToggleMenu.setBackground(UIUtils.COLOR_WHITE);
        btnToggleMenu.setFocusPainted(false);
        btnToggleMenu.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnToggleMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleMenu.addActionListener(e -> {
            menuPanel.setVisible(!menuPanel.isVisible());
        });
        
        headerPanel.add(btnToggleMenu);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Painel Central Dinâmico
        contentPanel = new JPanel(new BorderLayout());
        // Removido background para seguir o tema
        
        contentPanel.add(criarDashboard(), BorderLayout.CENTER); // Tela inicial
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setupInactivityTimer();
    }
    
    private void setupInactivityTimer() {
        // Timer principal de 15 minutos (menos os 30s de aviso)
        int delay = (TIMEOUT_MINUTES * 60 * 1000) - (WARNING_SECONDS * 1000);
        inactivityTimer = new Timer(delay, e -> showWarning());
        inactivityTimer.setRepeats(false);
        
        // Listener global para resetar o timer em qualquer ação
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (inactivityTimer.isRunning()) {
                inactivityTimer.restart();
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        
        inactivityTimer.start();
    }
    
    private void showWarning() {
        LogService.registrar("Aviso de inatividade exibido.");
        int choice = JOptionPane.showOptionDialog(this, 
            "Sua sessão expirará em " + WARNING_SECONDS + " segundos por inatividade.\nDeseja continuar logado?", 
            "Aviso de Sessão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE, 
            null, new String[]{"Sim, continuar", "Sair agora"}, "Sim, continuar");
            
        if (choice == JOptionPane.YES_OPTION) {
            inactivityTimer.restart();
        } else {
            efetuarLogout("Sessão encerrada pelo usuário no aviso.");
        }
    }
    
    private void efetuarLogout(String motivo) {
        if (inactivityTimer != null) inactivityTimer.stop();
        LogService.registrar(motivo);
        com.retailhub.model.Sessao.setUsuarioLogado(null);
        new TelaLogin().setVisible(true);
        this.dispose();
    }
    
    
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        // Garante que o painel interno também siga o tema
        SwingUtilities.updateComponentTreeUI(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel criarDashboard() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(5, 5));
        dashboardPanel.setBackground(UIUtils.COLOR_WHITE);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Topo: Painel de KPIs
        dashboardPanel.add(criarPainelKPIs(), BorderLayout.NORTH);

        // 2. Centro: Gráficos
        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        chartsPanel.setBackground(UIUtils.COLOR_WHITE);

        chartsPanel.add(new ChartPanel(criarGraficoCategoria(), true));
        chartsPanel.add(new ChartPanel(criarGraficoTopProdutos(), true));
        chartsPanel.add(new ChartPanel(criarGraficoPagamentos(), true));
        chartsPanel.add(criarPainelEstoqueBaixo());

        dashboardPanel.add(chartsPanel, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private JPanel criarPainelKPIs() {
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        kpiPanel.setBackground(UIUtils.COLOR_WHITE);

        double faturamento = 0;
        double lucro = 0;
        int totalVendas = 0;

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Faturamento e Total Vendas
            ResultSet rsVendas = stmt.executeQuery("SELECT count(*) as total, sum(iv.quantidade * iv.preco_unitario) as faturamento " +
                                                   "FROM vendas v JOIN itens_venda iv ON v.id = iv.venda_id " +
                                                   "WHERE v.status_venda = 'CONCLUIDA'");
            if (rsVendas.next()) {
                totalVendas = rsVendas.getInt("total");
                faturamento = rsVendas.getDouble("faturamento");
            }
            
            // Lucro Estimado (Preço Venda - Preço Custo)
            ResultSet rsLucro = stmt.executeQuery("SELECT sum(iv.quantidade * (iv.preco_unitario - p.preco_costo)) as lucro " +
                                                 "FROM itens_venda iv JOIN produtos p ON iv.produto_id = p.id " +
                                                 "JOIN vendas v ON iv.venda_id = v.id WHERE v.status_venda = 'CONCLUIDA'");
            if (rsLucro.next()) {
                lucro = rsLucro.getDouble("lucro");
            }

        } catch (Exception e) { e.printStackTrace(); }

        kpiPanel.add(criarCardKPI("💰 Faturamento Total", String.format("R$ %.2f", faturamento), UIUtils.COLOR_ORANGE));
        kpiPanel.add(criarCardKPI("📈 Lucro Bruto", String.format("R$ %.2f", lucro), UIUtils.COLOR_SUCCESS));
        kpiPanel.add(criarCardKPI("🎫 Ticket Médio", String.format("R$ %.2f", totalVendas > 0 ? faturamento/totalVendas : 0), new Color(100, 100, 255)));
        kpiPanel.add(criarCardKPI("📦 Total Pedidos", String.valueOf(totalVendas), Color.GRAY));

        return kpiPanel;
    }

    private JPanel criarCardKPI(String titulo, String valor, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        // Removido background fixo
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTit.setForeground(Color.GRAY);
        
        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(color);

        card.add(lblTit);
        card.add(lblVal);
        return card;
    }

    private JFreeChart criarGraficoCategoria() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT c.nome, count(p.id) as qtd FROM categorias c LEFT JOIN produtos p ON c.id = p.categoria_id GROUP BY c.nome")) {
             while (rs.next()) {
                 dataset.setValue(rs.getString("nome"), rs.getInt("qtd"));
             }
        } catch (Exception e) {}
        return ChartFactory.createPieChart("Produtos por Categoria", dataset, true, true, false);
    }

    private JFreeChart criarGraficoTopProdutos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.nome, sum(iv.quantidade) as total FROM itens_venda iv JOIN produtos p ON iv.produto_id = p.id GROUP BY p.nome ORDER BY total DESC LIMIT 5")) {
             while (rs.next()) {
                 dataset.addValue(rs.getInt("total"), "Vendas", rs.getString("nome"));
             }
        } catch (Exception e) {}
        return ChartFactory.createBarChart("Top 5 Produtos", "Produto", "Qtd", dataset, PlotOrientation.VERTICAL, false, true, false);
    }

    private JFreeChart criarGraficoPagamentos() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT metodo_pagamento, count(*) as qtd FROM vendas GROUP BY metodo_pagamento")) {
             while (rs.next()) {
                 dataset.setValue(rs.getString("metodo_pagamento"), rs.getInt("qtd"));
             }
        } catch (Exception e) {}
        return ChartFactory.createPieChart("Métodos de Pagamento", dataset, true, true, false);
    }

    private JPanel criarPainelEstoqueBaixo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("⚠️ Alerta de Estoque Baixo"));

        String[] colunas = {"Produto", "Qtd"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, quantidade_estoque FROM produtos WHERE quantidade_estoque < 10 ORDER BY quantidade_estoque ASC LIMIT 10")) {
             while (rs.next()) {
                 model.addRow(new Object[]{rs.getString("nome"), rs.getInt("quantidade_estoque")});
             }
        } catch (Exception e) {}

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
