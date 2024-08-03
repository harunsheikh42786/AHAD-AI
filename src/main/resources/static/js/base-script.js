function search(event) {
    const input = event.target;
    const list = input.nextElementSibling;
    const text = input.value;

    if (text.length === 0) {
        list.innerHTML = ''; // Clear the list if the search input is empty
        list.classList.remove('show'); // Hide the list
        return;
    }

    fetch(`/user/search-friend/${text}`)
        .then(response => response.json())
        .then(data => {
            list.innerHTML = ''; // Clear the previous list

            if (Array.isArray(data) && data.length > 0) {
                data.forEach(user => {
                    const listItem = document.createElement('a');
                    listItem.className = 'list-group-item list-group-item-action'; // Added 'list-group-item-action' class for better styling
                    listItem.href = `/user/friend-profile/${user.id}`;
                    listItem.textContent = user.name; // Adjust as per your user object structure
                    list.appendChild(listItem);
                });
                list.classList.add('show'); // Show the list
            } else {
                list.classList.remove('show'); // Hide the list if no results
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
            list.classList.remove('show'); // Hide the list on error
        });
}