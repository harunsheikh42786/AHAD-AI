package com.ahad.services;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ahad.entities.Chat;
import com.ahad.entities.Note;
import com.ahad.entities.User;
import com.ahad.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUser(Principal principal) {
        return this.userRepository.findUserByEmail(principal.getName());
    }

    @Override
    public User getLoginUser(Principal principal) {
        return getUser(principal);
    }

    @Override
    public User getCustomUser(String id) {
        return this.userRepository.findUserById(id);
    }

    @Override
    public List<Note> getNotes(Principal principal) {
        return getUser(principal).getNotes();
    }

    @Override
    public List<Chat> getChats(Principal principal) {
        throw new UnsupportedOperationException("Unimplemented method 'getChats'");
    }

    @Override
    public User updateUser(User user, Principal principal, String rawPassword) {
        User existUser = getUser(principal);
        if (passwordEncoder.matches(rawPassword, existUser.getPassword())) {
            existUser.setName(user.getName());
            existUser.setPhone(user.getPhone());
            existUser.setGender(user.getGender());
            existUser.setDob(user.getDob());
            existUser.setProfession(user.getProfession());
            existUser.setCity(user.getCity());
            existUser.setCountry(user.getCountry());
            return userRepository.save(existUser);
        } else {
            return null;
        }
    }

}
