package com.ahad.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ahad.entities.Note;
import com.ahad.services.ChatService;
import com.ahad.services.NoteService;

@Controller
@RequestMapping("/ai")
public class AiController {

    private OllamaChatModel chatModel;

    AiController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Autowired
    private ChatService chatService;

    @Autowired
    private NoteService noteService;

    Logger logger = LoggerFactory.getLogger(AiController.class);

    @GetMapping("/new-note")
    public String newNote(Principal principal) {
        noteService.createNewNote(principal);
        return "redirect:/ai/ai-page";
    }

    @GetMapping("/ai-page")
    public String getAiPage(Principal principal, Model model) {
        List<Note> notes = noteService.getNotesForUser(principal);
        if (notes.isEmpty()) {
            return "redirect:/ai/new-note";
        } else {
            notes = notes.stream()
                    .sorted((n1, n2) -> n2.getDate().compareTo(n1.getDate()))
                    .collect(Collectors.toList());
            model.addAttribute("notes", notes);
            model.addAttribute("chats", notes.get(0).getChats().stream()
                    .sorted((c1, c2) -> c1.getDate().compareTo(c2.getDate()))
                    .collect(Collectors.toList()));
            model.addAttribute("noteId", notes.get(0).getId());
        }
        return "user/ai-page";
    }

    @GetMapping("/search-note/{id}")
    public String aiSearchHandler(@PathVariable("id") String noteId, Model model, Principal principal) {
        List<Note> notes = noteService.getNotesForUser(principal);
        model.addAttribute("notes", notes);
        Note find_note = noteService.findNoteById(noteId, principal);
        model.addAttribute("chats", find_note.getChats().stream()
                .sorted((c1, c2) -> c1.getDate().compareTo(c2.getDate()))
                .collect(Collectors.toList()));
        model.addAttribute("noteId", noteId);
        return "user/ai-page";
    }

    @PostMapping("/search-query")
    public String searchQuery(@RequestParam String query, @RequestParam String noteId, Principal principal) {
        String response = chatModel.call(query);
        Note foundNote = noteService.findNoteById(noteId, principal);
        if (foundNote.getName().equals("New Note")) {
            foundNote.setName(response.substring(0, 30));
            noteService.updateNoteName(noteId, foundNote.getName(), principal);
        }
        chatService.saveChat(noteId, query, response, principal);
        return "redirect:/ai/search-note/" + noteId;
    }

    @GetMapping("/remove-note/{id}")
    public String removeNote(@PathVariable("id") String id, Principal principal) {
        noteService.deleteNoteById(id, principal);
        List<Note> notes = noteService.getNotesForUser(principal);
        if (notes.isEmpty()) {
            return "redirect:/ai/new-note";
        }
        return "redirect:/ai/ai-page";
    }

    @PostMapping("/update-note-name")
    public ResponseEntity<String> updateNoteName(@RequestParam String id, @RequestParam String name,
            Principal principal) {
        Note updatedNote = noteService.updateNoteName(id, name, principal);
        if (updatedNote != null) {
            return ResponseEntity.ok("Note updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
    }

    @PostMapping("/delete-chat")
    public ResponseEntity<String> deleteChat(@RequestParam String chatId, @RequestParam String noteId,
            Principal principal) {
        try {
            chatService.deleteChat(chatId, noteId, principal);
            return ResponseEntity.ok("Chat deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the chat");
        }
    }
}
