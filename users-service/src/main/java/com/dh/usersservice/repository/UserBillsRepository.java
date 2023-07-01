package com.dh.usersservice.repository;

import com.dh.usersservice.model.BillsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBillsRepository {

    private FeignSubscriptionRepository feignSubscriptionRepository;

    public UserBillsRepository(FeignSubscriptionRepository feignSubscriptionRepository) {
        this.feignSubscriptionRepository = feignSubscriptionRepository;
    }


    public List<BillsDTO> findUserById (String userId) {
        ResponseEntity<List<BillsDTO>> bills = feignSubscriptionRepository.findByUserId(userId);
        return bills.getBody();
    }

}
