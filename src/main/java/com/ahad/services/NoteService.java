package com.ahad.services;

import java.security.Principal;
import java.util.List;

import com.ahad.entities.Note;

public interface NoteService {

    Note createNewNote(Principal principal);

    List<Note> getNotesForUser(Principal principal);

    Note findNoteById(String noteId, Principal principal);

    Note updateNoteName(String id, String name, Principal principal);

    void deleteNoteById(String id, Principal principal);
}
