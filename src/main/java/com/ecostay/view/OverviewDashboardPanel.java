package com.ecostay.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class OverviewDashboardPanel extends JPanel {
    
    private final Color WHITE_PANEL = Color.WHITE;
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color SOFT_BORDER = new Color(226, 232, 240);

    public OverviewDashboardPanel() {
        setLayout(new BorderLayout(0, 28));
        setBackground(new Color(240, 244, 248));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Top Row Card Deck
        JPanel cardGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardGrid.setBackground(getBackground());
        cardGrid.add(createModernTravelCard("RESERVATION VOLUME", "148 Active", "↗ +12% Compounded", new Color(9, 132, 227)));
        cardGrid.add(createModernTravelCard("FOREIGN TRANSACTION YIELD", "$ 24,850 USD", "🟢 System Target Met", new Color(0, 184, 148)));
        cardGrid.add(createModernTravelCard("DOMESTIC LKR INVOICES", "742,000 LKR", "↗ SSCL/VAT Monitored", new Color(108, 92, 231)));
        cardGrid.add(createModernTravelCard("INFRASTRUCTURE CAPACITY", "89.4% Full", "⚠️ 3 Dwellings Offline", new Color(225, 112, 85)));
        add(cardGrid, BorderLayout.NORTH);

        // 2. Center Data Grid Container Card
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(WHITE_PANEL);
        gridWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SOFT_BORDER, 1, true),
                new EmptyBorder(22, 22, 22, 22)
        ));

        JLabel lblSecTitle = new JLabel("⚡ Live System Operational Infrastructure Event Logs");
        lblSecTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSecTitle.setForeground(TEXT_DARK);
        lblSecTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        gridWrapper.add(lblSecTitle, BorderLayout.NORTH);

        // Configure JTable Data Structure
        String[] headers = {"Timestamp Log", "Event Context", "Authorized Operator Role", "Execution Status"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        model.addRow(new Object[]{"23:42:15", "Foreign Guest Registered (Passport Checked)", "Front Desk Operator", "SUCCESS"});
        model.addRow(new Object[]{"22:15:00", "SQL Database Performance Optimization Completed", "System Admin Component", "COMPLETED"});
        model.addRow(new Object[]{"19:30:45", "Jasper Multi-Table Revenue Summary Compiled", "Management Admin", "VERIFIED"});

        JTable table = new JTable(model);
        table.setRowHeight(40); // Generous padding for rows
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(240, 247, 255));

        // Format Header Component
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(TEXT_DARK);
        th.setPreferredSize(new Dimension(0, 42));
        ((DefaultTableCellRenderer)th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Custom Highlight Cell Status Badge Renderer logic
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setForeground(new Color(0, 184, 148)); // Clean Modern Green
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        gridWrapper.add(scroll, BorderLayout.CENTER);

        add(gridWrapper, BorderLayout.CENTER);
    }

    private JPanel createModernTravelCard(String header, String value, String sub, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(WHITE_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SOFT_BORDER, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JPanel strip = new JPanel();
        strip.setBackground(accent);
        strip.setPreferredSize(new Dimension(0, 4));
        card.add(strip, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(3, 1, 2, 2));
        main.setBackground(WHITE_PANEL);

        JLabel h = new JLabel(header); h.setFont(new Font("Segoe UI", Font.BOLD, 11)); h.setForeground(new Color(148, 163, 184));
        JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI", Font.BOLD, 26)); v.setForeground(TEXT_DARK);
        JLabel s = new JLabel(sub); s.setFont(new Font("Segoe UI", Font.BOLD, 12)); s.setForeground(accent);

        main.add(h); main.add(v); main.add(s);
        card.add(main, BorderLayout.CENTER);
        return card;
    }
}