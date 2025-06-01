const signUpButton = document.getElementById("signUp");
const signInButton = document.getElementById("signIn");
const container = document.getElementById('container');
const voiceControl = document.getElementById("voiceControl");

// Initialize speech recognition
const recognition = new (window.SpeechRecognition ||
                     window.webkitSpeechRecognition ||
                     window.mozSpeechRecognition ||
                     window.msSpeechRecognition)();

recognition.continuous = false;
recognition.interimResults = false;
recognition.lang = "en-US";

// Form fields configuration
const signupFields = [
    { id: "firstName", prompt: "First name. Please say your first name." },
    { id: "lastName", prompt: "Last name. Please say your last name." },
    { id: "email", prompt: "Email address. Please say your email." },
    { id: "newpassword", prompt: "Password. Please create and say your password.", isPassword: true },
    { id: "confirmPassword", prompt: "Confirm password. Please say your password again.", isPassword: true },
    { id: "contactNumber", prompt: "Contact number. Please say your 10-digit phone number." },
    { id: "nic", prompt: "NIC number. Please say your national identification number." }
];

const signinFields = [
    { id: "signinEmail", prompt: "Email. Please say your registered email." },
    { id: "password", prompt: "Password. Please say your password.", isPassword: true }
];

// State variables
let currentFieldIndex = 0;
let isFormReadingMode = false;
let isListening = false;
let currentFormType = 'signin';
let saidNotFound = false;

// Event Listeners
signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
    currentFormType = 'signup';
    speak("Showing sign up form.");
    setTimeout(() => readFormFields('signup'), 1000);
});

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
    currentFormType = 'signin';
    speak("Showing sign in form.");
    setTimeout(() => readFormFields('signin'), 1000);
});

voiceControl.addEventListener("click", toggleVoiceControl);

// Form validation
document.getElementById("signupForm").addEventListener("submit", function(event) {
    if (!validateSignupForm()) {
        event.preventDefault();
    }
});

// Voice control functions
function toggleVoiceControl() {
    if (isListening) {
        recognition.stop();
        voiceControl.classList.remove("listening");
        speak("Voice control turned off.");
    } else {
        voiceControl.classList.add("listening");
        speak("Voice control activated. Please speak now.");
        recognition.start();
    }
    isListening = !isListening;
}

function readFormFields(formType) {
    saidNotFound = false; // Reset the not found flag
    isFormReadingMode = true;
    currentFieldIndex = 0;
    currentFormType = formType;
    const fields = formType === 'signup' ? signupFields : signinFields;

    function readNextField() {
        if (currentFieldIndex < fields.length) {
            const field = fields[currentFieldIndex];
            const fieldName = field.id.replace(/([A-Z])/g, ' $1').toLowerCase();
            speak(`Please provide your ${fieldName}. ${field.prompt}`);

            // Set timeout if no response
            setTimeout(() => {
                if (isFormReadingMode && currentFieldIndex < fields.length && currentFieldIndex === fields.findIndex(f => f.id === field.id)) {
                    speak("I didn't hear your response. Please say your " + fieldName);
                    readNextField();
                }
            }, 10000);
        } else {
            speak("Form completed. Say 'submit' to submit or 'review' to check your information.");
            isFormReadingMode = false;
        }
    }

    readNextField();
}

function processVoiceCommand(transcript) {
    console.log("Voice command:", transcript);
    const originalTranscript = transcript;
    transcript = transcript.toLowerCase().trim();

    // Navigation commands
    if (transcript.includes("sign in") || transcript.includes("login")) {
        container.classList.remove("right-panel-active");
        currentFormType = 'signin';
        speak("Showing sign in form.");
        setTimeout(() => readFormFields('signin'), 1000);
        return;
    }

    if (transcript.includes("sign up") || transcript.includes("register")) {
        container.classList.add("right-panel-active");
        currentFormType = 'signup';
        speak("Showing sign up form.");
        setTimeout(() => readFormFields('signup'), 1000);
        return;
    }

    // Form filling
    if (isFormReadingMode) {
        const fields = currentFormType === 'signup' ? signupFields : signinFields;

        // Check if user is trying to edit a specific field
        for (let i = 0; i < fields.length; i++) {
            const fieldName = fields[i].id.replace(/([A-Z])/g, ' $1').toLowerCase();
            if (transcript.includes(fieldName)) {
                currentFieldIndex = i;
                const value = document.getElementById(fields[i].id).value;
                if (value) {
                    speak(`Editing ${fieldName}. Current value is ${fields[i].isPassword ? "••••••" : value}. Please say the new value.`);
                } else {
                    speak(`Editing ${fieldName}. Please say the value.`);
                }
                return;
            }
        }

        // Process the input for the current field
        const currentField = fields[currentFieldIndex];
        processFieldInput(originalTranscript, currentField);
        return;
    }

    // Review command
    if (transcript.includes("review") || transcript.includes("check")) {
        reviewFormInformation();
        return;
    }

    // Edit command - FIXED VERSION
    if (transcript.includes("edit")) {
        const fields = currentFormType === 'signup' ? signupFields : signinFields;
        let fieldList = fields.map(f => f.id.replace(/([A-Z])/g, ' $1').toLowerCase());
        speak("Which field would you like to edit? Available fields are: " + fieldList.join(", "));
        return;
    }

    // Submit form
    if (transcript.includes("submit")) {
        submitCurrentForm();
        return;
    }

    // Help command
    if (transcript.includes("help") || transcript.includes("what can i say")) {
        speak("You can say: 'Sign in', 'Sign up', 'Submit', 'Review', 'Edit', or specific field names.");
        return;
    }

    // Default response
    if (!saidNotFound) {
        speak("Not found");
        saidNotFound = true;
    }
}

