package com.crime.dao;

import com.crime.db.DBConnection;
import com.crime.model.Evidence;
import java.sql.*;
import java.util.*;

public class EvidenceDAO {

    public List<Evidence> getAllEvidence() {
        List<Evidence> list = new ArrayList<>();
        String sql = "SELECT e.*, c.case_number FROM evidence e LEFT JOIN cases c ON e.case_id = c.case_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Evidence ev = mapRow(rs);
                ev.setCaseNumber(rs.getString("case_number"));
                list.add(ev);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addEvidence(Evidence ev) {
        String sql = "INSERT INTO evidence (title,type,description,found_location,found_date,case_id) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ev.getTitle()); ps.setString(2, ev.getType());
            ps.setString(3, ev.getDescription()); ps.setString(4, ev.getFoundLocation());
            ps.setDate(5, ev.getFoundDate()); ps.setInt(6, ev.getCaseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteEvidence(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM evidence WHERE evidence_id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getTotalCount() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM evidence")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Evidence mapRow(ResultSet rs) throws SQLException {
        Evidence ev = new Evidence();
        ev.setEvidenceId(rs.getInt("evidence_id")); ev.setTitle(rs.getString("title"));
        ev.setType(rs.getString("type")); ev.setDescription(rs.getString("description"));
        ev.setFoundLocation(rs.getString("found_location"));
        ev.setFoundDate(rs.getDate("found_date")); ev.setCaseId(rs.getInt("case_id"));
        return ev;
    }
}