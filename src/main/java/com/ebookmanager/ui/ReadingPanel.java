/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.ebookmanager.ui;

/**
 *
 * @author trank
 */
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
public class ReadingPanel extends javax.swing.JPanel {

    private Book book;
    private MainView mainView;
    private boolean isPreview;
    private BookProgressService progressService;
    private BookmarkService bookmarkService;
    
    // Biến trạng thái đọc sách
    private int currentPage = 0;
    private int totalPages = 0;
    
    // Thư viện PDFBox
    private PDDocument document;
    private PDFRenderer pdfRenderer;
    public ReadingPanel() {
        initComponents();
    }
    public ReadingPanel(Book book, MainView mainView, boolean isPreview) {
        this.book = book;
        this.mainView = mainView;
        this.isPreview = isPreview;
        
        this.progressService = new BookProgressService();
        this.bookmarkService = new BookmarkService();
        
        initComponents(); // NetBeans vẽ giao diện
        
        applyCustomStyles(); // Tô màu lại cho nút (đảm bảo phẳng đẹp)
        loadBookData();      // Mở file PDF
    }
    
    // Constructor phụ
    public ReadingPanel(Book book, MainView mainView) {
        this(book, mainView, false);
    }
    
    // --- PHẦN 1: LOGIC XỬ LÝ SÁCH & PDF ---

