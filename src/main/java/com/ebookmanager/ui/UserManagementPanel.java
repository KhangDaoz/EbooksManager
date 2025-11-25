package com.ebookmanager.ui;
import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.model.User;
import javax.swing.*;
import java.awt.*;

public class UserManagementPanel extends JPanel {
    public UserManagementPanel() {
        setLayout(new BorderLayout()); setOpaque(false);
        JPanel list = new JPanel(); list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        add(new JScrollPane(list), BorderLayout.CENTER);
        
        UserDAO dao = new UserDAO();
        for(User u : dao.findAllUsers()) {
            JPanel p = new JPanel(new BorderLayout()); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            p.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.LIGHT_GRAY));
            p.add(new JLabel("  " + u.getUserName() + " (" + u.getRole() + ")"), BorderLayout.CENTER);
            JButton del = new JButton("Delete");
            del.addActionListener(e -> { dao.deleteUser(u.getUserId()); p.setVisible(false); });
            p.add(del, BorderLayout.EAST);
            list.add(p);
        }
    }
}