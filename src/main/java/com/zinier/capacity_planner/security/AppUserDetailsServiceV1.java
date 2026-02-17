package com.zinier.capacity_planner.security;

import com.zinier.capacity_planner.user.dao.AppUserDaoV1;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsServiceV1 implements UserDetailsService {

    private final AppUserDaoV1 appUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity appUser = appUserDao.findActiveByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities = buildAuthorities(appUser.getUserRole());

        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }

    private List<SimpleGrantedAuthority> buildAuthorities(UserRole role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_VIEWER"));
        if (role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (role == UserRole.SUPER_ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
        }
        return authorities;
    }
}
