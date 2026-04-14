package com.crime.dao;

import com.crime.db.DBConnection;
import com.crime.model.Case;
import java.sql.*;
import java.util.*;

public class CaseDAO {

    public List<Case> getAllCases() {
        List<Case> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM cases ORDER BY date_reported DESC")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addCase(Case c) {
        String sql = "INSERT INTO cases (case_number,title,description,status,location,date_reported,officer_name) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCaseNumber()); ps.setString(2, c.getTitle());
            ps.setString(3, c.getDescription()); ps.setString(4, c.getStatus());
            ps.setString(5, c.getLocation()); ps.setDate(6, c.getDateReported());
            ps.setString(7, c.getOfficerName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStatus(int caseId, String status) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE cases SET status=? WHERE case_id=?")) {
            ps.setString(1, status); ps.setInt(2, caseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteCase(int caseId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM cases WHERE case_id=?")) {
            ps.setInt(1, caseId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int[] getStatusCounts() {
        int[] counts = new int[3];
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT status, COUNT(*) as cnt FROM cases GROUP BY status")) {
            while (rs.next()) {
                switch (rs.getString("status")) {
                    case "OPEN"          -> counts[0] = rs.getInt("cnt");
                    case "INVESTIGATING" -> counts[1] = rs.getInt("cnt");
                    case "CLOSED"        -> counts[2] = rs.getInt("cnt");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return counts;
    }

    private Case mapRow(ResultSet rs) throws SQLException {
        return new Case(rs.getInt("case_id"), rs.getString("case_number"),
                rs.getString("title"), rs.getString("description"), rs.getString("status"),
                rs.getString("location"), rs.getDate("date_reported"), rs.getString("officer_name"));
    }
}