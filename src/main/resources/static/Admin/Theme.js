document.addEventListener('DOMContentLoaded', function() {
        const darkMode = localStorage.getItem('darkMode') === 'true';
        const themeToggler = document.querySelector('.theme-toggler');

        if (darkMode) {
            document.body.classList.add('dark-theme-variables');
            themeToggler.querySelector('span:nth-child(1)').classList.remove('active');
            themeToggler.querySelector('span:nth-child(2)').classList.add('active');
        }

        // Theme toggler functionality
        themeToggler.addEventListener('click', () => {
            document.body.classList.toggle('dark-theme-variables');
            themeToggler.querySelector('span:nth-child(1)').classList.toggle('active');
            themeToggler.querySelector('span:nth-child(2)').classList.toggle('active');

            // Save the current theme preference to localStorage
            const isDarkMode = document.body.classList.contains('dark-theme-variables');
            localStorage.setItem('darkMode', isDarkMode);
        });

        // Sidebar functionality
        const sideMenu = document.querySelector('aside');
        const menuBtn = document.querySelector('#menu_bar');
        const closeBtn = document.querySelector('#close_btn');

        menuBtn.addEventListener('click', () => {
            sideMenu.style.display = "block";
        });

        closeBtn.addEventListener('click', () => {
            sideMenu.style.display = "none";
        });
    });


        // Highlight active navigation link based on current URL
        document.addEventListener('DOMContentLoaded', function() {
            const currentPath = window.location.pathname;
            const navLinks = document.querySelectorAll('.sidebar a');

            navLinks.forEach(link => {
                if (link.getAttribute('href') === currentPath) {
                    link.classList.add('active');
                }
            });
        });
