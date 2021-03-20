package com.solt.jdc.hateoas_sample.dao;

import com.solt.jdc.hateoas_sample.ds.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface AddressDao extends CrudRepository<Address, Integer> {
}
