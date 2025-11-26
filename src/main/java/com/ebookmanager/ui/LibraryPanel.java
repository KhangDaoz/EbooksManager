package com.ebookmanager.ui;

import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import com.ebookmanager.model.Collection;
import com.ebookmanager.service.BookProgressService;
import com.ebookmanager.service.BookService;
import com.ebookmanager.service.CollectionService;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LibraryPanel extends JPanel {
    private JPanel listPanel;
    private JTextField txtSearch;
    private BookProgressService progressService;
    private CollectionService collectionService;
    private BookService bookService;
    private MainView mainView;
    
    private List<Book> currentLibraryBooks = new ArrayList<>();
    private JPanel selectedPanel = null;
    private Object selectedObject = null;

    public LibraryPanel(MainView mainView) {
        this.mainView = mainView;
        this.progressService = new BookProgressService();
        this.collectionService = new CollectionService();
        this.bookService = new BookService();
        
        initComponents();
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadLibrary("");
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);
        
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBox.setOpaque(false);
        
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.setBorder(UIUtils.createRoundedBorder());
        
        JButton btnSearch = UIUtils.createPrimaryButton("Search");
        btnSearch.addActionListener(e -> loadLibrary(txtSearch.getText()));
        
        searchBox.add(txtSearch);
        searchBox.add(btnSearch);
        
        JButton btnCollections = UIUtils.createFlatButton("Collection");
        btnCollections.addActionListener(e -> showCollectionMenuDialog());
        
        topPanel.add(searchBox, BorderLayout.WEST);
        topPanel.add(btnCollections, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);


        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);


        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setBackground(Color.WHITE);
        listWrapper.add(listPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(listWrapper);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);

        loadLibrary("");
    }


    private void loadLibrary(String keyword) {
        listPanel.removeAll();
        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        
        ArrayList<BookProgress> progresses = progressService.getBookProgresses(userId);
        currentLibraryBooks.clear();
        
        if (progresses == null || progresses.isEmpty()) {
            JLabel lbl = new JLabel("Your library is empty.", SwingConstants.CENTER);
            lbl.setFont(UIUtils.FONT_GENERAL);
            lbl.setBorder(new EmptyBorder(20, 0, 0, 0));
            listPanel.add(lbl);
        } else {
            listPanel.add(Box.createVerticalStrut(10)); 
            for (BookProgress bp : progresses) {
                Book book = bp.getBookReading();
                currentLibraryBooks.add(book);

                if (!keyword.isEmpty() && !book.getBookTitle().toLowerCase().contains(keyword.toLowerCase())) {
                    continue;
                }
                JPanel bookCard = new JPanel(new BorderLayout(10, 0));
                bookCard.setBackground(new Color(220, 220, 220));
                bookCard.setBorder(new EmptyBorder(10, 15, 10, 15));
                bookCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                bookCard.setPreferredSize(new Dimension(0, 70));
                

                JPanel infoP = new JPanel(new GridLayout(2, 1));
                infoP.setOpaque(false);
                
                JLabel lblTitle = new JLabel(book.getBookTitle());
                lblTitle.setFont(UIUtils.FONT_BOLD);
                lblTitle.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lblTitle.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        mainView.openReadingView(book);
                    }
                });
                
                String ratingStr = (bp.getPersonalRating() > 0) ? bp.getPersonalRating() + "/5" : "Unrated";
                JLabel lblSub = new JLabel("Page: " + bp.getCurrentPage() + "  |  Rating: " + ratingStr);
                lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblSub.setForeground(Color.GRAY);
                
                infoP.add(lblTitle);
                infoP.add(lblSub);

                JPanel actionP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                actionP.setOpaque(false);
                
                JButton btnRate = new JButton("Rate");
                styleSmallButton(btnRate);
                btnRate.setBackground(new Color(255, 255, 200));
                
                JButton btnRemove = new JButton("Remove");
                styleSmallButton(btnRemove);
                
                btnRate.addActionListener(e -> {
                    String input = JOptionPane.showInputDialog(this, "Rate (1-5):", bp.getPersonalRating());
                    if (input != null) {
                        try {
                            int r = Integer.parseInt(input);
                            if (r >= 1 && r <= 5) {
                                progressService.rateBook(userId, book.getBookId(), r);
                                loadLibrary(txtSearch.getText());
                            }
                        } catch (Exception ex) {}
                    }
                });

                btnRemove.addActionListener(e -> {
                    int cf = JOptionPane.showConfirmDialog(this, "Remove '" + book.getBookTitle() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (cf == JOptionPane.YES_OPTION) {
                        progressService.removeBookFromLibrary(userId, book.getBookId());
                        loadLibrary(txtSearch.getText());
                    }
                });

                actionP.add(btnRate);
                actionP.add(btnRemove);

                bookCard.add(infoP, BorderLayout.CENTER);
                bookCard.add(actionP, BorderLayout.EAST);
                
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setBackground(Color.WHITE);
                wrapper.add(bookCard, BorderLayout.CENTER);
                wrapper.setBorder(new EmptyBorder(0, 10, 10, 10));
                
                listPanel.add(wrapper);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
    private void showCollectionMenuDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Collections", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(180, 180, 180));
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton btnView = new JButton("View");
        styleFlowButton(btnView);
        btnView.addActionListener(e -> {
            dialog.dispose();
            showViewCollectionsDialog();
        });
        
        JButton btnNew = new JButton("New");
        styleFlowButton(btnNew);
        btnNew.addActionListener(e -> {
            dialog.dispose();
            showCreateCollectionDialog();
        });

        dialog.add(btnView, gbc);
        gbc.gridy = 1;
        dialog.add(btnNew, gbc);
        
        dialog.setVisible(true);
    }
    private void showCreateCollectionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Collection", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(180, 180, 180));
        dialog.setLayout(new GridBagLayout());

        JTextField txtName = new JTextField(20);
        txtName.setBorder(BorderFactory.createTitledBorder("Name"));
        txtName.setPreferredSize(new Dimension(250, 50));
        
        JButton btnCreate = new JButton("Create");
        styleFlowButton(btnCreate);
        btnCreate.setPreferredSize(new Dimension(100, 40));
        
        btnCreate.addActionListener(e -> {
            if (!txtName.getText().trim().isEmpty()) {
                collectionService.createCollection(SessionManager.getInstance().getCurrentUser(), txtName.getText());
                dialog.dispose();
                showCollectionMenuDialog();
            }
        });

        dialog.add(txtName);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        dialog.add(btnCreate, gbc);
        
        dialog.setVisible(true);
    }
    private void showViewCollectionsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Your Collections", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(180, 180, 180));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        selectedPanel = null;
        selectedObject = null;

        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        ArrayList<Collection> collections = collectionService.getCollectionsForUser(userId);

        if (collections.isEmpty()) {
            content.add(new JLabel("No collections found."));
        }

        for (Collection col : collections) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(new Color(220, 220, 220));
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            p.setBorder(new EmptyBorder(10, 15, 10, 15));
            
            JLabel lbl = new JLabel(col.getCollectionName());
            lbl.setFont(UIUtils.FONT_BOLD);
            p.add(lbl, BorderLayout.CENTER);
            
            p.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedPanel != null) selectedPanel.setBackground(new Color(220, 220, 220));
                    if (e.getClickCount() == 2) {
                        dialog.dispose();
                        showCollectionDetailDialog(col);
                        return;
                    }
                    selectedPanel = p;
                    selectedObject = col;
                    p.setBackground(UIUtils.COLOR_ACCENT);
                }
            });
            
            content.add(p);
            content.add(Box.createVerticalStrut(10));
        }
        
        dialog.add(new JScrollPane(content), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(180, 180, 180));
        footer.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnRen = new JButton("Rename");
        styleFlowButton(btnRen); 
        btnRen.setPreferredSize(new Dimension(100, 40));
        
        JButton btnDel = new JButton("Delete");
        styleFlowButton(btnDel); 
        btnDel.setPreferredSize(new Dimension(100, 40));

        btnRen.addActionListener(e -> {
            if (selectedObject instanceof Collection) {
                Collection col = (Collection) selectedObject;
                String newN = JOptionPane.showInputDialog("Rename to:", col.getCollectionName());
                if (newN != null) {
                    collectionService.renameCollection(col.getCollectionId(), newN, SessionManager.getInstance().getCurrentUser());
                    dialog.dispose();
                    showViewCollectionsDialog();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a collection.");
            }
        });

        btnDel.addActionListener(e -> {
            if (selectedObject instanceof Collection) {
                Collection col = (Collection) selectedObject;
                if (JOptionPane.showConfirmDialog(dialog, "Delete collection?") == JOptionPane.YES_OPTION) {
                    collectionService.deleteCollection(col.getCollectionId(), SessionManager.getInstance().getCurrentUser());
                    dialog.dispose();
                    showViewCollectionsDialog();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a collection.");
            }
        });

        footer.add(btnRen, BorderLayout.WEST);
        footer.add(btnDel, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    private void showCollectionDetailDialog(Collection col) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), col.getCollectionName(), true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(180, 180, 180));
        JLabel title = new JLabel(col.getCollectionName());
        title.setFont(UIUtils.FONT_HEADER);
        header.add(title);
        dialog.add(header, BorderLayout.NORTH);

        JPanel listP = new JPanel();
        listP.setLayout(new BoxLayout(listP, BoxLayout.Y_AXIS));
        listP.setBackground(new Color(180, 180, 180));
        listP.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        dialog.add(new JScrollPane(listP), BorderLayout.CENTER);

        selectedPanel = null;
        selectedObject = null;

        Runnable reload = () -> {
            listP.removeAll();
            ArrayList<Book> books = collectionService.getBooksInCollection(col.getCollectionId(), SessionManager.getInstance().getCurrentUser());
            
            if (books.isEmpty()) {
                JLabel empty = new JLabel("No books in this collection.");
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                listP.add(empty);
            } else {
                for (Book b : books) {
                    JPanel p = new JPanel(new BorderLayout());
                    p.setBackground(new Color(220, 220, 220));
                    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                    p.setBorder(new EmptyBorder(5, 10, 5, 10));
                    
                    p.add(new JLabel(b.getBookTitle()), BorderLayout.CENTER);
                    
                    p.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (selectedPanel != null) selectedPanel.setBackground(new Color(220, 220, 220));
                            selectedPanel = p;
                            selectedObject = b;
                            p.setBackground(UIUtils.COLOR_ACCENT);
                        }
                    });
                    
                    listP.add(p);
                    listP.add(Box.createVerticalStrut(10));
                }
            }
            listP.revalidate();
            listP.repaint();
        };
        reload.run();

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(180, 180, 180));
        footer.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnAdd = new JButton("Addbook");
        styleFlowButton(btnAdd);
        btnAdd.setPreferredSize(new Dimension(100, 40));
        
        JButton btnRem = new JButton("Remove Book");
        styleFlowButton(btnRem);
        btnRem.setPreferredSize(new Dimension(120, 40));

        btnAdd.addActionListener(e -> showAddBookDialog(dialog, col, reload));
        
        btnRem.addActionListener(e -> {
            if (selectedObject instanceof Book) {
                Book b = (Book) selectedObject;
                if (JOptionPane.showConfirmDialog(dialog, "Remove book?") == JOptionPane.YES_OPTION) {
                    collectionService.removeBookFromCollection(b.getBookId(), col.getCollectionId(), SessionManager.getInstance().getCurrentUser());
                    reload.run();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Select a book to remove.");
            }
        });

        footer.add(btnAdd, BorderLayout.WEST);
        footer.add(btnRem, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    private void showAddBookDialog(JDialog parent, Collection col, Runnable onDone) {
        JDialog dialog = new JDialog(parent, "Add Book", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(180, 180, 180));
        JPanel searchP = new JPanel(new FlowLayout());
        searchP.setOpaque(false);
        
        JTextField txtS = new JTextField(20);
        JButton btnS = new JButton("Search");
        
        searchP.add(txtS);
        searchP.add(btnS);
        dialog.add(searchP, BorderLayout.NORTH);
        JPanel listP = new JPanel();
        listP.setLayout(new BoxLayout(listP, BoxLayout.Y_AXIS));
        listP.setBackground(new Color(180, 180, 180));
        dialog.add(new JScrollPane(listP), BorderLayout.CENTER);

        final Book[] selBook = {null};
        final JPanel[] selP = {null};

        Runnable load = () -> {
            listP.removeAll();
            String k = txtS.getText().toLowerCase();
            ArrayList<Book> filteredBooks = new ArrayList<>();
            for (Book b : currentLibraryBooks) {
                if (b.getBookTitle().toLowerCase().contains(k) || b.getAuthorName().toLowerCase().contains(k)) {
                    filteredBooks.add(b);
                }
            }
            
            if (filteredBooks.isEmpty()) {
                listP.add(new JLabel("No matching books in your Library."));
            }

            for (Book b : filteredBooks) {
                JPanel p = new JPanel(new BorderLayout());
                p.setBackground(new Color(220, 220, 220));
                p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                p.setBorder(new EmptyBorder(5, 10, 5, 10));
                p.add(new JLabel(b.getBookTitle()), BorderLayout.CENTER);
                
                p.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (selP[0] != null) selP[0].setBackground(new Color(220, 220, 220));
                        selP[0] = p;
                        selBook[0] = b;
                        p.setBackground(UIUtils.COLOR_ACCENT);
                    }
                });
                
                listP.add(p);
                listP.add(Box.createVerticalStrut(5));
            }
            listP.revalidate();
            listP.repaint();
        };
        
        btnS.addActionListener(e -> load.run());
        load.run();
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(180, 180, 180));
        
        JButton btnAdd = new JButton("Add Selected");
        styleFlowButton(btnAdd);
        
        btnAdd.addActionListener(e -> {
            if (selBook[0] != null) {
                try {
                    collectionService.addBookToCollection(selBook[0].getBookId(), col.getCollectionId(), SessionManager.getInstance().getCurrentUser());
                    JOptionPane.showMessageDialog(dialog, "Added!");
                    onDone.run(); 
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Select a book first.");
            }
        });
        
        footer.add(btnAdd);
        dialog.add(footer, BorderLayout.SOUTH);
        
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                onDone.run();
            }
        });
        
        dialog.setVisible(true);
    }

    private void styleFlowButton(JButton btn) {
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setBackground(new Color(220, 220, 220));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
    }
    
    private void styleSmallButton(JButton btn) {
        btn.setBackground(new Color(200, 200, 200));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}