package com.dh.usersservice.repository;


import com.dh.usersservice.configuration.feign.FeignInterceptor;
import com.dh.usersservice.model.BillsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name= "ms-bill",url = "http://localhost:8081", configuration = FeignInterceptor.class)
public interface FeignSubscriptionRepository {

    @RequestMapping(method = RequestMethod.GET,value = "/api/v1/bills/find/{userId}")
    ResponseEntity <List<BillsDTO>> findByUserId(@RequestParam String userId);
}
