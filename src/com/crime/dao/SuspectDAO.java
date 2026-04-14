package com.crime.dao;

import com.crime.db.DBConnection;
import com.crime.model.Suspect;
import java.sql.*;
import java.util.*;

public class SuspectDAO {

    public List<Suspect> getAllSuspects() {
        List<Suspect> list = new ArrayList<>();
        String sql = "SELECT s.*, c.case_number FROM suspects s LEFT JOIN cases c ON s.case_id = c.case_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Suspect s = mapRow(rs);
                s.setCaseNumber(rs.getString("case_number"));
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSuspect(Suspect s) {
        String sql = "INSERT INTO suspects (name,age,gender,address,background,danger_level,case_id) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName()); ps.setInt(2, s.getAge());
            ps.setString(3, s.getGender()); ps.setString(4, s.getAddress());
            ps.setString(5, s.getBackground()); ps.setString(6, s.getDangerLevel());
            ps.setInt(7, s.getCaseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteSuspect(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM suspects WHERE suspect_id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getTotalCount() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM suspects")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Suspect mapRow(ResultSet rs) throws SQLException {
        return new Suspect(rs.getInt("suspect_id"), rs.getString("name"),
                rs.getInt("age"), rs.getString("gender"), rs.getString("address"),
                rs.getString("background"), rs.getString("danger_level"), rs.getInt("case_id"));
    }
}