package com.ahad.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ahad.entities.Chat;
import com.ahad.entities.Note;
import com.ahad.entities.User;
import com.ahad.repositories.ChatRepository;
import com.ahad.repositories.NoteRepository;
import com.ahad.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findUserByEmail(principal.getName());
    }

    @Override
    public List<Chat> getChatsForNoteId(String noteId, Principal principal) {
        Note note = noteRepository.findById(noteId).orElse(null);
        if (note != null && note.getUser().equals(getUser(principal))) {
            return chatRepository.findByNoteAndDate(note, LocalDateTime.now());
        }
        return null;
    }

    @Override
    public Chat saveChat(String noteId, String query, String response, Principal principal) {
        Note note = noteRepository.findById(noteId).orElse(null);
        if (note != null && note.getUser().equals(getUser(principal))) {
            Chat chat = new Chat();
            chat.setId(UUID.randomUUID().toString());
            chat.setDate(LocalDateTime.now());
            chat.setQuery(query);
            chat.setResponse(response);
            chat.setNote(note);
            return chatRepository.save(chat);
        }
        return null;
    }

    @Override
    public void deleteChat(String chatId, String noteId, Principal principal) {
        Note note = noteRepository.findById(noteId).orElse(null);
        if (note != null && note.getUser().equals(getUser(principal))) {
            chatRepository.deleteById(chatId);
        }
    }

    @Override
    public void deleteChatsByNoteId(String noteId) {
        chatRepository.deleteAllByNoteId(noteId);
    }
}
