const body = document.querySelector("body"),
      sidebar = body.querySelector(".sidebar"),
      toggle = body.querySelector(".toggle"),
      searchBtn = body.querySelector(".search-box"),
      modeSwitch = body.querySelector(".toggle-switch"),
      modeText = body.querySelector(".mode-text");

// Check if dark mode is stored in localStorage and apply it
if(localStorage.getItem("darkMode") === "enabled") {
  body.classList.add("dark");
  modeText.innerText = "Light Mode";
}

toggle.addEventListener("click", () => {
  sidebar.classList.toggle("close");
  body.classList.toggle("sidebar-collapsed");
});

modeSwitch.addEventListener("click", () => {
  body.classList.toggle("dark");

  // Save the dark mode state in localStorage
  if(body.classList.contains("dark")) {
    modeText.innerText = "Light Mode";
    localStorage.setItem("darkMode", "enabled");
  } else {
    modeText.innerText = "Dark Mode";
    localStorage.setItem("darkMode", "disabled");
  }
});
