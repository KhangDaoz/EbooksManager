-- 1. RESET DATABASE (Làm sạch dữ liệu cũ)
DROP DATABASE IF EXISTS ebookmanager;
CREATE DATABASE ebookmanager;
USE ebookmanager;

-- 2. TẠO BẢNG (SCHEMA)

-- Bảng User
CREATE TABLE `user` (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL, -- Mặc định pass '123456' là '1450575459'
    role VARCHAR(50) NOT NULL
);

-- Bảng Book 
CREATE TABLE book (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    book_title VARCHAR(255) NOT NULL,
    author_name VARCHAR(255),
    file_path TEXT NOT NULL,
    publisher VARCHAR(255),
    genre VARCHAR(100),
    uploaded_by INT DEFAULT NULL,
    FOREIGN KEY (uploaded_by) REFERENCES `user`(user_id) ON DELETE SET NULL
);

-- Bảng Tiến độ đọc (Library)
CREATE TABLE book_progress (
    user_id INT,
    book_id INT,
    current_page INT DEFAULT 0,
    last_read DATETIME DEFAULT CURRENT_TIMESTAMP,
    personal_rating INT DEFAULT 0,
    PRIMARY KEY (user_id, book_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE
);

-- Bảng Bookmark
CREATE TABLE bookmark (
    bookmark_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    book_id INT,
    name VARCHAR(255),
    location_data TEXT, -- Lưu số trang
    FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE
);

-- Bảng Collection (Bộ sưu tập)
CREATE TABLE collection (
    collection_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    collection_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE
);

-- Bảng Liên kết Sách - Collection
CREATE TABLE collection_books (
    book_id INT,
    collection_id INT,
    PRIMARY KEY (book_id, collection_id),
    FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE,
    FOREIGN KEY (collection_id) REFERENCES collection(collection_id) ON DELETE CASCADE
);

-- 3. THÊM DỮ LIỆU MẪU (SEED DATA)

-- A. Users (Tất cả mật khẩu là 123456)
INSERT INTO `user` (user_name, hashed_password, role) VALUES 
('admin', '1450575459', 'Admin'),  -- ID: 1
('user1', '1450575459', 'Member'), -- ID: 2 
('user2', '1450575459', 'Member'); -- ID: 3

-- B. Books 
INSERT INTO book (book_title, author_name, file_path, publisher, genre, uploaded_by) VALUES 
('Lập trình Java Căn bản', 'Nguyễn Văn A', 'C:/fake/java_basic.pdf', 'NXB Giáo Dục', 'Technology', 1),
('Clean Code', 'Robert C. Martin', 'C:/fake/clean_code.pdf', 'Prentice Hall', 'Technology', 1),
('Harry Potter và Hòn đá Phù thủy', 'J.K. Rowling', 'C:/fake/harry_potter_1.pdf', 'NXB Trẻ', 'Fiction', 1),
('Đắc Nhân Tâm', 'Dale Carnegie', 'C:/fake/dac_nhan_tam.pdf', 'NXB Tổng Hợp', 'Self-help', 1),
('Giải tích 1', 'Bộ Giáo Dục', 'C:/fake/giai_tich.pdf', 'NXB Đại Học', 'Education', 1),
('Introduction to Algorithms', 'Thomas H. Cormen', 'C:/fake/algorithms.pdf', 'MIT Press', 'Technology', 1),
('Triết học', 'Bộ Giáo Dục', 'C:/fake/triet_hoc.pdf', 'NXB Đại Học', 'Education', 1),
('PLDC', 'Bộ Giáo Dục', 'C:/fake/PLDC.pdf', 'NXB Đại Học', 'Education', 1),
('Đại số', 'Bộ Giáo Dục', 'C:/fake/đai_so.pdf', 'NXB Đại Học', 'Education', 1);
