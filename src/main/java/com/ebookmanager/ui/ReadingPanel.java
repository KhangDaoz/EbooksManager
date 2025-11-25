package com.ebookmanager.ui;

import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import com.ebookmanager.model.Bookmark;
import com.ebookmanager.service.BookProgressService;
import com.ebookmanager.service.BookService;
import com.ebookmanager.service.BookmarkService;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ReadingPanel extends JPanel {
    private Book book;
    private MainView mainView;
    private BookProgressService progressService;
    private BookmarkService bookmarkService;
    
    private int currentPage = 0;
    private int totalPages = 0;
    private PDDocument document;
    private PDFRenderer pdfRenderer;
    
    private JLabel lblPageImage;
    private JLabel lblPageInfo; 
    private JPanel bookmarkListPanel;

    public ReadingPanel(Book book, MainView mainView) {
        this.book = book;
        this.mainView = mainView;
        this.progressService = new BookProgressService();
        this.bookmarkService = new BookmarkService();
        
        initComponents();
        loadBookData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 0)); 
        setBackground(UIUtils.COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ========================================================
        // 1. KHỐI BÊN TRÁI (READER AREA)
        // ========================================================
        JPanel readerPanel = new JPanel(new BorderLayout(0, 10));
        readerPanel.setOpaque(false);

        // --- TOP: SỐ TRANG ---
        JPanel topInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topInfoPanel.setOpaque(false);
        
        lblPageInfo = new JLabel("0/0");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPageInfo.setOpaque(true);
        lblPageInfo.setBackground(Color.WHITE);
        lblPageInfo.setBorder(new EmptyBorder(5, 20, 5, 20)); 
        
        topInfoPanel.add(lblPageInfo);
        readerPanel.add(topInfoPanel, BorderLayout.NORTH);

        // --- CENTER: PDF CONTENT ---
        lblPageImage = new JLabel("Loading...", SwingConstants.CENTER);
        lblPageImage.setVerticalAlignment(SwingConstants.TOP);
        lblPageImage.setOpaque(true);
        lblPageImage.setBackground(Color.GRAY);
        
        JScrollPane scrollPane = new JScrollPane(lblPageImage);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.COLOR_ACCENT, 2));
        scrollPane.getViewport().setBackground(Color.GRAY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        readerPanel.add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM: NAVIGATION ---
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JButton btnPrev = UIUtils.createFlatButton("<");
        btnPrev.setPreferredSize(new Dimension(50, 35));
        
        JButton btnNext = UIUtils.createFlatButton(">");
        btnNext.setPreferredSize(new Dimension(50, 35));
        
        JPanel centerBtnContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerBtnContainer.setOpaque(false);
        JButton btnClose = new JButton("Close");
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setBackground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setFont(UIUtils.FONT_BOLD);
        centerBtnContainer.add(btnClose);

        btnPrev.addActionListener(e -> changePage(-1));
        btnNext.addActionListener(e -> changePage(1));
        btnClose.addActionListener(e -> closeBook());

        navPanel.add(btnPrev, BorderLayout.WEST);
        navPanel.add(centerBtnContainer, BorderLayout.CENTER);
        navPanel.add(btnNext, BorderLayout.EAST);

        readerPanel.add(navPanel, BorderLayout.SOUTH);

        // ========================================================
        // 2. KHỐI BÊN PHẢI (SIDEBAR: BOOKMARKS)
        // ========================================================
        JPanel sidebarPanel = new JPanel(new BorderLayout(0, 10));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setOpaque(false);

        // --- TOP: NÚT BOOKMARKS ---
        JPanel sidebarTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sidebarTop.setOpaque(false);
        
        JButton btnBookmarks = new JButton("Bookmarks");
        btnBookmarks.setPreferredSize(new Dimension(120, 35));
        btnBookmarks.setBackground(Color.WHITE);
        btnBookmarks.setFocusPainted(false);
        btnBookmarks.addActionListener(e -> showBookmarkMenu(btnBookmarks));
        
        sidebarTop.add(btnBookmarks);
        sidebarPanel.add(sidebarTop, BorderLayout.NORTH);

        // --- CENTER: DANH SÁCH BOOKMARK ---
        bookmarkListPanel = new JPanel();
        bookmarkListPanel.setLayout(new BoxLayout(bookmarkListPanel, BoxLayout.Y_AXIS));
        bookmarkListPanel.setBackground(Color.WHITE);
        
        JScrollPane bmScroll = new JScrollPane(bookmarkListPanel);
        bmScroll.setBorder(null);
        
        JPanel whiteBox = new JPanel(new BorderLayout());
        whiteBox.setBackground(Color.WHITE);
        whiteBox.add(bmScroll, BorderLayout.CENTER);
        
        sidebarPanel.add(whiteBox, BorderLayout.CENTER);

        add(readerPanel, BorderLayout.CENTER);
        add(sidebarPanel, BorderLayout.EAST);
    }

    // --- [SỬA LẠI MENU ĐÚNG TÊN] ---
    private void showBookmarkMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        
        // Item 1: View all (Tải lại danh sách vào ô trắng)
        JMenuItem itemView = new JMenuItem("View all");
        itemView.addActionListener(e -> loadBookmarksList());
        
        // Item 2: New (Tạo mới)
        JMenuItem itemNew = new JMenuItem("New");
        itemNew.addActionListener(e -> saveBookmark());
        
        menu.add(itemView);
        menu.add(itemNew);
        
        menu.show(invoker, 0, invoker.getHeight());
    }

    private void loadBookmarksList() {
        bookmarkListPanel.removeAll();
        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        ArrayList<Bookmark> bookmarks = bookmarkService.getBookmarksForBook(userId, book.getBookId());

        if (bookmarks.isEmpty()) {
            JLabel empty = new JLabel("No bookmarks");
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(10,0,0,0));
            bookmarkListPanel.add(empty);
        } else {
            for (Bookmark bm : bookmarks) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(new Color(240, 240, 240));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                row.setBorder(new EmptyBorder(5, 10, 5, 5));
                
                JLabel lblName = new JLabel("<html><b>" + bm.getName() + "</b> <font color='gray'>(P." + (Integer.parseInt(bm.getLocationData()) + 1) + ")</font></html>");
                lblName.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lblName.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        try {
                            currentPage = Integer.parseInt(bm.getLocationData());
                            renderCurrentPage();
                        } catch (NumberFormatException ex) {}
                    }
                });

                JButton btnDel = new JButton("x");
                btnDel.setForeground(Color.RED);
                btnDel.setBorderPainted(false);
                btnDel.setContentAreaFilled(false);
                btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnDel.addActionListener(e -> {
                    bookmarkService.deleteBookmark(bm.getBookmarkId());
                    loadBookmarksList();
                });

                row.add(lblName, BorderLayout.CENTER);
                row.add(btnDel, BorderLayout.EAST);
                
                bookmarkListPanel.add(row);
                bookmarkListPanel.add(Box.createVerticalStrut(5));
            }
        }
        bookmarkListPanel.revalidate();
        bookmarkListPanel.repaint();
    }

    private void saveBookmark() {
        String name = JOptionPane.showInputDialog(this, "Bookmark Name:", "Mark Page " + (currentPage + 1));
        if (name != null && !name.trim().isEmpty()) {
            int userId = SessionManager.getInstance().getCurrentUser().getUserId();
            bookmarkService.createBookmark(userId, book.getBookId(), String.valueOf(currentPage));
            JOptionPane.showMessageDialog(this, "Bookmark Saved!");
            loadBookmarksList();
        }
    }

    private void loadBookData() {
        new Thread(() -> {
            try {
                BookService bs = new BookService();
                InputStream is = bs.readBook(book); 
                document = PDDocument.load(is);
                pdfRenderer = new PDFRenderer(document);
                totalPages = document.getNumberOfPages();
                
                int userId = SessionManager.getInstance().getCurrentUser().getUserId();
                BookProgress bp = progressService.getBookProgress(userId, book.getBookId());
                if (bp != null && bp.getCurrentPage() != null) {
                    currentPage = Math.min(bp.getCurrentPage(), totalPages - 1);
                }

                SwingUtilities.invokeLater(() -> {
                    renderCurrentPage();
                    // Tự động hiện danh sách bookmark khi mở sách (nếu muốn)
                    // loadBookmarksList(); 
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error loading PDF: " + e.getMessage()));
            }
        }).start();
    }

    private void renderCurrentPage() {
        if (document == null) return;
        try {
            BufferedImage image = pdfRenderer.renderImage(currentPage, 1.3f); 
            lblPageImage.setIcon(new ImageIcon(image));
            lblPageInfo.setText((currentPage + 1) + "/" + totalPages);
            ((JScrollPane)lblPageImage.getParent().getParent()).getVerticalScrollBar().setValue(0);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void changePage(int delta) {
        int newPage = currentPage + delta;
        if (newPage >= 0 && newPage < totalPages) {
            currentPage = newPage;
            renderCurrentPage();
        }
    }

    private void closeBook() {
        try {
            if (document != null) document.close();
            int userId = SessionManager.getInstance().getCurrentUser().getUserId();
            int bookId = book.getBookId();
            
            if (!progressService.isBookInLibrary(userId, bookId)) {
                progressService.addBookToLibrary(userId, bookId);
            }
            progressService.updateBookProgress(userId, bookId, currentPage, 0);
        } catch (Exception e) { e.printStackTrace(); }
        mainView.goBackToLibrary();
    }
}