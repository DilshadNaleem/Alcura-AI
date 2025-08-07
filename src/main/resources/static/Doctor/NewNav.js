// Function to enable dark mode
function enableDarkMode() {
    document.body.classList.add('dark-theme-variables');
    localStorage.setItem('darkMode', 'enabled');
    updateThemeTogglerIcons(true);
}

// Function to disable dark mode
function disableDarkMode() {
    document.body.classList.remove('dark-theme-variables');
    localStorage.setItem('darkMode', 'disabled');
    updateThemeTogglerIcons(false);
}

// Function to update the toggler icons
function updateThemeTogglerIcons(isDarkMode) {
    const icons = document.querySelectorAll('.theme-toggler span');
    if (icons.length >= 2) {
        if (isDarkMode) {
            icons[0].classList.remove('active'); // light icon
            icons[1].classList.add('active');    // dark icon
        } else {
            icons[0].classList.add('active');    // light icon
            icons[1].classList.remove('active'); // dark icon
        }
    }
}

// Initialize theme on page load
document.addEventListener('DOMContentLoaded', function() {
    // Check for saved theme preference
    const darkMode = localStorage.getItem('darkMode') === 'enabled';

    // Set initial theme
    if (darkMode) {
        enableDarkMode();
    } else {
        disableDarkMode();
    }

    // Setup theme toggler
    const themeToggler = document.querySelector('.theme-toggler');
    if (themeToggler) {
        themeToggler.addEventListener('click', () => {
            if (document.body.classList.contains('dark-theme-variables')) {
                disableDarkMode();
            } else {
                enableDarkMode();
            }
        });
    }

    // Menu toggle functionality - UPDATED TO USE menu_bar
    document.getElementById('menu_bar').addEventListener('click', function() {
        document.querySelector('aside').classList.toggle('active');
    });

    document.getElementById('close_btn').addEventListener('click', function() {
        document.querySelector('aside').classList.remove('active');
    });
});

// Image preview functionality
function previewImage(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            document.getElementById('profileImage').src = e.target.result;
        }

        reader.readAsDataURL(input.files[0]);
    }
}