    private void loadBookData() {
        // Chạy luồng riêng để không đơ giao diện khi đang load file
        new Thread(() -> {
            try {
                BookService bs = new BookService();
                InputStream is = bs.readBook(book);
                
                if (is == null) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "File not found!"));
                    return;
                }

                PDDocument doc = PDDocument.load(is);
                document = doc;
                pdfRenderer = new PDFRenderer(document);
                totalPages = document.getNumberOfPages();
                
                // Nếu không phải xem thử, hãy load lại trang đang đọc dở
                if (!isPreview) {
                    int userId = SessionManager.getInstance().getCurrentUser().getUserId();
                    BookProgress bp = progressService.getBookProgress(userId, book.getBookId());
                    if (bp != null && bp.getCurrentPage() != null) {
                        currentPage = Math.min(bp.getCurrentPage(), totalPages - 1);
                    }
                }
                
                SwingUtilities.invokeLater(this::renderCurrentPage);
                
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error loading PDF: " + e.getMessage()));
            }
        }).start();
    }

    private void renderCurrentPage() {
        if (document == null) return;
        try {
            // Scale 1.5f để ảnh nét hơn
            float scale = 1.5f; 
            BufferedImage image = pdfRenderer.renderImage(currentPage, scale);
            
            // Hiển thị ảnh lên Label
            lblPageImage.setIcon(new ImageIcon(image));
            lblPageImage.setText(""); // Xóa chữ nếu có
            
            // Cập nhật số trang (VD: 5/120)
            lblPageInfo.setText((currentPage + 1) + "/" + totalPages);
            
            // Reset thanh cuộn về đầu trang
            scrollPaneBook.getVerticalScrollBar().setValue(0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            
            if (isPreview) {
                mainView.goBackToCommunity(); 
            } else {
                // Lưu tiến độ đọc
                int userId = SessionManager.getInstance().getCurrentUser().getUserId();
                if (!progressService.isBookInLibrary(userId, book.getBookId())) {
                    progressService.addBookToLibrary(userId, book.getBookId());
                }
                progressService.updateBookProgress(userId, book.getBookId(), currentPage, 0);
                
                mainView.goBackToLibrary(); 
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- PHẦN 2: LOGIC BOOKMARK ---

    private void showBookmarkOptionMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem itemView = new JMenuItem("View All Bookmarks");
        itemView.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemView.addActionListener(e -> showViewAllBookmarksDialog());
        
        JMenuItem itemNew = new JMenuItem("Bookmark This Page");
        itemNew.setFont(new Font("Segoe UI", Font.BOLD, 14));
        itemNew.addActionListener(e -> showCreateBookmarkDialog());
        
        menu.add(itemView);
        menu.addSeparator();
        menu.add(itemNew);
        
        // Hiển thị menu ngay dưới nút Bookmarks
        menu.show(btnBookmarks, 0, btnBookmarks.getHeight());
    }

    private void showCreateBookmarkDialog() {
        if (isPreview) {
            JOptionPane.showMessageDialog(this, "Please add to library to use bookmarks.");
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Bookmark Name for Page " + (currentPage + 1) + ":");
        if (name != null && !name.trim().isEmpty()) {
            bookmarkService.createBookmark(SessionManager.getInstance().getCurrentUser().getUserId(), 
                    book.getBookId(), String.valueOf(currentPage), name);
            JOptionPane.showMessageDialog(this, "Bookmark Saved!");
        }
    }
    
    private void showViewAllBookmarksDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Your Bookmarks", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        ArrayList<Bookmark> bms = bookmarkService.getBookmarksForBook(userId, book.getBookId());

        if (bms.isEmpty()) {
            listPanel.add(new JLabel("No bookmarks found."));
        } else {
            for (Bookmark bm : bms) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(new Color(245, 245, 245));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
                
                JLabel lblName = new JLabel(bm.getName() + " (Pg " + (Integer.parseInt(bm.getLocationData()) + 1) + ")");
                lblName.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lblName.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        currentPage = Integer.parseInt(bm.getLocationData());
                        renderCurrentPage();
                        dialog.dispose();
                    }
                });
                
                JButton btnDel = new JButton("x");
                btnDel.setForeground(Color.RED);
                btnDel.setBorderPainted(false);
                btnDel.setContentAreaFilled(false);
                btnDel.addActionListener(e -> {
                    bookmarkService.deleteBookmark(bm.getBookmarkId());
                    dialog.dispose();
                    showViewAllBookmarksDialog();
                });
                
                row.add(lblName, BorderLayout.CENTER);
                row.add(btnDel, BorderLayout.EAST);
                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(5));
            }
        }
        dialog.add(new JScrollPane(listPanel));
        dialog.setVisible(true);
    }

    // --- PHẦN 3: STYLE GIAO DIỆN (TÔ MÀU NÚT) ---
    private void applyCustomStyles() {
        // Màu xanh chủ đạo (Trùng màu 51, 204, 255 mà bạn chọn)
        Color blueColor = new Color(51, 204, 255); 
        
        // Style nút Prev/Next
        styleBlueButton(btnPrev, blueColor);
        styleBlueButton(btnNext, blueColor);
        
        // Style nút Close và Bookmarks (Màu trắng)
        styleWhiteButton(btnClose);
        styleWhiteButton(btnBookmarks);
        
        // Chỉnh thanh cuộn
        scrollPaneBook.getVerticalScrollBar().setUnitIncrement(20);
    }
    
    private void styleBlueButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Giữ nguyên font bạn đã set trong Designer
    }
    
    private void styleWhiteButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        btnBookmarks = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblPageInfo = new javax.swing.JLabel();
        scrollPaneBook = new javax.swing.JScrollPane();
        lblPageImage = new javax.swing.JLabel();
        pnlBottom = new javax.swing.JPanel();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();

        setBackground(new java.awt.Color(50, 50, 50));
        setPreferredSize(new java.awt.Dimension(1200, 750));
        setLayout(new java.awt.BorderLayout());

        pnlTop.setOpaque(false);
        pnlTop.setLayout(new java.awt.BorderLayout());

        btnBookmarks.setText("Bookmarks");
        btnBookmarks.setPreferredSize(new java.awt.Dimension(110, 35));
        btnBookmarks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBookmarksActionPerformed(evt);
            }
        });
        pnlTop.add(btnBookmarks, java.awt.BorderLayout.EAST);

        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(120, 50));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 75, Short.MAX_VALUE)
        );

        pnlTop.add(jPanel1, java.awt.BorderLayout.WEST);

        jPanel2.setOpaque(false);

        lblPageInfo.setBackground(new java.awt.Color(50, 50, 50));
        lblPageInfo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblPageInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblPageInfo.setText("Loading...");
        lblPageInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 5, 20, 5));
        lblPageInfo.setOpaque(true);
        jPanel2.add(lblPageInfo);

        pnlTop.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(pnlTop, java.awt.BorderLayout.PAGE_START);

        scrollPaneBook.setBorder(null);

        lblPageImage.setBackground(new java.awt.Color(80, 80, 80));
        lblPageImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPageImage.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblPageImage.setOpaque(true);
        scrollPaneBook.setViewportView(lblPageImage);

        add(scrollPaneBook, java.awt.BorderLayout.CENTER);

        pnlBottom.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 1, 1));
        pnlBottom.setOpaque(false);
        pnlBottom.setLayout(new java.awt.BorderLayout());

        btnPrev.setBackground(new java.awt.Color(51, 204, 255));
        btnPrev.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPrev.setText("Prev Page");
        btnPrev.setPreferredSize(new java.awt.Dimension(120, 45));
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });
        pnlBottom.add(btnPrev, java.awt.BorderLayout.WEST);

        btnNext.setBackground(new java.awt.Color(51, 204, 255));
        btnNext.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNext.setText("Next Page");
        btnNext.setPreferredSize(new java.awt.Dimension(120, 45));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        pnlBottom.add(btnNext, java.awt.BorderLayout.EAST);

        jPanel3.setOpaque(false);

        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnClose.setText("Close Reader");
        btnClose.setPreferredSize(new java.awt.Dimension(150, 45));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel3.add(btnClose);

        pnlBottom.add(jPanel3, java.awt.BorderLayout.CENTER);

        add(pnlBottom, java.awt.BorderLayout.PAGE_END);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {                                        
        changePage(-1);// TODO add your handling code here:
    }                                       

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {                                         
        closeBook();// TODO add your handling code here:
    }                                        

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {                                        
        changePage(1);// TODO add your handling code here:
    }                                       

    private void btnBookmarksActionPerformed(java.awt.event.ActionEvent evt) {                                             
        showBookmarkOptionMenu();// TODO add your handling code here:
    }                                            


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBookmarks;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblPageImage;
    private javax.swing.JLabel lblPageInfo;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JScrollPane scrollPaneBook;
    // End of variables declaration                   
}
