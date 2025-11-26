package com.ebookmanager.ui;

import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import com.ebookmanager.model.Bookmark;
import com.ebookmanager.service.*;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ReadingPanel extends JPanel {
    private Book book;
    private MainView mainView;
    private BookProgressService progressService;
    private BookmarkService bookmarkService;
    
    private boolean isPreview; 
    
    private int currentPage = 0;
    private int totalPages = 0;
    private int currentRating = 0;
    
    private PDDocument document;
    private PDFRenderer pdfRenderer;
    
    private JLabel lblPageImage, lblPageInfo;

    public ReadingPanel(Book book, MainView mainView, boolean isPreview) {
        this.book = book; 
        this.mainView = mainView;
        this.isPreview = isPreview;
        
        this.progressService = new BookProgressService(); 
        this.bookmarkService = new BookmarkService();
        
        initComponents(); 
        loadBookData();
    }
    
    public ReadingPanel(Book book, MainView mainView) {
        this(book, mainView, false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0)); 
        setBackground(new Color(50, 50, 50)); 
        setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton btnBookmarks = new JButton("Bookmarks");
        styleButton(btnBookmarks);
        btnBookmarks.setPreferredSize(new Dimension(110, 35));
        btnBookmarks.addActionListener(e -> showBookmarkOptionMenu(btnBookmarks));

        JPanel centerInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerInfoPanel.setOpaque(false);
        lblPageInfo = new JLabel("Loading...");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPageInfo.setOpaque(true);
        lblPageInfo.setBackground(new Color(50, 50, 50));
        lblPageInfo.setForeground(Color.WHITE);
        lblPageInfo.setBorder(new EmptyBorder(5, 20, 5, 20));
        centerInfoPanel.add(lblPageInfo);

        JPanel dummyLeft = new JPanel();
        dummyLeft.setOpaque(false);
        dummyLeft.setPreferredSize(btnBookmarks.getPreferredSize());

        topPanel.add(dummyLeft, BorderLayout.WEST);
        topPanel.add(centerInfoPanel, BorderLayout.CENTER);
        topPanel.add(btnBookmarks, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        lblPageImage = new JLabel("", SwingConstants.CENTER);
        lblPageImage.setVerticalAlignment(SwingConstants.TOP);
        lblPageImage.setOpaque(true);
        lblPageImage.setBackground(new Color(80, 80, 80)); 
        
        JScrollPane scrollPane = new JScrollPane(lblPageImage);
        scrollPane.setBorder(null); 
        scrollPane.getViewport().setBackground(new Color(80, 80, 80));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnPrev = UIUtils.createPrimaryButton("Prev Page"); // Nút to hơn
        btnPrev.setPreferredSize(new Dimension(120, 45));
        btnPrev.addActionListener(e -> changePage(-1));
        
        JButton btnNext = UIUtils.createPrimaryButton("Next Page"); // Nút to hơn
        btnNext.setPreferredSize(new Dimension(120, 45));
        btnNext.addActionListener(e -> changePage(1));
        
        JPanel centerClosePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerClosePanel.setOpaque(false);
        JButton btnClose = new JButton("Close Reader");
        styleButton(btnClose);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setPreferredSize(new Dimension(150, 45));
        btnClose.addActionListener(e -> closeBook());
        centerClosePanel.add(btnClose);

        bottomPanel.add(btnPrev, BorderLayout.WEST);
        bottomPanel.add(centerClosePanel, BorderLayout.CENTER);
        bottomPanel.add(btnNext, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }
    private void showBookmarkOptionMenu(JButton sourceBtn) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemView = new JMenuItem("View All Bookmarks");
        itemView.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemView.addActionListener(e -> showViewAllDialog());
        JMenuItem itemNew = new JMenuItem("New Bookmark...");
        itemNew.setFont(new Font("Segoe UI", Font.BOLD, 14));
        itemNew.addActionListener(e -> showCreateBookmarkDialog());
        menu.add(itemView); menu.addSeparator(); menu.add(itemNew);
        menu.show(sourceBtn, 0, sourceBtn.getHeight());
    }

    private void showCreateBookmarkDialog() {
        if (isPreview) {
            JOptionPane.showMessageDialog(this, "Please add to library to use bookmarks."); return;
        }
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Bookmark", true);
        dialog.setSize(300, 180); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5)); panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblHint = new JLabel("Bookmark Title (Page " + (currentPage + 1) + "):");
        JTextField txtTitle = new JTextField();
        JButton btnCreate = new JButton("Create");
        btnCreate.setBackground(UIUtils.COLOR_ACCENT); btnCreate.setForeground(Color.WHITE);
        btnCreate.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            if (!title.isEmpty()) {
                bookmarkService.createBookmark(SessionManager.getInstance().getCurrentUser().getUserId(), book.getBookId(), String.valueOf(currentPage), title);
                JOptionPane.showMessageDialog(dialog, "Bookmark Created!"); dialog.dispose();
            }
        });
        panel.add(lblHint); panel.add(txtTitle); panel.add(btnCreate);
        dialog.add(panel, BorderLayout.CENTER); dialog.setVisible(true);
    }

    private void showViewAllDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Your Bookmarks", true);
        dialog.setSize(350, 400); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout());
        JPanel listPanel = new JPanel(); listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); listPanel.setBackground(Color.WHITE);
        
        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        ArrayList<Bookmark> bms = bookmarkService.getBookmarksForBook(userId, book.getBookId());

        if (bms.isEmpty()) listPanel.add(new JLabel("No bookmarks found."));
        else {
            for (Bookmark bm : bms) {
                JPanel row = new JPanel(new BorderLayout()); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                row.setBackground(new Color(245, 245, 245)); row.setBorder(new EmptyBorder(5, 10, 5, 5));
                JLabel lblName = new JLabel(bm.getName()); lblName.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lblName.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        try { currentPage = Integer.parseInt(bm.getLocationData()); renderCurrentPage(); dialog.dispose(); } catch (Exception ex) {}
                    }
                });
                JButton btnDel = new JButton("x"); btnDel.setForeground(Color.RED); btnDel.setContentAreaFilled(false); btnDel.setBorderPainted(false);
                btnDel.addActionListener(e -> { bookmarkService.deleteBookmark(bm.getBookmarkId()); dialog.dispose(); showViewAllDialog(); });
                row.add(lblName, BorderLayout.CENTER); row.add(btnDel, BorderLayout.EAST);
                listPanel.add(row); listPanel.add(Box.createVerticalStrut(5));
            }
        }
        dialog.add(new JScrollPane(listPanel), BorderLayout.CENTER); dialog.setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.WHITE); btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void loadBookData() {
        new Thread(() -> {
            try {
                BookService bs = new BookService();
                InputStream is = bs.readBook(book); PDDocument doc = PDDocument.load(is);
                document = doc; pdfRenderer = new PDFRenderer(document); totalPages = document.getNumberOfPages();
                
                if (!isPreview) {
                    int userId = SessionManager.getInstance().getCurrentUser().getUserId();
                    BookProgress bp = progressService.getBookProgress(userId, book.getBookId());
                    if (bp != null && bp.getCurrentPage() != null) currentPage = Math.min(bp.getCurrentPage(), totalPages - 1);
                }
                SwingUtilities.invokeLater(this::renderCurrentPage);
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void renderCurrentPage() {
        if (document == null) return;
        try {
            float scale = 1.5f; 
            BufferedImage image = pdfRenderer.renderImage(currentPage, scale);
            lblPageImage.setIcon(new ImageIcon(image)); lblPageImage.setText("");
            lblPageInfo.setText((currentPage + 1) + "/" + totalPages);
            if (lblPageImage.getParent() != null) ((JScrollPane)lblPageImage.getParent().getParent()).getVerticalScrollBar().setValue(0);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void changePage(int d) {
        int n = currentPage + d;
        if (n >= 0 && n < totalPages) { currentPage = n; renderCurrentPage(); }
    }
    private void closeBook() {
        try {
            if (document != null) document.close();
            
            if (isPreview) {
                System.out.println("Closed Preview. Returning to Community.");
                mainView.goBackToCommunity(); 
            } else {
                int userId = SessionManager.getInstance().getCurrentUser().getUserId();
                if (!progressService.isBookInLibrary(userId, book.getBookId())) {
                    progressService.addBookToLibrary(userId, book.getBookId());
                }
                progressService.updateBookProgress(userId, book.getBookId(), currentPage, currentRating);
                
                mainView.goBackToLibrary(); 
            }
            
        } catch (Exception e) { e.printStackTrace(); }
    }
}