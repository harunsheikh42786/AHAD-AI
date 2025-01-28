package com.ahad.services;

import java.security.Principal;
import java.util.List;

import com.ahad.entities.Chat;

public interface ChatService {

    List<Chat> getChatsForNoteId(String noteId, Principal principal);

    Chat saveChat(String noteId, String query, String response, Principal principal);

    void deleteChat(String chatId, String noteId, Principal principal);

    void deleteChatsByNoteId(String noteId);
}
