package com.x.flow.xflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.x.flow.xflow.entity.Customer;
import com.x.flow.xflow.mapper.CustomerMapper;

@Service
public class CustomerService {

	@Autowired
	CustomerMapper customerMapper;
	
	public Customer login() {
		Customer customer = new Customer();
		customer.setId(1L);
		return customer;
	}
	
}
