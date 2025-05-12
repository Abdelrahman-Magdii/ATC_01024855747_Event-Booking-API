package com.spring.eventbooking.service;

import com.spring.eventbooking.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrgRepo organizationRepository;
    private final JwtUtilsUser jwtUtilsUser;



}
