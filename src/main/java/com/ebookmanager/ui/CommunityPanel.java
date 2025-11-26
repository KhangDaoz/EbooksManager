package com.ebookmanager.ui;

import com.ebookmanager.model.Book;
import com.ebookmanager.service.BookProgressService;
import com.ebookmanager.service.BookService;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CommunityPanel extends JPanel {
    private JPanel listPanel;
    private BookService bookService;
    private BookProgressService progressService; 
    private MainView mainView;
    private JTextField txtSearch;

    public CommunityPanel(MainView mainView) {
        this.mainView = mainView;
        this.bookService = new BookService(); 
        this.progressService = new BookProgressService(); 
        
        initComponents();
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadBooks("");
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
        txtSearch.setPreferredSize(new Dimension(350, 45)); 
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBorder(UIUtils.createRoundedBorder());
        
        JButton btnSearch = UIUtils.createPrimaryButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 45));
        btnSearch.addActionListener(e -> loadBooks(txtSearch.getText()));
        
        searchBox.add(txtSearch);
        searchBox.add(btnSearch);
        
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBox.setOpaque(false);
        
        JButton btnUpload = UIUtils.createFlatButton("Upload");
        btnUpload.setPreferredSize(new Dimension(120, 45)); 
        
        JPopupMenu uploadMenu = new JPopupMenu();
        JMenuItem itemNew = new JMenuItem("Upload New Book");
        itemNew.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemNew.setPreferredSize(new Dimension(180, 35));
        
        JMenuItem itemManage = new JMenuItem("View Uploaded Books");
        itemManage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemManage.setPreferredSize(new Dimension(180, 35));
        
        itemNew.addActionListener(e -> showUploadDialog());
        itemManage.addActionListener(e -> showManageUploadsDialog());
        
        uploadMenu.add(itemNew);
        uploadMenu.add(itemManage);
        
        btnUpload.addActionListener(e -> uploadMenu.show(btnUpload, 0, btnUpload.getHeight()));
        rightBox.add(btnUpload);

        topPanel.add(searchBox, BorderLayout.WEST);
        topPanel.add(rightBox, BorderLayout.EAST);
        
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

        loadBooks("");
    }

    private void loadBooks(String keyword) {
        listPanel.removeAll();
        ArrayList<Book> books;

        if (keyword == null || keyword.trim().isEmpty()) {
            books = bookService.findAllBooks();
        } else {
            books = bookService.searchBooks(keyword.trim());
        }

        if (books == null || books.isEmpty()) {
            JLabel lblEmpty = new JLabel("No books found.");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblEmpty.setBorder(new EmptyBorder(20,0,0,0));
            listPanel.add(lblEmpty);
        } else {
            listPanel.add(Box.createVerticalStrut(10)); 
            for (Book book : books) {
                JPanel bookRow = new JPanel(new BorderLayout());
                bookRow.setBackground(new Color(220, 220, 220)); 
                bookRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); 
                bookRow.setPreferredSize(new Dimension(0, 60));
                bookRow.setBorder(new EmptyBorder(0, 20, 0, 20));
                
                bookRow.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { bookRow.setBackground(new Color(200, 200, 200)); }
                    @Override
                    public void mouseExited(MouseEvent e) { bookRow.setBackground(new Color(220, 220, 220)); }
                    @Override
                    public void mouseClicked(MouseEvent e) { showBookDetailDialog(book); }
                });

                String displayTitle = book.getBookTitle();
                if (book.getAuthorName() != null && !book.getAuthorName().isEmpty()) {
                    displayTitle += "  -  " + book.getAuthorName();
                }
                
                JLabel lblTitle = new JLabel(displayTitle);
                lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
                bookRow.add(lblTitle, BorderLayout.CENTER);

                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setBackground(Color.WHITE);
                wrapper.add(bookRow, BorderLayout.CENTER);
                wrapper.setBorder(new EmptyBorder(0, 10, 10, 10));
                
                listPanel.add(wrapper);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
    private void showBookDetailDialog(Book book) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Detail", true);
        dialog.setSize(500, 400); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(245, 245, 245));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        
        content.add(new JLabel("Title:"), gbc); gbc.gridx = 1;
        JTextField txtTitle = new JTextField(book.getBookTitle()); txtTitle.setEditable(false); txtTitle.setPreferredSize(new Dimension(250, 30)); content.add(txtTitle, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; content.add(new JLabel("Author:"), gbc); gbc.gridx = 1;
        JTextField txtAuthor = new JTextField(book.getAuthorName()); txtAuthor.setEditable(false); txtAuthor.setPreferredSize(new Dimension(250, 30)); content.add(txtAuthor, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; content.add(new JLabel("Publisher:"), gbc); gbc.gridx = 1;
        JTextField txtPub = new JTextField(book.getPublisher()); txtPub.setEditable(false); txtPub.setPreferredSize(new Dimension(250, 30)); content.add(txtPub, gbc);

        gbc.gridx = 0; gbc.gridy = 3; content.add(new JLabel("Genre:"), gbc); gbc.gridx = 1;
        JTextField txtGenre = new JTextField(book.getGenre()); txtGenre.setEditable(false); txtGenre.setPreferredSize(new Dimension(250, 30)); content.add(txtGenre, gbc);
        
        dialog.add(content, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        footer.setBackground(new Color(245, 245, 245));

        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        boolean isOwned = progressService.isBookInLibrary(userId, book.getBookId());

        JButton btnReadOrPreview; 
        JButton btnAddLib = new JButton("Add to Library");
        JButton btnClose = new JButton("Close");

        if (isOwned) {

            btnReadOrPreview = new JButton("Read");
            btnAddLib.setEnabled(false);
            btnAddLib.setText("In Library");
            
            btnReadOrPreview.addActionListener(e -> {
                mainView.openReadingView(book, false); 
                dialog.dispose();
            });
        } else {

            btnReadOrPreview = new JButton("Preview");
            btnReadOrPreview.setBackground(new Color(255, 250, 205)); 
            
            btnReadOrPreview.addActionListener(e -> {
                mainView.openReadingView(book, true); 
                dialog.dispose();
            });
        }

        styleDialogButton(btnReadOrPreview); 
        styleDialogButton(btnAddLib); 
        styleDialogButton(btnClose);
        if (!isOwned) {
            btnAddLib.addActionListener(e -> {
                try {
                    progressService.addBookToLibrary(userId, book.getBookId());
                    JOptionPane.showMessageDialog(dialog, "Book added to your library!");
                    dialog.dispose();
                } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage()); }
            });
        }
        
        btnClose.addActionListener(e -> dialog.dispose());
        
        footer.add(btnReadOrPreview); footer.add(btnAddLib); footer.add(btnClose);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUploadDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Upload New Book", true);
        dialog.setSize(450, 400); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(240, 240, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtTitle = new JTextField(20);
        JTextField txtAuthor = new JTextField(20);
        JTextField txtPublisher = new JTextField(20); 
        JTextField txtGenre = new JTextField(20);
        JLabel lblFile = new JLabel("No file selected");
        JButton btnBrowse = new JButton("Choose PDF...");
        final File[] selectedFile = {null};
        
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fc.getSelectedFile();
                lblFile.setText(selectedFile[0].getName());
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(btnBrowse, gbc); gbc.gridx = 1; dialog.add(lblFile, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Title:"), gbc); gbc.gridx = 1; dialog.add(txtTitle, gbc);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Author:"), gbc); gbc.gridx = 1; dialog.add(txtAuthor, gbc);
        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Publisher:"), gbc); gbc.gridx = 1; dialog.add(txtPublisher, gbc);
        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Genre:"), gbc); gbc.gridx = 1; dialog.add(txtGenre, gbc);
        
        JButton btnSave = new JButton("Upload Book");
        btnSave.setBackground(UIUtils.COLOR_ACCENT); btnSave.setForeground(Color.WHITE);
        
        btnSave.addActionListener(e -> {
            if (selectedFile[0] != null) {
                try {
                    Book book = new Book(txtTitle.getText(), txtAuthor.getText(), selectedFile[0].getAbsolutePath(), txtPublisher.getText(), txtGenre.getText());
                    bookService.uploadBook(selectedFile[0], book);
                    JOptionPane.showMessageDialog(dialog, "Upload Successful!");
                    loadBooks(""); dialog.dispose();
                } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage()); }
            } else { JOptionPane.showMessageDialog(dialog, "Please select a PDF file."); }
        });
        
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        dialog.add(btnSave, gbc);
        dialog.setVisible(true);
    }

    private void showManageUploadsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage My Uploads", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(240, 240, 240));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        ArrayList<Book> myBooks = bookService.getUploadedBooks(userId); 

        if (myBooks.isEmpty()) content.add(new JLabel("No uploaded books found."));

        for (Book book : myBooks) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            row.setBorder(new EmptyBorder(5, 15, 5, 5));
            
            JLabel lblTitle = new JLabel(book.getBookTitle());
            lblTitle.setFont(UIUtils.FONT_BOLD);
            
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            actions.setOpaque(false);
            
            JButton btnEdit = new JButton("Edit");
            styleDialogButton(btnEdit); btnEdit.setPreferredSize(new Dimension(70, 30));
            JButton btnDel = new JButton("Delete");
            styleDialogButton(btnDel); btnDel.setPreferredSize(new Dimension(70, 30)); btnDel.setForeground(Color.RED);
            
            btnEdit.addActionListener(e -> showEditBookDialog(dialog, book));
            btnDel.addActionListener(e -> {
                if(JOptionPane.showConfirmDialog(dialog, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == 0) {
                    try {
                        bookService.deleteBook(book.getBookId(), SessionManager.getInstance().getCurrentUser());
                        dialog.dispose(); showManageUploadsDialog(); loadBooks(""); 
                    } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, ex.getMessage()); }
                }
            });
            actions.add(btnEdit); actions.add(btnDel);
            row.add(lblTitle, BorderLayout.CENTER); row.add(actions, BorderLayout.EAST);
            content.add(row); content.add(Box.createVerticalStrut(10));
        }
        dialog.add(new JScrollPane(content), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showEditBookDialog(JDialog parent, Book book) {
        JDialog dialog = new JDialog(parent, "Edit Book", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(240, 240, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtTitle = new JTextField(book.getBookTitle(), 20);
        JTextField txtAuthor = new JTextField(book.getAuthorName(), 20);
        JTextField txtPub = new JTextField(book.getPublisher(), 20); 
        JTextField txtGenre = new JTextField(book.getGenre(), 20);
        
        gbc.gridx=0; gbc.gridy=0; dialog.add(new JLabel("Title:"), gbc); gbc.gridx=1; dialog.add(txtTitle, gbc);
        gbc.gridx=0; gbc.gridy=1; dialog.add(new JLabel("Author:"), gbc); gbc.gridx=1; dialog.add(txtAuthor, gbc);
        gbc.gridx=0; gbc.gridy=2; dialog.add(new JLabel("Publisher:"), gbc); gbc.gridx=1; dialog.add(txtPub, gbc);
        gbc.gridx=0; gbc.gridy=3; dialog.add(new JLabel("Genre:"), gbc); gbc.gridx=1; dialog.add(txtGenre, gbc);
        
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(UIUtils.COLOR_ACCENT); btnUpdate.setForeground(Color.WHITE);
        
        btnUpdate.addActionListener(e -> {
            book.setBookTitle(txtTitle.getText());
            book.setAuthorName(txtAuthor.getText());
            book.setPublisher(txtPub.getText());
            book.setGenre(txtGenre.getText());
            
            if (bookService.updateBook(book)) {
                JOptionPane.showMessageDialog(dialog, "Updated!");
                dialog.dispose(); parent.dispose(); showManageUploadsDialog(); loadBooks("");
            } else { JOptionPane.showMessageDialog(dialog, "Failed."); }
        });
        
        gbc.gridx=1; gbc.gridy=4; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        dialog.add(btnUpdate, gbc);
        dialog.setVisible(true);
    }
    
    private void styleDialogButton(JButton btn) {
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(225, 225, 225));
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }
}