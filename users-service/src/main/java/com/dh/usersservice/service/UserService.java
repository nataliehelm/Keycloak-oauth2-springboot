package com.dh.usersservice.service;

import com.dh.usersservice.model.BillsDTO;
import com.dh.usersservice.model.User;
import com.dh.usersservice.model.UserBillsDTO;
import com.dh.usersservice.repository.FeignSubscriptionRepository;
import com.dh.usersservice.repository.UserBillsRepository;
import com.dh.usersservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository keycloakRepository;
    private final UserBillsRepository userBillsRepository;

    public UserService(UserRepository keycloakRepository, UserBillsRepository userBillsRepository) {
        this.keycloakRepository = keycloakRepository;
        this.userBillsRepository = userBillsRepository;
    }


    public List<User> findByUsername(String username){
        return keycloakRepository.findByUsername(username);
    }

    public User findById (String id){
        return keycloakRepository.findById(id);
    }

    public UserBillsDTO findByUserId(String id) {
        User user = keycloakRepository.findById(id);
        List<BillsDTO> bills = userBillsRepository.findUserById(id);
        return new UserBillsDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getFirstName(), bills);
    }
}
