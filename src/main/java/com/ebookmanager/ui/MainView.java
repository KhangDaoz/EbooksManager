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
    private JPanel mainContainer; 
    private List<JButton> sidebarButtons = new ArrayList<>();

    public MainView() {
        setTitle("E-Book Manager"); 
        setSize(1200, 750); // Tăng kích thước mặc định chút cho đẹp
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        setLocationRelativeTo(null);
        mainContainer = new JPanel(new BorderLayout()); 
        mainContainer.setBackground(UIUtils.COLOR_BACKGROUND);
        
        mainContainer.add(createSidebar(), BorderLayout.WEST);
        mainContainer.add(createRightPanel(), BorderLayout.CENTER);
        setContentPane(mainContainer);
    }
    public void openReadingView(Book book, boolean isPreview) {
        ReadingPanel readingPanel = new ReadingPanel(book, this, isPreview);
        setContentPane(readingPanel);
        revalidate();
        repaint();
    }
    public void openReadingView(Book book) {
        openReadingView(book, false); 
    }
    private void restoreMainInterface(String cardName) {
        setContentPane(mainContainer);
        cardLayout.show(cardPanel, cardName);
        if (cardName.equals("LIBRARY")) updateSidebarVisuals("Library");
        else if (cardName.equals("COMMUNITY")) updateSidebarVisuals("Community");
        
        revalidate();
        repaint();
    }

    public void goBackToLibrary() { 
        restoreMainInterface("LIBRARY");
    }

    public void goBackToCommunity() {
        restoreMainInterface("COMMUNITY");
    }
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout()); sidebar.setBackground(UIUtils.COLOR_DARK_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 15, 5, 15); gbc.gridx = 0; int row = 0;

        JLabel logo = new JLabel("E-BOOK MANAGER", SwingConstants.CENTER);
        logo.setForeground(Color.WHITE); logo.setFont(UIUtils.FONT_BOLD);
        gbc.gridy = row++; sidebar.add(logo, gbc);

        JButton btnCom = createSidebarButton("Community", "COMMUNITY");
        gbc.gridy = row++; sidebar.add(btnCom, gbc); setActiveButton(btnCom); 
        
        JButton btnLib = createSidebarButton("Library", "LIBRARY");
        gbc.gridy = row++; sidebar.add(btnLib, gbc);

        if (SessionManager.getInstance().isAdmin()) {
            gbc.gridy = row++; sidebar.add(new JSeparator(), gbc);
            gbc.gridy = row++; sidebar.add(createSidebarButton("Manage Users", "USER_MGMT"), gbc);
            gbc.gridy = row++; sidebar.add(createSidebarButton("Manage Books", "BOOK_MGMT"), gbc);
        }

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
        
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT)); header.setOpaque(false);
        JLabel lbl = new JLabel("Welcome " + SessionManager.getInstance().getCurrentUser().getUserName());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24)); header.add(lbl);
        panel.add(header, BorderLayout.NORTH);

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

    private void setActiveButton(JButton active) {
        for(JButton b : sidebarButtons) {
            b.setBackground(b == active ? UIUtils.COLOR_ACCENT : UIUtils.COLOR_DARK_BG);
            b.setForeground(b == active ? Color.WHITE : UIUtils.COLOR_TEXT_SIDEBAR);
        }
    }

    private void updateSidebarVisuals(String btnText) {
        for (JButton btn : sidebarButtons) {
            if (btn.getText().equals(btnText)) {
                setActiveButton(btn);
                break;
            }
        }
    }
}