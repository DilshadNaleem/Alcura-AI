 document.addEventListener('DOMContentLoaded', function() {

           const sideMenu = document.querySelector('.sidebar-container');
           const menuBtn = document.getElementById('menu_toggle');
           const closeBtn = document.getElementById('close_btn');

           // Toggle sidebar on menu button click
           menuBtn.addEventListener('click', () => {
               sideMenu.classList.add('active');
           });

           // Close sidebar on close button click
           closeBtn.addEventListener('click', () => {
               sideMenu.classList.remove('active');
           });

           // Set active link based on current page
           const currentPath = window.location.pathname;
           const navLinks = document.querySelectorAll('.sidebar-menu a');

           navLinks.forEach(link => {
               if (link.getAttribute('href') === currentPath) {
                   link.classList.add('active');
               }
           });

           // Close sidebar when clicking outside (optional)
           document.addEventListener('click', (event) => {
               if (!sideMenu.contains(event.target) &&
                   event.target !== menuBtn &&
                   sideMenu.classList.contains('active')) {
                   sideMenu.classList.remove('active');
               }
           });
       });