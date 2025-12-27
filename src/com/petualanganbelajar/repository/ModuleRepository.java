/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.repository;
import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.ModuleModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author DD
 */
public class ModuleRepository {
    public List<ModuleModel> getAllModules() {
        List<ModuleModel> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                modules.add(new ModuleModel(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modules;
    }
}
