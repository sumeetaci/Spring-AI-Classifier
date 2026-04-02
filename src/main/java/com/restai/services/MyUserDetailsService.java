package com.restai.services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.restai.entity.User;
import com.restai.repositories.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{
	 @Autowired
	    private UserRepository userRepository;

	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        // Find your user in the DB
	        User user = userRepository.findByUsername(username); 
	        if (user == null) throw new UsernameNotFoundException("User not found");
	        
	        // Return Spring's User object (org.springframework.security.core.userdetails.User)
	        return new org.springframework.security.core.userdetails.User(
	            user.getUsername(), 
	            user.getPassword(), 
	            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
	        );
	    }
}
