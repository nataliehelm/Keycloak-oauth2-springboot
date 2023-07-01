package com.dh.usersservice.repository;

import com.dh.usersservice.model.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    @Autowired
    private Keycloak keycloak;
    @Value("${dh.keycloak.realm}")
    private String realm;

    public List<User> findByUsername(String username){
        List<UserRepresentation> userRepresentation = keycloak
                .realm(realm)
                .users()
                .search(username);
        return userRepresentation.stream().map(user -> fromRepresentation(user)).collect(Collectors.toList());
    }

    public User findById(String id) {
        UserRepresentation userRepresentation = keycloak
                .realm(realm)
                .users()
                .get(id)
                .toRepresentation();
        return fromRepresentation(userRepresentation);

    }

    //funcion para convertir de userrepresentation que keycloak me devuelve a user de mi modelo
    private User fromRepresentation(UserRepresentation userRepresentation) {
        return new User(userRepresentation.getId(), userRepresentation.getUsername(),
                userRepresentation.getEmail(), userRepresentation.getFirstName() );
    }

    private UserRepresentation toUserRepresentation (User user){
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setRequiredActions(List.of("UPDATE_PASSWORD"));
        return userRepresentation;
    }
}
