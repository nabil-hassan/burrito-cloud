package net.nh.burrito.repository.jdbc;

import net.nh.burrito.entity.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Order.builder().id(rs.getLong("id")).orderName(rs.getString("name")).street(rs.getString("street")).town(rs.getString("town"))
                .county(rs.getString("county")).postcode(rs.getString("postcode")).creditCardNo(rs.getString("ccNo"))
                .creditCardExpiryDate(rs.getString("ccExpiryDate")).creditCardCCV("ccCCV").build();
    }
}
