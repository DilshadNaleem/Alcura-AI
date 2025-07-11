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
    { id: "firstName", prompt: "First name. Please say your first name letter by letter." },
    { id: "lastName", prompt: "Last name. Please say your last name letter by letter." },
    { id: "email", prompt: "Email address. Please say your email letter by letter." },
    { id: "newpassword", prompt: "Password. Please create and say your password letter by letter.", isPassword: true },
    { id: "confirmPassword", prompt: "Confirm password. Please say your password again letter by letter.", isPassword: true },
    { id: "contactNumber", prompt: "Contact number. Please say your phone number digit by digit." },
    { id: "nic", prompt: "NIC number. Please say your national identification number letter by letter." }
];

const signinFields = [
    { id: "signinEmail", prompt: "Email. Please say your registered email letter by letter." },
    { id: "password", prompt: "Password. Please say your password letter by letter.", isPassword: true }
];

// State variables
let currentFieldIndex = 0;
let isFormReadingMode = false;
let isListening = false;
let currentFormType = 'signin';
let saidNotFound = false;
let currentInputBuffer = "";
let isTypingMode = false;
let currentEditingField = null;
let hasUserSpoken = false;
let initialWelcomeMessage = null;

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
    saidNotFound = false;
    isFormReadingMode = true;
    currentFieldIndex = 0;
    currentFormType = formType;
    const fields = formType === 'signup' ? signupFields : signinFields;

    function readNextField() {
        if (currentFieldIndex < fields.length) {
            const field = fields[currentFieldIndex];
            const fieldName = field.id.replace(/([A-Z])/g, ' $1').toLowerCase();

            // Enter typing mode for this field
            currentEditingField = field;
            isTypingMode = true;
            currentInputBuffer = document.getElementById(field.id).value || "";

            speak(`Please provide your ${fieldName} letter by letter. Current value is ${field.isPassword ? "••••••" : currentInputBuffer || "empty"}. Say each character clearly (like "d i l s h a d"), or say 'backspace', 'space', 'dot', 'at', or 'done' when finished.`);
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

    // Check if we're in typing mode for a specific field
    if (isTypingMode && currentEditingField) {
        processTypingInput(originalTranscript, currentEditingField);
        return;
    }

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

    // Edit specific field command
    if (transcript.startsWith("edit ")) {
        const fieldToEdit = transcript.substring(5).trim();
        const fields = currentFormType === 'signup' ? signupFields : signinFields;

        for (let i = 0; i < fields.length; i++) {
            const fieldName = fields[i].id.replace(/([A-Z])/g, ' $1').toLowerCase();
            if (fieldToEdit.includes(fieldName)) {
                currentFieldIndex = i;
                currentEditingField = fields[i];
                isTypingMode = true;
                currentInputBuffer = ""; // Clear buffer to start fresh

                // Clear the field
                document.getElementById(fields[i].id).value = "";
                if (fields[i].isPassword) {
                    document.getElementById(fields[i].id).setAttribute('data-real-value', "");
                }

                speak(`Editing ${fieldName}. Please say the new value letter by letter. Say 'done' when finished.`);
                return;
            }
        }
        speak("Field not found. Please try again.");
        return;
    }

    // Form filling
    if (isFormReadingMode) {
        const fields = currentFormType === 'signup' ? signupFields : signinFields;
        const currentField = fields[currentFieldIndex];
        currentEditingField = currentField;
        isTypingMode = true;
        currentInputBuffer = document.getElementById(currentField.id).value || "";
        speak(`Please say the letters for ${currentField.id.replace(/([A-Z])/g, ' $1').toLowerCase()} one by one. Current value is ${currentField.isPassword ? "••••••" : currentInputBuffer || "empty"}. Say 'done' when finished.`);
        return;
    }

    // Review command
    if (transcript.includes("review") || transcript.includes("check")) {
        reviewFormInformation();
        return;
    }

    // Submit form
    if (transcript.includes("submit")) {
        submitCurrentForm();
        return;
    }

    // Help command
    if (transcript.includes("help") || transcript.includes("what can i say")) {
        speak("You can say: 'Sign in', 'Sign up', 'Submit', 'Review', 'Edit [fieldname]', or specific field names. When typing, say letters one by one (like 'd i l s h a d'), or use commands like 'backspace', 'space', 'dot', 'at', or 'done'.");
        return;
    }

    // Default response
    if (!saidNotFound) {
        speak("Command not recognized. Please try again.");
        saidNotFound = true;
    }
}

function processTypingInput(transcript, field) {
    transcript = transcript.toLowerCase().trim();
    console.log("Processing typing input:", transcript);

    // Special commands
    if (transcript === "backspace") {
        currentInputBuffer = currentInputBuffer.slice(0, -1);
        updateFieldDisplay(field);
        speak("Backspace");
        return;
    }

    if (transcript === "space") {
        currentInputBuffer += " ";
        updateFieldDisplay(field);
        speak("Space");
        return;
    }

    if (transcript === "dot" || transcript === "period") {
        currentInputBuffer += ".";
        updateFieldDisplay(field);
        speak("Dot");
        return;
    }

    if (transcript === "at" || transcript === "at symbol") {
        currentInputBuffer += "@";
        updateFieldDisplay(field);
        speak("At symbol");
        return;
    }

    if (transcript === "underscore") {
        currentInputBuffer += "_";
        updateFieldDisplay(field);
        speak("Underscore");
        return;
    }

    if (transcript === "dash" || transcript === "hyphen") {
        currentInputBuffer += "-";
        updateFieldDisplay(field);
        speak("Dash");
        return;
    }

    if (transcript === "done") {
        finishTyping(field);
        return;
    }

    // Process individual letters when spoken with spaces (like "d i l s h a d")
    if (transcript.includes(" ")) {
        const letters = transcript.split(" ")
                               .filter(l => l.length === 1 && /[a-z0-9]/.test(l))
                               .join("");
        if (letters.length > 0) {
            currentInputBuffer += letters;
            updateFieldDisplay(field);
            speak("Added " + letters.split("").join(" "));
            return;
        }
    }

    // Process single letters or numbers
    if ((transcript.length === 1 && /[a-z0-9]/.test(transcript))) {
        currentInputBuffer += transcript;
        updateFieldDisplay(field);
        speak(transcript);
        return;
    }

    // Process number words (zero through nine)
    const numberWords = {
        "zero": "0", "one": "1", "two": "2", "three": "3", "four": "4",
        "five": "5", "six": "6", "seven": "7", "eight": "8", "nine": "9"
    };

    if (numberWords[transcript]) {
        currentInputBuffer += numberWords[transcript];
        updateFieldDisplay(field);
        speak(numberWords[transcript]);
        return;
    }

    // If we get here, it's an unrecognized command
    speak("Please say a single letter, number, or a special command like 'space', 'dot', or 'at'. For names, say letters with spaces between them like 'd i l s h a d'.");
}

function updateFieldDisplay(field) {
    const fieldElement = document.getElementById(field.id);

    // For password fields, show asterisks but store the actual value
    if (field.isPassword) {
        fieldElement.value = "*".repeat(currentInputBuffer.length);
        fieldElement.setAttribute('data-real-value', currentInputBuffer);
    } else {
        fieldElement.value = currentInputBuffer;
    }
}

function finishTyping(field) {
    const fieldElement = document.getElementById(field.id);

    // For email fields, ensure proper lowercase formatting
    if (field.id.includes("email")) {
        // Split email into parts and lowercase the domain
        const parts = currentInputBuffer.split('@');
        if (parts.length === 2) {
            currentInputBuffer = parts[0] + '@' + parts[1].toLowerCase();
        } else {
            currentInputBuffer = currentInputBuffer.toLowerCase();
        }
    }

    // For password fields, get the real value from data attribute
    if (field.isPassword) {
        fieldElement.setAttribute('data-real-value', currentInputBuffer);
        fieldElement.value = "*".repeat(currentInputBuffer.length);
    } else {
        fieldElement.value = currentInputBuffer;
    }

    const fieldName = field.id.replace(/([A-Z])/g, ' $1').toLowerCase();
    speak(`${fieldName} set to ${field.isPassword ? "••••••" : currentInputBuffer || "empty"}`);

    // Reset typing state
    isTypingMode = false;
    currentInputBuffer = "";
    currentEditingField = null;

    // Move to next field if in form reading mode
    if (isFormReadingMode) {
        moveToNextField();
    }
}

function moveToNextField() {
    saidNotFound = false;
    const fields = currentFormType === 'signup' ? signupFields : signinFields;
    currentFieldIndex++;

    if (currentFieldIndex < fields.length) {
        setTimeout(() => {
            const nextField = fields[currentFieldIndex];
            currentEditingField = nextField;
            isTypingMode = true;
            currentInputBuffer = document.getElementById(nextField.id).value || "";

            const fieldName = nextField.id.replace(/([A-Z])/g, ' $1').toLowerCase();
            speak(`Now please provide your ${fieldName} letter by letter. Current value is ${nextField.isPassword ? "••••••" : currentInputBuffer || "empty"}. Say each character clearly (like "d i l s h a d"), or say 'done' when finished.`);
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
        let value = document.getElementById(field.id).value;
        if (field.isPassword) {
            value = document.getElementById(field.id).getAttribute('data-real-value') || value;
        }
        const fieldName = field.id.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
        reviewText += `${fieldName}: ${field.isPassword ? "••••••" : value || "empty"}. `;
    });

    speak(reviewText + "Say 'submit' to proceed or 'edit [fieldname]' to make changes.");
}

function submitCurrentForm() {
    if (currentFormType === 'signup') {
        // Ensure all password fields have their real values before submission
        document.getElementById("newpassword").value =
            document.getElementById("newpassword").getAttribute('data-real-value') ||
            document.getElementById("newpassword").value;
        document.getElementById("confirmPassword").value =
            document.getElementById("confirmPassword").getAttribute('data-real-value') ||
            document.getElementById("confirmPassword").value;

        if (validateSignupForm()) {
            speak("Submitting registration form.");
            document.getElementById("signupForm").submit();
        }
    } else {
        // Ensure password field has its real value before submission
        document.getElementById("password").value =
            document.getElementById("password").getAttribute('data-real-value') ||
            document.getElementById("password").value;

        speak("Submitting login form.");
        document.getElementById("signinForm").submit();
    }
}

function validateSignupForm() {
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const contactNumber = document.getElementById("contactNumber").value.trim();
    const password = document.getElementById("newpassword").getAttribute('data-real-value') ||
                     document.getElementById("newpassword").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").getAttribute('data-real-value') ||
                           document.getElementById("confirmPassword").value.trim();

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

     // NIC validation (example: old or new Sri Lankan NIC formats)
        if (!/^(\d{9}[vVxX]|\d{12})$/.test(nic)) {
            alert('Please enter a valid NIC (e.g. 123456789V or 200012345678).');
            return false;
        }

    return true;
}

function speak(text) {
    if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.rate = 0.9;
        window.speechSynthesis.speak(utterance);
    } else {
        console.log("Text-to-speech not supported");
    }
}

function playInitialInstructions() {
    if ('speechSynthesis' in window) {
        const utterance = new SpeechSynthesisUtterance(
            "Welcome to our accessible form system. Say 'sign up' to register or 'sign in' to login. When filling fields, say letters one by one (like 's a m p l e') or use special commands."
        );
        utterance.rate = 0.9;

        // Store the utterance so we can cancel it later
        initialWelcomeMessage = utterance;

        window.speechSynthesis.speak(utterance);
    }
}

// Recognition event handlers
recognition.onresult = (event) => {
    hasUserSpoken = true;
    if (initialWelcomeMessage) {
        window.speechSynthesis.cancel();
    }
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

recognition.onstart = () => {
    hasUserSpoken = false;
};

// Initial instructions
setTimeout(playInitialInstructions, 1000);