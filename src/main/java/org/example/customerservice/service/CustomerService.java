package org.example.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.customerservice.models.Customer;
import org.example.customerservice.repo.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, Customer customer) {

        Customer existing = getCustomer(id);

        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());

        return customerRepository.save(existing);

    }

    public void deleteCustomer(Long id) {

        Customer existing = getCustomer(id);

        customerRepository.delete(existing);
    }
}