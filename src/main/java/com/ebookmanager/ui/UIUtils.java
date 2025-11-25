package com.ebookmanager.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class UIUtils {
    // --- MÀU SẮC (THEME NAVY BLUE) ---
    public static final Color COLOR_DARK_BG = new Color(44, 62, 80); 
    public static final Color COLOR_BACKGROUND = new Color(236, 240, 241);
    public static final Color COLOR_WHITE = Color.WHITE;
    public static final Color COLOR_ACCENT = new Color(52, 152, 219); // Màu xanh dương chính
    public static final Color COLOR_TEXT_PRIMARY = new Color(44, 62, 80);
    public static final Color COLOR_TEXT_SIDEBAR = new Color(189, 195, 199);
    public static final Color COLOR_DANGER = new Color(231, 76, 60);

    // --- FONT ---
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_GENERAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // --- BORDER ---
    public static Border createRoundedBorder() {
        return new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true), 
            new EmptyBorder(5, 10, 5, 10)
        );
    }

    // =========================================================================
    // KHU VỰC QUAN TRỌNG: CÁC HÀM TẠO BUTTON ĐỂ SỬA LỖI GẠCH ĐỎ
    // =========================================================================
    
    /**
     * Tạo nút bấm chính (Màu xanh dương, chữ trắng)
     * Hàm này được gọi trong LibraryPanel
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(COLOR_ACCENT); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        // Padding: Trên, Trái, Dưới, Phải
        btn.setBorder(new EmptyBorder(10, 20, 10, 20)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Tạo nút bấm phẳng (Màu xám nhạt)
     * Dùng cho các nút phụ
     */
    public static JButton createFlatButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_GENERAL);
        btn.setBackground(new Color(230, 230, 230)); 
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}