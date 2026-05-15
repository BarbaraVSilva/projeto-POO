package com.retailhub.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UIUtils {

    public static final Color COLOR_ORANGE = new Color(238, 77, 45);
    public static Color COLOR_WHITE = Color.WHITE;
    public static Color COLOR_DARK = new Color(51, 51, 51);
    public static Color COLOR_BACKGROUND = new Color(245, 245, 245);
    public static final Color COLOR_SUCCESS = new Color(40, 167, 69);
    public static final Color COLOR_DANGER = new Color(220, 53, 69);

    public static void applyTheme(String theme) {
        try {
            if ("Dark".equalsIgnoreCase(theme)) {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
                COLOR_WHITE = new Color(45, 45, 45);
                COLOR_DARK = new Color(220, 220, 220);
                COLOR_BACKGROUND = new Color(30, 30, 30);
            } else {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
                COLOR_WHITE = Color.WHITE;
                COLOR_DARK = new Color(51, 51, 51);
                COLOR_BACKGROUND = new Color(245, 245, 245);
            }
            // Atualiza o UIManager com as cores customizadas para que componentes novos as usem
            UIManager.put("Panel.background", COLOR_BACKGROUND);
            
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(COLOR_BACKGROUND);
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_ORANGE);
        button.setForeground(Color.WHITE); // Manter branco fixo para legibilidade no laranja
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fonte um pouco menor
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }
    
    public static JButton createButton(String text, Color bgColor) {
        JButton button = createButton(text);
        button.setBackground(bgColor);
        return button;
    }
    
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(COLOR_DARK);
        label.setFont(FONT_BOLD);
        return label;
    }

    public static JLabel createMenuLabel(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 9)); // Fonte mais compacta
        label.setForeground(Color.GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 5)); // Padding mínimo
        return label;
    }
    
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_REGULAR);
        textField.putClientProperty("JComponent.roundRect", true);
        textField.setMargin(new Insets(3, 5, 3, 5)); // Reduzido para compactação
    }

    public static void styleTable(JTable table) {
        table.getTableHeader().setBackground(COLOR_ORANGE);
        table.getTableHeader().setForeground(Color.WHITE); // Manter contraste no cabeçalho
        table.getTableHeader().setFont(FONT_BOLD);
        table.setFont(FONT_REGULAR);
        table.setRowHeight(30); // Linhas mais compactas
        table.setSelectionBackground(new Color(255, 200, 180));
        table.setSelectionForeground(COLOR_DARK);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Centralizar texto nas células
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public static ImageIcon loadIcon(String path) {
        try {
            java.net.URL imgURL = UIUtils.class.getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                // Fallback para carregamento via arquivo se não estiver no classpath (durante desenvolvimento)
                java.io.File file = new java.io.File("src/main/resources" + path);
                if (file.exists()) {
                    return new ImageIcon(file.getAbsolutePath());
                }
                System.err.println("Não foi possível encontrar o ícone: " + path);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ImageIcon loadScaledIcon(String path, int width, int height) {
        ImageIcon icon = loadIcon(path);
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}
