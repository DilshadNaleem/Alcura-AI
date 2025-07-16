document.addEventListener('DOMContentLoaded', function() {
        // Sidebar functionality
        const sideMenu = document.querySelector('aside');
        const menuBtn = document.querySelector('#menu_bar');
        const closeBtn = document.querySelector('#close_btn');
        const themeToggler = document.querySelector('.theme-toggler');
        const lightModeIcon = document.getElementById('light-mode');
        const darkModeIcon = document.getElementById('dark-mode');

        // Check for saved theme preference on page load
        if (localStorage.getItem('darkMode') === 'enabled') {
            document.body.classList.add('dark-theme-variables');
            lightModeIcon.classList.remove('active');
            darkModeIcon.classList.add('active');
        } else {
            document.body.classList.remove('dark-theme-variables');
            lightModeIcon.classList.add('active');
            darkModeIcon.classList.remove('active');
        }

        menuBtn.addEventListener('click', () => {
            sideMenu.style.display = "block";
        });

        closeBtn.addEventListener('click', () => {
            sideMenu.style.display = "none";
        });

        // Theme toggler with localStorage persistence
        themeToggler.addEventListener('click', () => {
            document.body.classList.toggle('dark-theme-variables');

            // Update button states
            lightModeIcon.classList.toggle('active');
            darkModeIcon.classList.toggle('active');

            // Save theme preference to localStorage
            if (document.body.classList.contains('dark-theme-variables')) {
                localStorage.setItem('darkMode', 'enabled');
            } else {
                localStorage.setItem('darkMode', 'disabled');
            }
        });
    });