package com.solt.jdc.hateoas_sample.controller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class IndexController {
    @GetMapping("/")
    public RepresentationModel index() {
        RepresentationModel representationModel = new RepresentationModel();

        representationModel.add(linkTo(methodOn(IndexController.class).index()).withSelfRel());
        representationModel.add(linkTo(methodOn(CustomerController.class).listCustomer()).withRel("customers"));
        return representationModel;
    }

}
