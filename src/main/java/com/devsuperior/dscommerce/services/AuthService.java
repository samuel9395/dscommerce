package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForBiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserService userService;

    public void validateUserAdmin(long userId) {
        User user = userService.authenticated();

        if (!user.hasRole("ROLE_ADMIN") && !user.getId().equals(userId)) {
            throw new ForBiddenException("Access denied");
        }
    }
}
