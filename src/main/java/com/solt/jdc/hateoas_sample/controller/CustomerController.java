package com.solt.jdc.hateoas_sample.controller;

import com.solt.jdc.hateoas_sample.dao.AddressDao;
import com.solt.jdc.hateoas_sample.dao.CustomerDao;
import com.solt.jdc.hateoas_sample.ds.Address;
import com.solt.jdc.hateoas_sample.ds.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class CustomerController {
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private AddressDao addressDao;
    public static final Class<CustomerController> CUSTOMERS_CONTROLLER_CLASS = CustomerController.class;

    @GetMapping("/customers")
    public CollectionModel<EntityModel<Customer>> listCustomer() {
        List<EntityModel<Customer>> customers=
                StreamSupport.stream(customerDao.findAll().spliterator(),false)
                .map(cus -> EntityModel.of(cus, linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).getCustomer(cus.getId())).withSelfRel(),
                        linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).getCustomer(cus.getId())).withRel("customer")))
                .collect(Collectors.toList());

        Link customerLink = linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).listCustomer()).withSelfRel();
        return CollectionModel.of(customers, customerLink);
    }

    @GetMapping("/customers/{id}")
    public EntityModel<Customer> getCustomer(@PathVariable Integer id) {
      //  Class<CustomerController> customersControllerClass= CustomerController.class;
        Optional<Customer> customer = customerDao.findById(id);
        if (!customer.isPresent()) {
            throw new EntityNotFoundException("Id - "+ id);
        }

        EntityModel resource = EntityModel.of(customer.get());
        resource.add(linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).getCustomer(id)).withSelfRel());
        resource.add(linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).listCustomer()).withRel("customers"));
        resource.add(linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).listAddresses(id)).withRel("addresses"));
        return resource;
    }
    @GetMapping("/customers/{customerId}/addresses/{addressId}")
    public EntityModel<Address> getAddress(@PathVariable int customerId, @PathVariable int addressId) {
      //  Class<CustomerController> customersControllerClass= CustomerController.class;
        Customer customer = customerDao.findById(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Address customerAddress = customer.getAddresses().stream()
                .filter(address -> address.getId().equals(addressId))
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return EntityModel.of(customerAddress,
                linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).getAddress(customerId, customerAddress.getId())).withSelfRel(),
                linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).getCustomer(customerId)).withRel("customer"));
    }
    @GetMapping("/customers/{id}/addresses")
    public CollectionModel<EntityModel<Address>> listAddresses(@PathVariable int id) {
       // Class<CustomerController> customersControllerClass= CustomerController.class;
        Customer customer = customerDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Address>> addresses = customer.getAddresses().stream()
                .map(address -> EntityModel.of(address, linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).getAddress(id, address.getId())).withSelfRel(),
                        linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).getCustomer(id)).withRel("customer")))
                .collect(Collectors.toList());
        Link addressLink = linkTo(methodOn(CUSTOMERS_CONTROLLER_CLASS).listAddresses(id)).withSelfRel();
        return CollectionModel.of(addresses, addressLink);
    }
}
