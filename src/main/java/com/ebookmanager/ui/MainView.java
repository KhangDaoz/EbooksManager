package com.ebookmanager.ui;
import com.ebookmanager.model.Book;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainView extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    // Danh sách nút để quản lý việc đổi màu
    private List<JButton> sidebarButtons = new ArrayList<>();

    public MainView() {
        setTitle("E-Book Manager"); setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null);
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(UIUtils.COLOR_BACKGROUND);
        setContentPane(main);
        add(createSidebar(), BorderLayout.WEST);
        add(createRightPanel(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout()); sidebar.setBackground(UIUtils.COLOR_DARK_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 15, 5, 15); gbc.gridx = 0; int row = 0;

        // Logo
        JLabel logo = new JLabel("E-BOOK MANAGER", SwingConstants.CENTER);
        logo.setForeground(Color.WHITE); logo.setFont(UIUtils.FONT_BOLD);
        gbc.gridy = row++; sidebar.add(logo, gbc);

        // Menus
        JButton btnCom = createSidebarButton("Community", "COMMUNITY");
        gbc.gridy = row++; sidebar.add(btnCom, gbc); setActiveButton(btnCom); // Mặc định sáng nút Community
        
        JButton btnLib = createSidebarButton("Library", "LIBRARY");
        gbc.gridy = row++; sidebar.add(btnLib, gbc);

        // ADMIN CHECK
        if (SessionManager.getInstance().isAdmin()) {
            gbc.gridy = row++; sidebar.add(new JSeparator(), gbc);
            gbc.gridy = row++; sidebar.add(createSidebarButton("Manage Users", "USER_MGMT"), gbc);
            gbc.gridy = row++; sidebar.add(createSidebarButton("Manage Books", "BOOK_MGMT"), gbc);
        }

        // Bottom
        gbc.gridy = row++; gbc.weighty = 1.0; sidebar.add(Box.createGlue(), gbc);
        gbc.weighty = 0;
        gbc.gridy = row++; sidebar.add(createSidebarButton("Settings", "SETTINGS"), gbc);
        
        JButton btnLogout = createSidebarButton("Logout", "LOGOUT");
        btnLogout.setForeground(UIUtils.COLOR_DANGER);
        btnLogout.addActionListener(e -> { SessionManager.getInstance().logout(); new LoginView().setVisible(true); dispose(); });
        gbc.gridy = row++; sidebar.add(btnLogout, gbc);

        return sidebar;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20)); panel.setBackground(UIUtils.COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT)); header.setOpaque(false);
        JLabel lbl = new JLabel("Welcome " + SessionManager.getInstance().getCurrentUser().getUserName());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24)); header.add(lbl);
        panel.add(header, BorderLayout.NORTH);

        // Content
        JPanel contentBox = new JPanel(new BorderLayout()); contentBox.setBackground(Color.WHITE);
        contentBox.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardLayout = new CardLayout(); cardPanel = new JPanel(cardLayout); cardPanel.setOpaque(false);
        
        cardPanel.add(new CommunityPanel(this), "COMMUNITY");
        cardPanel.add(new LibraryPanel(this), "LIBRARY");
        cardPanel.add(new SettingsPanel(), "SETTINGS");

        if (SessionManager.getInstance().isAdmin()) {
            cardPanel.add(new UserManagementPanel(), "USER_MGMT");
            cardPanel.add(new AdminBookManagementPanel(), "BOOK_MGMT");
        }
        
        contentBox.add(cardPanel, BorderLayout.CENTER);
        panel.add(contentBox, BorderLayout.CENTER);
        return panel;
    }

    private JButton createSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setBackground(UIUtils.COLOR_DARK_BG); btn.setForeground(UIUtils.COLOR_TEXT_SIDEBAR);
        btn.setFont(UIUtils.FONT_GENERAL); btn.setFocusPainted(false); btn.setBorderPainted(false);
        if (!cardName.equals("LOGOUT")) {
            sidebarButtons.add(btn);
            btn.addActionListener(e -> { cardLayout.show(cardPanel, cardName); setActiveButton(btn); });
        }
        return btn;
    }

    // Hàm đổi màu nút active
    private void setActiveButton(JButton active) {
        for(JButton b : sidebarButtons) {
            b.setBackground(b == active ? UIUtils.COLOR_ACCENT : UIUtils.COLOR_DARK_BG);
            b.setForeground(b == active ? Color.WHITE : UIUtils.COLOR_TEXT_SIDEBAR);
        }
    }
    
    // --- CÁC HÀM ĐIỀU HƯỚNG ---

    public void openReadingView(Book book, boolean isPreview) {
        cardPanel.add(new ReadingPanel(book, this, isPreview), "READ_" + book.getBookId());
        cardLayout.show(cardPanel, "READ_" + book.getBookId());
    }

    public void openReadingView(Book book) {
        openReadingView(book, false); 
    }

    // 1. Quay về Library (và cập nhật màu nút Sidebar)
    public void goBackToLibrary() { 
        cardLayout.show(cardPanel, "LIBRARY"); 
        updateSidebarVisuals("Library");
    }

    // 2. Quay về Community (MỚI)
    public void goBackToCommunity() {
        cardLayout.show(cardPanel, "COMMUNITY");
        updateSidebarVisuals("Community");
    }

    // Helper: Tìm nút theo tên text để set active
    private void updateSidebarVisuals(String btnText) {
        for (JButton btn : sidebarButtons) {
            if (btn.getText().equals(btnText)) {
                setActiveButton(btn);
                break;
            }
        }
    }
}