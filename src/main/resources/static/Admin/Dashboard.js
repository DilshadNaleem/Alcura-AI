// Simple Dark Mode for Face Enrollment
document.addEventListener('DOMContentLoaded', function() {
    const darkMode = localStorage.getItem('darkMode') === 'true';
    const container = document.querySelector('.container');

    if (darkMode && container) {
        container.style.background = '#212529';
        container.style.color = '#f8f9fa';

        // Adjust button colors for dark mode
        document.querySelectorAll('.btn').forEach(btn => {
            if (btn.classList.contains('btn-secondary')) {
                btn.style.borderColor = '#f8f9fa';
                btn.style.color = '#f8f9fa';
            }
        });
    }
});


