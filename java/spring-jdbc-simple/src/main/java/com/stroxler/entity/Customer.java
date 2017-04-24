package com.stroxler.entity;


public class Customer {
    private long id;
    private String firstName;
    private String lastName;
    private long numSales;

    public Customer(Customer other) {
        this.id = other.id;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.numSales = other.numSales;
    }

    public Customer(long id, String firstName, String lastName, long numSales) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.numSales = numSales;
    }

    public Customer(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.numSales = 0;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", numSales=" + numSales +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (numSales != customer.numSales) return false;
        if (!firstName.equals(customer.firstName)) return false;
        return lastName.equals(customer.lastName);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + (int) (numSales ^ (numSales >>> 32));
        return result;
    }

    public long getId() {
        return id;
    }

    public Customer setId(long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Customer setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Customer setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public long getNumSales() {
        return numSales;
    }

    public Customer setNumSales(long nSales) {
        this.numSales = nSales;
        return this;
    }
}
