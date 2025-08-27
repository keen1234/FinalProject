// Sample book data
const books = [
    {
        id: 1,
        title: "The Silent Echo",
        author: "Eleanor Rivers",
        date: "2023-06-15",
        genre: ["mystery", "fiction"],
        description: "A gripping mystery set in a small coastal town where ancient secrets resurface. Detective Miller must solve a cold case that connects to a series of unexplained disappearances.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/4bcb2fbe-678f-4174-a984-a97e26b8b940.png"
    },
    {
        id: 2,
        title: "Cosmic Horizons",
        author: "Marcus Zhang",
        date: "2022-11-03",
        genre: ["sci-fi", "fiction"],
        description: "Humanity's first interstellar mission encounters an ancient alien civilization. Explore the depths of space and the limits of human understanding in this epic sci-fi adventure.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/15d43165-5405-4d4c-a38a-b72e2d46fe77.png"
    },
    {
        id: 3,
        title: "Empire's Fall",
        author: "Sarah Johnson",
        date: "2021-09-22",
        genre: ["history", "biography"],
        description: "The definitive account of the Roman Empire's decline, told through the eyes of its last great emperor. Meticulously researched and powerfully written.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/eba30d05-cf86-4c81-b31a-d59c67269bce.png"
    },
    {
        id: 4,
        title: "Whispers of Magic",
        author: "Aisha Patel",
        date: "2023-03-08",
        genre: ["fantasy", "fiction"],
        description: "In a world where magic is fading, a young apprentice discovers she holds the key to restoring balance. A tale of courage, friendship, and ancient powers.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/4f4ca5ff-ba2e-4f68-bba1-502ab7e70076.png"
    },
    {
        id: 5,
        title: "The Midnight Library",
        author: "Nora Roberts",
        date: "2023-01-14",
        genre: ["fiction", "mystery"],
        description: "Between life and death there is a library, and within that library, the shelves go on forever. Every book provides a chance to try another life you could have lived.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/b82c332b-5568-415f-9458-b50717be287f.png"
    },
    {
        id: 6,
        title: "Quantum Realms",
        author: "Dr. Alan Turing",
        date: "2022-07-30",
        genre: ["sci-fi", "fiction"],
        description: "A groundbreaking exploration of quantum computing and its potential to reshape reality. Blends cutting-edge science with thrilling narrative.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/0b538c8f-1f89-4f3a-869c-184c0ecc495c.png"
    },
    {
        id: 7,
        title: "Ancient Wisdom",
        author: "Li Chen",
        date: "2021-12-05",
        genre: ["history", "biography"],
        description: "Rediscover the lost knowledge of ancient civilizations and how their wisdom can guide us in the modern world. A journey through time and philosophy.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/d7219e03-d549-4826-86a2-88c911b07f70.png"
    },
    {
        id: 8,
        title: "Dragon's Legacy",
        author: "Kaelen Stormrider",
        date: "2023-08-19",
        genre: ["fantasy", "fiction"],
        description: "The epic conclusion to the Dragon Rider trilogy. Alliances are forged, kingdoms fall, and the fate of the realm hangs in the balance.",
        image: "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/8bb084c4-5ff8-4fea-839e-925f69e5ee11.png"
    }
];

// DOM Elements
const booksGrid = document.getElementById('booksGrid');
const searchInput = document.getElementById('searchInput');
const sortButtons = document.querySelectorAll('.sort-btn');
const genreSort = document.getElementById('genreSort');
const bookDetail = document.getElementById('bookDetail');
const closeDetail = document.getElementById('closeDetail');
const loading = document.getElementById('loading');

// State variables
let currentSort = 'title';
let currentGenre = '';
let currentBooks = [...books];

// Initialize application
function initializeApp() {
    showLoading(true);
    setTimeout(() => {
        renderBooks(books);
        showLoading(false);
        setupEventListeners();
    }, 1000);
}

// Setup event listeners
function setupEventListeners() {
    // Search functionality
    searchInput.addEventListener('input', debounce((e) => {
        const searchTerm = e.target.value.toLowerCase();
        filterBooks(searchTerm);
    }, 300));

    // Sort functionality
    sortButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            sortButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentSort = btn.dataset.sort;
            sortBooks();
        });
    });

    // Genre filter
    genreSort.addEventListener('change', (e) => {
        currentGenre = e.target.value;
        filterBooks(searchInput.value.toLowerCase());
    });

    // Close detail view
    closeDetail.addEventListener('click', () => {
        hideBookDetail();
    });

    // Close detail with ESC key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            hideBookDetail();
        }
    });

    // Close modal when clicking outside
    bookDetail.addEventListener('click', (e) => {
        if (e.target === bookDetail) {
            hideBookDetail();
        }
    });
}

