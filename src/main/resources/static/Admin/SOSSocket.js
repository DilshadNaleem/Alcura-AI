// socket.js - Improved WebSocket handling for emergency alerts with click notifications
let stompClient = null;
let emergencyCount = localStorage.getItem('emergencyCount') ? parseInt(localStorage.getItem('emergencyCount')) : 0;

console.debug('[WebSocket] Initializing with emergencyCount:', emergencyCount);

// Initialize WebSocket connection
function connectWebSocket() {
    console.debug('[WebSocket] Attempting to connect...');
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function(frame) {
        console.debug('[WebSocket] Connected successfully. Frame:', frame);

        stompClient.subscribe('/topic/admin/emergencies', function(message) {
            console.debug('[WebSocket] Received emergency message:', message.body);
            const emergency = JSON.parse(message.body);

            // Only increment if not on SOS view page
            if (!window.location.pathname.includes('/Admin/SOSView')) {
                emergencyCount++;
                localStorage.setItem('emergencyCount', emergencyCount);
                console.debug('[WebSocket] Updated emergencyCount:', emergencyCount);
                updateSOSBadge(emergencyCount);

                // Play sound and show notification if needed
                try {
                    console.debug('[WebSocket] Attempting to play alert sound');
                    new Audio('/sounds/emergency-alert.mp3').play();
                } catch (e) {
                    console.error('[WebSocket] Error playing sound:', e);
                }

                if (Notification.permission === "granted") {
                    console.debug('[WebSocket] Showing browser notification');
                    new Notification("Emergency Alert", {
                        body: `New emergency at ${emergency.hospital}`
                    });
                }
            }
        });

        // Initial badge update
        updateSOSBadge(emergencyCount);
    }, function(error) {
        console.error('[WebSocket] Connection error:', error);
        // Attempt to reconnect after delay
        setTimeout(connectWebSocket, 5000);
    });
}

// Update the SOS badge
function updateSOSBadge(count) {
    console.debug('[WebSocket] Updating badges with count:', count);
    const badges = document.querySelectorAll('.msg_count, .notification-badge');
    badges.forEach(badge => {
        badge.textContent = count;
        badge.style.display = count > 0 ? 'inline-block' : 'none';
    });
}

// Reset emergency count when viewing SOS pages
function checkAndResetBadge() {
    console.debug('[WebSocket] Checking path:', window.location.pathname);

    if (window.location.pathname.includes('/Admin/SOSView')) {
        console.debug('[WebSocket] On SOS view page - resetting counter');
        emergencyCount = 0;
        localStorage.setItem('emergencyCount', emergencyCount);
        updateSOSBadge(emergencyCount);
    }
}

// Handle link clicks with alert
function handleLinkClicks(e) {
    // Check if the clicked element or its parent is a link
    const target = e.target.closest('a[href]');

    if (target &&
        !target.href.includes('javascript:') &&
        !target.hasAttribute('data-no-alert') &&
        target.target !== '_blank') {



        // Reset counter immediately if going to SOSView
        if (target.href.includes('/Admin/SOSView')) {
            emergencyCount = 0;
            localStorage.setItem('emergencyCount', emergencyCount);
            updateSOSBadge(emergencyCount);
        }


    }
}

// Handle navigation more reliably
function handleNavigation() {
    checkAndResetBadge();
}

// Initialize when page loads
document.addEventListener('DOMContentLoaded', function() {
    console.debug('[WebSocket] DOM fully loaded');
    connectWebSocket();
    checkAndResetBadge(); // This will reset if we're already on SOSView

    // Request notification permission if needed
    if (Notification.permission !== "granted" && Notification.permission !== "denied") {
        console.debug('[WebSocket] Requesting notification permission');
        Notification.requestPermission().then(permission => {
            console.debug('[WebSocket] Notification permission:', permission);
        });
    }

    // Add click event listener for all links
    document.addEventListener('click', handleLinkClicks);
});

// Set up navigation listeners
window.addEventListener('popstate', handleNavigation);
window.addEventListener('pushstate', handleNavigation);
window.addEventListener('replacestate', handleNavigation);

// Override history methods to ensure our handler is called
const originalPushState = history.pushState;
history.pushState = function() {
    originalPushState.apply(this, arguments);
    setTimeout(handleNavigation, 0);
};

const originalReplaceState = history.replaceState;
history.replaceState = function() {
    originalReplaceState.apply(this, arguments);
    setTimeout(handleNavigation, 0);
};

