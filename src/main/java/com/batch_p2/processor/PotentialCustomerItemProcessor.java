package com.batch_p2.processor;

import com.batch_p2.model.Customer;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class PotentialCustomerItemProcessor implements ItemProcessor<Customer, Customer> {

    private int ageCalculation(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }

    private LocalDate convert(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    @Override
    public Customer process(Customer customer) throws Exception {
        int age = ageCalculation(convert(customer.getBirthdate()), LocalDate.now());
        if (age > 20 && age <50)
        return customer;
        else return null;
    }
}
