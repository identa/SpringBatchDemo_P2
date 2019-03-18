package com.batch_p2.utils;

import com.batch_p2.model.Campaign;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CampaignRowMapper implements RowMapper<Campaign> {
    @Override
    public Campaign mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Campaign(resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("statusID"),
                resultSet.getDate("start_date"),
                resultSet.getDate("end_date"),
                resultSet.getDouble("budget"),
                resultSet.getDouble("bid"));
    }
}
