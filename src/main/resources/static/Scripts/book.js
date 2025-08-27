document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/books')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch books');
            }
            return response.json();
        })
        .then(books => {
            const bookGrid = document.querySelector('.book-grid');
            bookGrid.innerHTML = books.map(book => `
                <div class="book-card">
                    <img src="${book.imageUrl}" alt="${book.title}">
                    <h3>${book.title}</h3>
                    <p>${book.author}</p>
                </div>
            `).join('');
        })
        .catch(error => {
            console.error(error);
            alert('Error loading books');
        });
});