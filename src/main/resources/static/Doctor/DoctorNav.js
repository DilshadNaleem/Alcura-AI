document.addEventListener('DOMContentLoaded', function() {
    // Mobile sidebar toggle functionality
    const menuBtn = document.getElementById('menu_bar');
    const closeBtn = document.getElementById('close_btn');
    const sidebar = document.querySelector('aside');

    if (menuBtn && closeBtn && sidebar) {
        menuBtn.addEventListener('click', () => {
            sidebar.classList.add('active');
        });

        closeBtn.addEventListener('click', () => {
            sidebar.classList.remove('active');
        });
    }

    // Theme toggle functionality (if you have a theme switcher)
    const themeToggler = document.querySelector('.theme-toggler');
    if (themeToggler) {
        themeToggler.addEventListener('click', () => {
            document.body.classList.toggle('dark-theme-variables');

            // Save theme preference to localStorage
            const isDark = document.body.classList.contains('dark-theme-variables');
            localStorage.setItem('darkMode', isDark);

            // Update icon visibility
            themeToggler.querySelector('span:nth-child(1)').classList.toggle('active');
            themeToggler.querySelector('span:nth-child(2)').classList.toggle('active');
        });
    }

    // Check for saved theme preference
    const darkMode = localStorage.getItem('darkMode') === 'true';
    if (darkMode) {
        document.body.classList.add('dark-theme-variables');
        if (themeToggler) {
            themeToggler.querySelector('span:nth-child(1)').classList.remove('active');
            themeToggler.querySelector('span:nth-child(2)').classList.add('active');
        }
    }
});