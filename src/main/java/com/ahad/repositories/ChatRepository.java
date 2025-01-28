package com.ahad.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ahad.entities.Chat;
import com.ahad.entities.Note;

import jakarta.transaction.Transactional;

public interface ChatRepository extends JpaRepository<Chat, String> {

    List<Chat> findByNoteAndDate(Note note, LocalDateTime date);

    @Modifying
    @Transactional
    @Query("DELETE FROM Chat c WHERE c.note.id = :noteId")
    void deleteAllByNoteId(@Param("noteId") String noteId);

}
