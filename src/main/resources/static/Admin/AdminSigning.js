document.addEventListener("DOMContentLoaded", function () {
    const signUpButton = document.getElementById("signUp");
    const signInButton = document.getElementById("signIn");
    const container = document.getElementById("container");

    signUpButton.addEventListener("click", () => {
        container.classList.add("right-panel-active");
    });

    signInButton.addEventListener("click", () => {
        container.classList.remove("right-panel-active");
    });

    // Add form submission validation
    document.getElementById("signupForm").addEventListener("submit", function (event) {
        if (!validateSignupForm()) {
            event.preventDefault();
        }
    });
});

// ✅ Signup Form Validation Function
function validateSignupForm() {
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const contactNumber = document.getElementById("contactNumber").value.trim();
    const password = document.getElementById("newpassword").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();
    const nic = document.getElementById("nic").value.trim();

    // First name validation
    if (!/^[A-Za-z]{2,}$/.test(firstName)) {
        alert('First name must be at least 2 characters and contain only letters.');
        return false;
    }

    // Last name validation
    if (!/^[A-Za-z]{2,}$/.test(lastName)) {
        alert('Last name must be at least 2 characters and contain only letters.');
        return false;
    }

    // Email validation
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        alert('Please enter a valid email address (e.g. name@example.com).');
        return false;
    }

    // Password validation (minimum 6 characters)
    if (password.length < 6) {
        alert('Password must be at least 6 characters long.');
        return false;
    }

    // Confirm password match
    if (password !== confirmPassword) {
        alert('Passwords do not match.');
        return false;
    }

    // Contact number validation (example: 10-digit number)
    if (!/^\d{10}$/.test(contactNumber)) {
        alert('Please enter a valid 10-digit contact number.');
        return false;
    }

    // NIC validation (example: old or new Sri Lankan NIC formats)
    if (!/^(\d{9}[vVxX]|\d{12})$/.test(nic)) {
        alert('Please enter a valid NIC (e.g. 123456789V or 200012345678).');
        return false;
    }

    return true;
}
