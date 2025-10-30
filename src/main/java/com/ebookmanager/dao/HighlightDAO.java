// package com.ebookmanager.dao;

// import com.ebookmanager.db.DatabaseConnector;
// import com.ebookmanager.model.Highlight;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;


// public class HighlightDAO {
//     public void createHighlight(Highlight highlight) {
//         String sql = "INSERT INTO highlight (user_id, book_id, page_number, start_pos, end_pos, background_color, note_content) VALUES (?,?,?,?,?,?,?);";
//         try (Connection conn = DatabaseConnector.getConnection();
//             PreparedStatement query = conn.prepareStatement(sql)) {
//             query.setInt(1, highlight.getUserId());
//             query.setInt(2, highlight.getBookId());
//             query.setInt(3, highlight.getPageNumber());
//             query.setInt(4, highlight.getStartPos());
//             query.setInt(5, highlight.getEndPos());
//             query.setString(7,highlight.getBackgroundColor());
//             query.setString(6, highlight.getNoteContent());
//             query.executeUpdate();
//         } catch (SQLException e) {
//             System.err.println("ERROR creating highlight: " + e.getMessage());
//         }
//     }

//     public List<Highlight> getHighlightForUserBook(int userId,int bookId) {
//         List<Highlight> highlights = new ArrayList<>();
//         String sql = "SELECT * FROM highlight WHERE user_id = ? AND book_id = ?;";
//         try(Connection conn = DatabaseConnector.getConnection();
//             PreparedStatement query = conn.prepareStatement(sql)) {
//             query.setInt(1, userId);
//             query.setInt(2, bookId);
//             ResultSet res = query.executeQuery();
//             while (res.next()) {
//                 Highlight highlight = new Highlight (
//                     res.getInt("user_id"),
//                     res.getInt("book_id"),
//                     res.getInt("page_number"),
//                     res.getInt("start_pos"),
//                     res.getInt("end_pos"),
//                     res.getString("background_color"),
//                     res.getString("note_content")
//                 );
//                 highlights.add(highlight);
//             }

//         } catch (SQLException e) {
//             System.out.println("ERROR fetching hightlights: " + e.getMessage());
//         }
//         return highlights;
//     }

//     public void updateNoteContent(int highlight_id,String newNote) {
//         String sql = "UPDATE highlight SET note_content = ? WHERE highlight_id = ?;";
//         try (Connection conn = DatabaseConnector.getConnection();
//             PreparedStatement query = conn.prepareStatement(sql)) {
//             query.setString(1, newNote);
//             query.setInt(2,highlight_id);
//             query.executeUpdate();
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//     }


//     public void deleteHighlight(int highlightId) {
//         String sql = "DELETE FROM highlight WHERE highlight_id = ?;";
//         try (Connection conn = DatabaseConnector.getConnection();
//             PreparedStatement query = conn.prepareStatement(sql)) {
//             query.setInt(1,highlightId);
//             query.executeUpdate(sql);
//         } catch (SQLException e) {
//             System.out.println("ERROR deleting highlight: " + e.getMessage());
//         }
//     }

// }
