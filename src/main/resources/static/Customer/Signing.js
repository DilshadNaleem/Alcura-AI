const signUpButton = document.getElementById("signUp");
const signInButton = document.getElementById("signIn");
const container = document.getElementById('container');

// Form fields configuration
const signupFields = [
    { id: "firstName" },
    { id: "lastName" },
    { id: "email" },
    { id: "newpassword", isPassword: true },
    { id: "confirmPassword", isPassword: true },
    { id: "contactNumber" },
    { id: "nic" }
];

const signinFields = [
    { id: "signinEmail" },
    { id: "password", isPassword: true }
];

// Event Listeners
signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});

// Form validation
document.getElementById("signupForm").addEventListener("submit", function(event) {
    if (!validateSignupForm()) {
        event.preventDefault();
    }
});

function validateSignupForm() {
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const contactNumber = document.getElementById("contactNumber").value.trim();
    const password = document.getElementById("newpassword").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();
    const nic = document.getElementById("nic").value.trim();

    // Clear previous error messages
    document.querySelectorAll('.error-message').forEach(el => el.remove());

    let isValid = true;

    if (!/^[A-Za-z]{2,}$/.test(firstName)) {
        showError("firstName", "First name must be at least 2 characters and contain only letters.");
        isValid = false;
    }

    if (!/^[A-Za-z]{2,}$/.test(lastName)) {
        showError("lastName", "Last name must be at least 2 characters and contain only letters.");
        isValid = false;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        showError("email", "Please enter a valid email address.");
        isValid = false;
    }

    if (!/^\d{10}$/.test(contactNumber)) {
        showError("contactNumber", "Contact number must be exactly 10 digits.");
        isValid = false;
    }

    if (password.length < 6) {
        showError("newpassword", "Password must be at least 6 characters long.");
        isValid = false;
    }

    if (password !== confirmPassword) {
        showError("confirmPassword", "Passwords do not match. Please try again.");
        isValid = false;
    }

    // NIC validation (for both old and new Sri Lankan NIC formats)
    if (!/^(\d{9}[vVxX]|\d{12})$/.test(nic)) {
        showError("nic", "Please enter a valid NIC (e.g. 123456789V or 200012345678).");
        isValid = false;
    }

    return isValid;
}

function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.style.color = 'red';
    errorElement.style.fontSize = '0.8em';
    errorElement.style.marginTop = '5px';
    errorElement.textContent = message;

    // Insert after the field
    field.parentNode.insertBefore(errorElement, field.nextSibling);

    // Highlight the field
    field.style.borderColor = 'red';

    // Remove error when field is focused
    field.addEventListener('focus', function() {
        field.style.borderColor = '';
        errorElement.remove();
    }, { once: true });
}