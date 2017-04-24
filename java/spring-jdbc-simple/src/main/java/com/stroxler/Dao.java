package com.stroxler;

import com.stroxler.entity.Customer;
import com.stroxler.exception.NotFoundSqlException;
import com.stroxler.logging.LogCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Dao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void resetDb() {
        SqlScriptRunner.runSqlScript("sql/clear_db.sql", jdbcTemplate);
        SqlScriptRunner.runSqlScript("sql/schema.sql", jdbcTemplate);
    }

    private static final String ADD_CUSTOMER =
            "INSERT INTO " +
                    "customers " +
                    "(id, first_name, last_name, n_sales) " +
                    "VALUES" +
                    "(?, ?, ?, ?)";

    @LogCall
    public void addCustomer(Customer customer) {
        addCustomer(
                customer.getId(), customer.getFirstName(), customer.getLastName(),
                customer.getNumSales());
    }

    private void addCustomer(long id, String firstName, String lastName, long nSales) {
        jdbcTemplate.update(ADD_CUSTOMER, id, firstName, lastName, nSales);
    }

    public List<Customer> findAllCustomers() {
        return jdbcTemplate.query(
                "SELECT " +
                        "id, first_name, last_name, n_sales " +
                        "FROM customers " +
                        "ORDER BY id ASC",
                (rs, rowNum) -> {
                    return new Customer(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getLong("n_sales"));
                } );
    }

    @LogCall
    public Customer findCustomer(long id) {
        List<Customer> customers = jdbcTemplate.query(
                "SELECT " +
                        "id, first_name, last_name, n_sales " +
                        "FROM customers " +
                        "WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> {
                    return new Customer(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getLong("n_sales"));
                });
        if (customers.size() != 1) {
            throw new NotFoundSqlException();
        }
        return customers.get(0);
    }

    @LogCall
    public void updateCustomer(Customer customer) {
        jdbcTemplate.update(
                "UPDATE customers " +
                        "SET " +
                        "first_name = ?, " +
                        "last_name = ?, " +
                        "n_sales = ? " +
                        "WHERE " +
                        "id = ?",
                customer.getFirstName(), customer.getLastName(),
                customer.getNumSales(), customer.getId()
        );
    }

    @LogCall
    public void deleteCustomer(Customer customer) {
        deleteCustomer(customer.getId());
    }

    private void deleteCustomer(long id) {
        jdbcTemplate.update(
                "DELETE FROM customers " +
                        "WHERE " +
                        "id = ?",
                id
        );
    }

}

