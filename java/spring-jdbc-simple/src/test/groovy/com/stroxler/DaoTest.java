package com.stroxler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.stroxler.exception.NotFoundSqlException;
import org.junit.Rule;
import org.junit.Test;

import com.stroxler.entity.Customer;
import org.junit.Before;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class} )
public class DaoTest {

    @Autowired
    private Dao sut;

    @Before
    public void setUpSqlTestCase() throws Exception {
        sut.resetDb();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Customer steven = new Customer(123, "Steven", "Troxler");
    private final Customer john = new Customer(124, "John", "Doe");

    @Test
    public void testAddAndFindCustomer() throws Exception {
        sut.addCustomer(steven);
        Customer stevenOut = sut.findCustomer(steven.getId());
        assertEquals(steven, stevenOut,
                "Customer from Dao should equal original");
    }

    @Test
    public void testFindAllCustomers() throws Exception {
        List<Customer> customers = new ArrayList<>();
        customers.add(steven);
        customers.add(john);
        customers.stream()
                .forEach(sut::addCustomer);
        List<Customer> customersOut = sut.findAllCustomers();
        assertEquals(customers, customersOut,
                "Customers coming out should equal originals");
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        sut.addCustomer(steven);
        Customer stevenWithSales = new Customer(steven).setNumSales(1);
        sut.updateCustomer(stevenWithSales);
        Customer stevenOut = sut.findCustomer(steven.getId());
        assertEquals(stevenOut, stevenWithSales,
                "After update, customer coming out should match update");
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        sut.addCustomer(steven);
        assertEquals(sut.findCustomer(steven.getId()), steven);
        sut.deleteCustomer(steven);
        exception.expect(NotFoundSqlException.class);
        sut.findCustomer(steven.getId());
    }

}