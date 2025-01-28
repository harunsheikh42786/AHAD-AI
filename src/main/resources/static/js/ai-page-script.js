document.addEventListener('click', function (event) {
    var isClickInside = event.target.closest('.form-group');
    if (!isClickInside) {
        // Hide all opr-btn and show all dots-btn
        var allOprButtons = document.querySelectorAll('.opr-btn');
        var allDotsButtons = document.querySelectorAll('.dots-btn');
        var allNameInputs = document.querySelectorAll('.name-input');
        var allSubmitButtons = document.querySelectorAll('.btn-submit');

        allOprButtons.forEach(function (button) {
            button.classList.add('d-none');
        });

        allDotsButtons.forEach(function (button) {
            button.classList.remove('d-none');
        });

        allNameInputs.forEach(function (input) {
            if (!input.classList.contains('d-none')) {
                saveNoteName(input); // Save note name when hiding input
            }
            input.classList.add('d-none');
        });

        allSubmitButtons.forEach(function (button) {
            button.classList.remove('d-none');
        });
    }
});

function showButtons(event) {
    event.stopPropagation(); // Prevent click event from bubbling up

    // Toggle buttons for the current note
    var currentElement = event.target.closest('.form-group');
    var buttons = currentElement.querySelectorAll('.opr-btn');
    buttons.forEach(function (button) {
        button.classList.toggle('d-none');
    });
    var dots = currentElement.querySelector('.dots-btn');
    dots.classList.toggle('d-none');
}

function renameNote(event) {
    event.stopPropagation(); // Prevent click event from bubbling up

    var currentElement = event.target.closest('.form-group');
    var nameInput = currentElement.querySelector('.name-input');
    var submitButton = currentElement.querySelector('.btn-submit');

    nameInput.classList.remove('d-none');
    submitButton.classList.add('d-none');

    var buttons = currentElement.querySelectorAll('.opr-btn');
    buttons.forEach(function (button) {
        button.classList.add('d-none');
    });

    var dots = currentElement.querySelector('.dots-btn');
    dots.classList.add('d-none');
}

function saveNoteName(inputElement) {
    var formGroup = inputElement.closest('.form-group');
    var noteId = formGroup.querySelector('input[name="noteId"]').value;
    var noteName = formGroup.querySelector('input[name="noteName"]').value;

    fetch('/ai/update-note-name', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            id: noteId,
            name: noteName
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); // Adjust to `.text()` if the response is a plain text message
        })
        .then(data => {
            console.log('Success:', data);
            location.reload(); // reload page
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function deleteChat(event) {
    event.stopPropagation(); // Prevents the event from bubbling up

    var chatId = event.target.closest('.actions').querySelector('input[name="chatId"]').value;
    var noteId = event.target.closest('.actions').querySelector('input[name="noteId"]').value;

    fetch('/ai/delete-chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            chatId: chatId,
            noteId: noteId
        })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(`Network response was not ok: ${text}`);
                });
            }
            return response.text(); // Adjust to `.text()` if the response is a plain text message
        })
        .then(data => {
            console.log('Success:', data);
            // Remove the chat element from the DOM
            event.target.closest('.row').remove();
            location.reload(); // reload page
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function copyChat(event) {
    event.stopPropagation(); // Prevents the event from bubbling up

    // Find the closest parent div with class 'chat-response' and then get the HTML content of the <p> tag
    var htmlContent = event.target.closest('.chat-response').querySelector('p').innerHTML;

    // Create a new DOM element to parse the HTML content and extract plain text
    var parser = new DOMParser();
    var doc = parser.parseFromString(htmlContent, 'text/html');
    var plainText = doc.body.textContent || doc.body.innerText;  // Extract the plain text

    // Copy the plain text to clipboard
    navigator.clipboard.writeText(plainText).then(() => {
        console.log('Chat content copied to clipboard');
    }).catch((error) => {
        console.error('Error copying chat content:', error);
    });
}





function scrollToBottom() {
        const chatContainer = document.getElementById('chat-container');
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

    // Call scrollToBottom when the page loads
    window.onload = function() {
        scrollToBottom();
    };