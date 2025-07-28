document.addEventListener('DOMContentLoaded', function() {
    // Theme toggler functionality
    const themeToggler = document.querySelector('.theme-toggler');

    // Function to apply dark mode styles
    function applyDarkModeStyles() {
        document.body.classList.add('dark-theme-variables');
        themeToggler.querySelector('span:nth-child(1)').classList.remove('active');
        themeToggler.querySelector('span:nth-child(2)').classList.add('active');
        localStorage.setItem('darkMode', 'true');
    }

    // Function to apply light mode styles
    function applyLightModeStyles() {
        document.body.classList.remove('dark-theme-variables');
        themeToggler.querySelector('span:nth-child(1)').classList.add('active');
        themeToggler.querySelector('span:nth-child(2)').classList.remove('active');
        localStorage.setItem('darkMode', 'false');
    }

    // Theme toggle event listener
    if (themeToggler) {
        themeToggler.addEventListener('click', function() {
            if (document.body.classList.contains('dark-theme-variables')) {
                applyLightModeStyles();
            } else {
                applyDarkModeStyles();
            }
        });
    }

    // Check for saved theme preference
    const darkMode = localStorage.getItem('darkMode') === 'true';
    if (darkMode) {
        applyDarkModeStyles();
    }

    // Sidebar toggle functionality for mobile
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

    // Set active link based on current URL
    function setActiveLink() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.sidebar a');

        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === currentPath) {
                link.classList.add('active');
            }
        });
    }

    setActiveLink();
});