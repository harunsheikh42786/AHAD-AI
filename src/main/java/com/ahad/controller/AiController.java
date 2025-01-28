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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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

import reactor.core.publisher.Flux;

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
    public String searchQuery(@RequestParam String request, @RequestParam String noteId, Principal principal) {
        String ollamaQuery = """
                Generate a response wrapped in a <div> tag.
                - Use inline CSS styles (e.g., color: light;).
                - Use h3, h4 tag (color : dim light, font-weight: bold).
                - Use br tag provide break and use padding and margin normally.
                - Do note provide form tag, and any outside css.
                - Use p(color white if normal text), span, i , b, table if table present.
                - <Code> if resopnse generate code, make a div with scrollable on width increase and bg-light, text-dark.
                - Ensure semantic HTML.
                - Follow professional coding standards.
                """;
        String response = chatModel.call("<query>" + request + "</query> \n" + ollamaQuery);
        Note foundNote = noteService.findNoteById(noteId, principal);
        if (foundNote.getName().equals("New Note")) {
            foundNote.setName(chatModel.call(
                    "<query>" + response
                            + "</query>, Provide a chat name according to this query in 20 letter Properly space."));
            noteService.updateNoteName(noteId, foundNote.getName(), principal);
        }
        chatService.saveChat(noteId, request, response, principal);
        return "redirect:/ai/search-note/" + noteId;
    }

    // @GetMapping(value = "/search-query2/{request}")
    // public Flux<String> searchQuery(@PathVariable("request") String request) {
    // String ollamaQuery = """
    // Generate a response wrapped in a <div> tag.
    // - Use inline CSS styles (e.g., color: light;).
    // - Use h3, h4 tag (color : dim light, font-weight: bold).
    // - Use br tag provide break and use padding and margin normally.
    // - Do not provide form tag, and any outside css.
    // - Use p(color white if normal text), span, i , b, table if table present.
    // - <Code> if response generate code, make a div with scrollable on width
    // increase and bg-light, text-dark.
    // - Ensure semantic HTML.
    // - Follow professional coding standards.
    // """;

    // // Directly return the streaming response from chatModel
    // return chatModel.stream(request
    // + " \n heading place <h> normal text in <p> use <br> to break line and
    // padding and margin to giv space. Generate Code if asked in query. in <code>
    // tag use multiple colours for dataType name, Parameter name, return type all
    // with different colours ");
    // }

    @GetMapping("/remove-note/{id}")
    public String removeNote(@PathVariable("id") String id, Principal principal) {
        chatService.deleteChatsByNoteId(id);
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
