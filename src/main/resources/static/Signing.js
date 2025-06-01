const signUpButton = document.getElementById("signUp");
const signInButton = document.getElementById("signIn");
const container = document.getElementById('container');
const voiceControl = document.getElementById("voiceControl");

signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});

//Voice
const recognition = new (window.SpeechRecognition ||
    window.webkitSpeechRecognition ||
    window.mozSpeechRecognition ||
    window.msSpeechRecognition) ();

recognition.countinuous = false;
recognition.interimResults = false;
recognition.lang = "en-US";

//Form Validation
document.getElementById("signupForm").addEventListener("submit", function (event)
{
    let isValid = true;

    const firstName = document.getElementById("firstName").value.trim();
    const Lastname = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const contactNumber = document.getElementById("contactNumber").value.trim();
    const password = document.getElementById("newpassword").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();

    if (!/^[A-Za-z]{2,}$/.test(firstName))
    {
        alert("First name must be at least 2 characters & contain only letters.");
        isValid = false;
    }

    if(!/^[A-Za-z]{2,}$/.test(Lastname))
    {
        alert ("Last name must be at least 2 characters & contain only letters.");
        isValid = false;
    }

    if(!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
    {
        alert("Please enter a valid email address.");
        isValid = false;
    }

    if(!/^\d{10}$/.test(contactNumber))
    {
        alert ("Contact number must be 10 digits.");
        isValid = false;
    }

    if(password < 6)
    {
        alert("Passowrd must be at least 6 characters long.");
        isValid = false;
    }

    if(password !== confirmPassword)
    {
        alert("Passwords do not match");
        isValid = false;
    }

    if(!isValid)
    {
        event.preventDefault();
    }
});

// Voice recognition
voiceControl.addEventListener("click", () =>
{
    voiceControl.classList.toggle("listening");
    if(voiceControl.classList.contains("listening"))
    {
        speak ("I'm Listening. Please speak now.");
        recognition.start();
    } else{
        recognition.stop();
    }
});


    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript.toLowerCase();
      processVoiceCommand(transcript);
    };

    recognition.onerror = (event) => {
      console.error("Speech recognition error", event.error);
      voiceControl.classList.remove("listening");
      speak("Sorry, I didn't catch that. Please try again.");
    };

    recognition.onend = () => {
      if (voiceControl.classList.contains("listening")) {
        recognition.start(); // Restart listening if still in listening mode
      }
    };

    function processVoiceCommand(transcript) {
      console.log("Voice command:", transcript);

      // Greetings
      if (transcript.includes("hello") || transcript.includes("hi")) {
        speak("Hello! How can I help you today?");
        return;
      }

      // Navigation commands
      if (transcript.includes("sign in") || transcript.includes("login")) {
        container.classList.remove("right-panel-active");
        speak("Showing sign in form. Please say your email to fill it in.");
        return;
      }

      if (transcript.includes("sign up") || transcript.includes("register")) {
        container.classList.add("right-panel-active");
        speak("Showing sign up form. Please say your details to fill them in.");
        return;
      }

      // Enhanced email detection for both forms
      if (
        transcript.includes("email") ||
        transcript.includes("@") ||
        transcript.includes(" at ")
      ) {
        // Clean up the email by replacing " at " with "@" and removing spaces
        let emailText = transcript
          .toLowerCase()
          .replace(/\s+/g, " ") // Replace multiple spaces with single space
          .replace(/\s*at\s*/g, "@") // Replace " at " with "@"
          .replace(/\s+/g, "") // Remove all remaining spaces
          .replace(/dot/g, ".") // Replace "dot" with "."
          .replace(/underscore/g, "_") // Replace "underscore" with "_"
          .replace(/dash/g, "-"); // Replace "dash" with "-"

        // Extract the email address
        const emailMatch = emailText.match(
          /([a-z0-9._-]+@[a-z0-9._-]+\.[a-z0-9._-]+)/i
        );

        if (emailMatch) {
          const email = emailMatch[0];
          if (container.classList.contains("right-panel-active")) {
            // Sign up form
            document.getElementById("email").value = email;
            speak(`Email set to ${email.split("@").join(" at ")}. Now please say your password.`);
          } else {
            // Sign in form
            document.getElementById("signinEmail").value = email;
            speak(`Email set to ${email.split("@").join(" at ")}. Now please say your password.`);
          }
        } else {
          speak("I couldn't detect a valid email address. Please try again.");
        }
        return;
      }

      // Fill password in sign in form
      if (
        (transcript.includes("password") || transcript.includes("pass")) &&
        container.classList.contains("right-panel-active") === false
      ) {
        const password = transcript.replace(/password|pass|is|my/gi, "").trim();
        if (password.length > 0) {
          document.getElementById("signinPassword").value = password;
          speak("I've filled in your password. Say 'submit' to log in.");
        }
        return;
      }

      // Submit form
      if (transcript.includes("submit")) {
        if (container.classList.contains("right-panel-active")) {
          document.getElementById("signupForm").submit();
        } else {
          document.getElementById("signinForm").submit();
        }
        speak("Submitting the form.");
        return;
      }

      // Fill sign up form fields
      if (container.classList.contains("right-panel-active")) {
        // First name
        if (transcript.includes("first name") || transcript.includes("my name is")) {
          const nameMatch = transcript.match(/(?:first name is|my name is) ([a-zA-Z]{2,})/i);
          if (nameMatch) {
            document.getElementById("firstName").value = nameMatch[1];
            speak(`First name set to ${nameMatch[1]}. Now please say your last name.`);
          }
          return;
        }

        // Last name
        if (transcript.includes("last name")) {
          const nameMatch = transcript.match(/last name is ([a-zA-Z]{2,})/i);
          if (nameMatch) {
            document.getElementById("lastName").value = nameMatch[1];
            speak(`Last name set to ${nameMatch[1]}. Now please say your email.`);
          }
          return;
        }

        // Password
        if (transcript.includes("password") || transcript.includes("pass")) {
          const password = transcript.replace(/password|pass|is|my/gi, "").trim();
          if (password.length > 0) {
            document.getElementById("password").value = password;
            document.getElementById("confirmPassword").value = password;
            speak("Password set. Now please say your contact number.");
          }
          return;
        }

        // Contact number
        if (
          transcript.includes("contact") ||
          transcript.includes("phone") ||
          transcript.includes("number")
        ) {
          const numberMatch = transcript.match(/(\d{10})/);
          if (numberMatch) {
            document.getElementById("contactNumber").value = numberMatch[0];
            speak(`Contact number set to ${numberMatch[0]}. Now please say your NIC number.`);
          }
          return;
        }

        // NIC
        if (transcript.includes("nic") || transcript.includes("national id")) {
          const nicMatch = transcript.match(/([a-zA-Z0-9]{10,12})/i);
          if (nicMatch) {
            document.getElementById("nic").value = nicMatch[0];
            speak("NIC set to " + nicMatch[0] + ". Say 'submit' to complete registration.");
          }
          return;
        }
      }

      // Default response
      speak("I didn't understand that command. Please try again.");
    }

    function speak(text) {
      const utterance = new SpeechSynthesisUtterance(text);
      window.speechSynthesis.speak(utterance);
    }