function processFieldInput(transcript, field) {
    // Remove all spaces first
    let cleanedTranscript = transcript.replace(/\s+/g, '');

    // Email handling
    if (field.id.includes("email")) {
        processEmailInput(cleanedTranscript, field.id);
        return;
    }

    // Password handling
    if (field.isPassword) {
        processPasswordInput(cleanedTranscript, field.id);
        return;
    }

    // Other fields
    processRegularInput(cleanedTranscript, field);
}

function processEmailInput(transcript, fieldId) {
    // Process email format
    let emailText = transcript.replace(/\s*at\s*/g, "@")
                             .replace(/dot/g, ".")
                             .replace(/underscore/g, "_")
                             .replace(/dash/g, "-");

    const emailMatch = emailText.match(/([a-z0-9._-]+@[a-z0-9._-]+\.[a-z0-9._-]+)/i);
    if (emailMatch) {
        const email = emailMatch[0];
        document.getElementById(fieldId).value = email;
        speak(`Email set to ${email.split("@").join(" at ")}`);
        moveToNextField();
    } else {
        if (!saidNotFound) {
            speak("Not found");
            saidNotFound = true;
        }
    }
}

function processPasswordInput(transcript, fieldId) {
    // Remove common password-related words
    const password = transcript.replace(/password|pass|is|my|the/gi, "");

    if (password.length > 0) {
        document.getElementById(fieldId).value = password;

        if (fieldId.includes("confirm")) {
            speak("Password confirmed");
        } else {
            speak("Password set");
        }

        moveToNextField();
    } else {
        if (!saidNotFound) {
            speak("Not found");
            saidNotFound = true;
        }
    }
}

function processRegularInput(transcript, field) {
    // Remove field name references if present
    const fieldName = field.id.replace(/([A-Z])/g, ' $1').toLowerCase();
    let cleanedTranscript = transcript.replace(new RegExp(fieldName, 'gi'), '')
                                    .replace(/is|my|the/gi, '');

    // Only update if we have actual content
    if (cleanedTranscript) {
        document.getElementById(field.id).value = cleanedTranscript;
        const displayName = field.id.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
        speak(`${displayName} set to ${cleanedTranscript}`);
        moveToNextField();
    } else {
        if (!saidNotFound) {
            speak("Not found");
            saidNotFound = true;
        }
    }
}

function moveToNextField() {
    saidNotFound = false; // Reset for next field
    const fields = currentFormType === 'signup' ? signupFields : signinFields;
    currentFieldIndex++;

    if (currentFieldIndex < fields.length) {
        setTimeout(() => {
            const nextField = fields[currentFieldIndex];
            const fieldName = nextField.id.replace(/([A-Z])/g, ' $1').toLowerCase();
            speak(`Now please provide your ${fieldName}. ${nextField.prompt}`);
        }, 500);
    } else {
        isFormReadingMode = false;
        speak("Form completed. Say 'submit' to submit or 'review' to check your information.");
    }
}

function reviewFormInformation() {
    const fields = currentFormType === 'signup' ? signupFields : signinFields;
    let reviewText = "Here's your information: ";

    fields.forEach(field => {
        const value = document.getElementById(field.id).value;
        const fieldName = field.id.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
        reviewText += `${fieldName}: ${field.isPassword ? "••••••" : value}. `;
    });

    speak(reviewText + "Say 'submit' to proceed or 'edit' to make changes.");
}

function submitCurrentForm() {
    if (currentFormType === 'signup') {
        if (validateSignupForm()) {
            document.getElementById("signupForm").submit();
            speak("Submitting registration form.");
        }
    } else {
        document.getElementById("signinForm").submit();
        speak("Submitting login form.");
    }
}

function validateSignupForm() {
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const contactNumber = document.getElementById("contactNumber").value.trim();
    const password = document.getElementById("newpassword").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();

    if (!/^[A-Za-z]{2,}$/.test(firstName)) {
        speak("First name must be at least 2 characters and contain only letters.");
        return false;
    }

    if (!/^[A-Za-z]{2,}$/.test(lastName)) {
        speak("Last name must be at least 2 characters and contain only letters.");
        return false;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        speak("Please enter a valid email address.");
        return false;
    }

    if (!/^\d{10}$/.test(contactNumber)) {
        speak("Contact number must be exactly 10 digits.");
        return false;
    }

    if (password.length < 6) {
        speak("Password must be at least 6 characters long.");
        return false;
    }

    if (password !== confirmPassword) {
        speak("Passwords do not match. Please try again.");
        return false;
    }

    return true;
}

function speak(text) {
    if ('speechSynthesis' in window) {
        // Cancel any ongoing speech
        window.speechSynthesis.cancel();

        const utterance = new SpeechSynthesisUtterance(text);
        utterance.rate = 0.9;
        utterance.onend = function() {
            console.log("Finished speaking");
        };
        window.speechSynthesis.speak(utterance);
    } else {
        console.log("Text-to-speech not supported");
    }
}

// Recognition event handlers
recognition.onresult = (event) => {
    const transcript = event.results[0][0].transcript;
    processVoiceCommand(transcript);
};

recognition.onerror = (event) => {
    console.error("Speech recognition error", event.error);
    voiceControl.classList.remove("listening");
    isListening = false;
    if (event.error !== 'no-speech') {
        speak("Sorry, I didn't catch that. Please try again.");
    }
};

recognition.onend = () => {
    if (isListening) {
        recognition.start();
    }
};

// Initial instructions
setTimeout(() => {
    speak("Welcome to our accessible form system. Say 'sign up' to register or 'sign in' to login.");
}, 1000);