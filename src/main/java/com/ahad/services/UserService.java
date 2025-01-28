package com.ahad.services;

import java.security.Principal;
import java.util.List;

import com.ahad.entities.Chat;
import com.ahad.entities.Note;
import com.ahad.entities.User;

public interface UserService {

    User getLoginUser(Principal principal);

    User getCustomUser(String userId);

    List<Note> getNotes(Principal principal);

    List<Chat> getChats(Principal principal);

    User updateUser(User user, Principal principal, String rawPassword);

    User getUser(Principal principal);

}
