package com.ahad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ahad.entities.Note;
import com.ahad.entities.User;

import jakarta.transaction.Transactional;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.id = :id")
    void deleteNoteById(@Param("id") String id);

    Note getNoteById(String id);

    List<Note> findByUser(User user);

}