// Render books grid
function renderBooks(booksToRender) {
    booksGrid.innerHTML = '';

    if (booksToRender.length === 0) {
        booksGrid.innerHTML = `
            <div class="text-center mb-4">
                <i class="fas fa-book-open text-6xl text-gray-300 mb-4"></i>
                <h3 class="text-xl font-semibold text-gray-600 mb-2">No books found</h3>
                <p class="text-gray-500">Try adjusting your search criteria</p>
            </div>
        `;
        return;
    }

    booksToRender.forEach(book => {
        const bookCard = createBookCard(book);
        booksGrid.appendChild(bookCard);
    });
}

// Create book card element
function createBookCard(book) {
    const bookCard = document.createElement('div');
    bookCard.className = 'book-card';
    bookCard.innerHTML = `
        <div class="book-card-image">
            <img src="${book.image}" alt="${book.title} by ${book.author}">
            <div class="genre-badges">
                ${book.genre.map(g => `<span class="genre-badge">${g}</span>`).join('')}
            </div>
        </div>
        <div class="book-card-content">
            <h3>${book.title}</h3>
            <p class="author">by ${book.author}</p>
            <p class="date">Published: ${formatDate(book.date)}</p>
            <p class="description">${book.description}</p>
        </div>
    `;

    bookCard.addEventListener('click', () => showBookDetail(book));
    return bookCard;
}

// Format date for display
function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// Show book detail modal
function showBookDetail(book) {
    updateDetailModal(book);
    bookDetail.classList.add('show');
    document.body.style.overflow = 'hidden';
}

// Hide book detail modal
function hideBookDetail() {
    bookDetail.classList.remove('show');
    document.body.style.overflow = 'auto';
}

// Update detail modal content
function updateDetailModal(book) {
    document.getElementById('detailImage').src = book.image;
    document.getElementById('detailImage').alt = `${book.title} by ${book.author}`;
    document.getElementById('detailTitle').textContent = book.title;
    document.getElementById('detailAuthor').textContent = `by ${book.author}`;
    document.getElementById('detailDate').textContent = `Published: ${formatDate(book.date)}`;
    document.getElementById('detailDescription').textContent = book.description;

    updateGenreBadges(book.genre);
}

// Update genre badges in detail modal
function updateGenreBadges(genres) {
    const genresContainer = document.getElementById('detailGenres');
    genresContainer.innerHTML = '';

    genres.forEach(genre => {
        const badge = document.createElement('span');
        badge.className = 'genre-badge';
        badge.textContent = genre;
        genresContainer.appendChild(badge);
    });
}

// Filter books based on search term and genre
function filterBooks(searchTerm = '') {
    let filtered = books.filter(book => {
        const matchesSearch = book.title.toLowerCase().includes(searchTerm) ||
            book.author.toLowerCase().includes(searchTerm) ||
            book.genre.some(g => g.toLowerCase().includes(searchTerm)) ||
            book.description.toLowerCase().includes(searchTerm);

        const matchesGenre = currentGenre === '' || book.genre.includes(currentGenre);

        return matchesSearch && matchesGenre;
    });

    currentBooks = filtered;
    sortBooks();
}

// Sort books based on current sort criteria
function sortBooks() {
    currentBooks.sort((a, b) => {
        switch(currentSort) {
            case 'title':
                return a.title.localeCompare(b.title);
            case 'author':
                return a.author.localeCompare(b.author);
            case 'date':
                return new Date(b.date) - new Date(a.date);
            default:
                return 0;
        }
    });
    renderBooks(currentBooks);
}

// Show/hide loading state
function showLoading(show) {
    loading.style.display = show ? 'flex' : 'none';
}

// Debounce function for search input
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', initializeApp);

// Add keyboard navigation support
document.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
        e.preventDefault();
        const bookCards = document.querySelectorAll('.book-card');
        const currentIndex = Array.from(bookCards).findIndex(card => card === document.activeElement);

        if (bookCards.length > 0) {
            let nextIndex;
            if (e.key === 'ArrowDown') {
                nextIndex = (currentIndex + 1) % bookCards.length;
            } else {
                nextIndex = (currentIndex - 1 + bookCards.length) % bookCards.length;
            }

            bookCards[nextIndex].focus();
        }
    }
});

// Add focus styles for accessibility
document.addEventListener('DOMContentLoaded', () => {
    const style = document.createElement('style');
    style.textContent = `
        .book-card:focus {
            outline: 2px solid #3b82f6;
            outline-offset: 2px;
        }
    `;
    document.head.appendChild(style);
});

// Export functions for testing (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        filterBooks,
        sortBooks,
        formatDate,
        createBookCard
    };
}
