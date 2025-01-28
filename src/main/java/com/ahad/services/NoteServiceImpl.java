package com.ahad.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ahad.entities.Note;
import com.ahad.entities.User;
import com.ahad.repositories.NoteRepository;
import com.ahad.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findUserByEmail(principal.getName());
    }

    @Override
    public Note createNewNote(Principal principal) {
        User user = getUser(principal);
        Note note = new Note();
        note.setId(UUID.randomUUID().toString());
        note.setName("New Note");
        note.setDate(LocalDateTime.now());
        note.setUser(user);
        return noteRepository.save(note);
    }

    @Override
    public List<Note> getNotesForUser(Principal principal) {
        User user = getUser(principal);
        return noteRepository.findByUser(user);
    }

    @Override
    public Note findNoteById(String noteId, Principal principal) {
        User user = getUser(principal);
        return user.getNotes().stream().filter(note -> note.getId().equals(noteId)).findFirst().orElse(null);
    }

    @Override
    public Note updateNoteName(String id, String name, Principal principal) {
        Note noteToUpdate = findNoteById(id, principal);
        if (noteToUpdate != null) {
            noteToUpdate.setName(name);
            return noteRepository.save(noteToUpdate);
        }
        return null;
    }

    @Override
    public void deleteNoteById(String id, Principal principal) {
        noteRepository.deleteNoteById(id);
    }
}
