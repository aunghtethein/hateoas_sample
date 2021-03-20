package com.solt.jdc.hateoas_sample.dao;

import com.solt.jdc.hateoas_sample.ds.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface CustomerDao extends CrudRepository<Customer, Integer> {
}
