function goToHome() {
    window.location.href = "index.html"; // Back to Home Page
}

function enable2FA() {
    window.location.href = "enable2fa.html"; // 2FA enabling page
}

function goToDashboard() {
    window.location.href = "dashboard.html"; // Dashboard page
}

let token = null;

document.getElementById("loginForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Form default submit rokna

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");
    const postLoginOptions = document.getElementById("postLoginOptions");
    const enable2FAOption = document.getElementById("enable2FAOption");

    // Hide options initially
    postLoginOptions.style.display = "none";
    message.textContent = "";

    // Simple validation
    if (!username || !password) {
        message.textContent = "Please fill all fields!";
        return;
    }

    // API call to /user/login
    fetch("http://localhost:8024/public/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Login failed");
        }
        return response.json(); // Parse JSON response
    })
    .then(data => {
        token = data.token; // Store JWT token
        localStorage.setItem("token", token); // Save token for future pages
		console.log("stat "+data.status);
        if (data.status === "2FA_REQUIRED") {
            // If 2FA is required, redirect to verify 2FA page
            window.location.href = "enable2fa.html";
        } else {
			
            // Login successful, offer to enable 2FA
            postLoginOptions.style.display = "block";
            message.textContent = "Login successful!";
            message.style.color = "#27ae60"; // Green for success
            enable2FAOption.style.display = "block"; // Show enable 2FA option
        }
    })
    .catch(error => {
        message.textContent = "Invalid credentials!";
    });
});