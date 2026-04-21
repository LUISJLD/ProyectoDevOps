package com.backend.demo.security.services;

import com.backend.demo.model.entity.User;
import com.backend.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JpaUserDetailService  implements UserDetailsService{
    private final UserRepository userRepository;

    @Override
@Transactional // IMPORTANTE: Agrega esta anotación para mantener la sesión abierta
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    
    // Forzamos la carga de los roles antes de salir del método transaccional
    user.getRoles().size(); 
    
    return UserInfoDetail.build(user);
}
}