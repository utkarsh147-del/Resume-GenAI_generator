function goToHome() {
    window.location.href = "index.html"; // Back to Home Page
}
document.getElementById("signupForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Form default submit rokna

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    // Simple validation
    if (!username || !password) {
        message.textContent = "Please fill all fields!";
        return;
    }

    // API call to backend
    fetch("http://localhost:8024/public/signup", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Signup failed");
        }
        return response.text();
    })
    .then(data => {
        message.textContent = "Signup successful! Redirecting to Login...";
        message.style.color = "#27ae60"; // Green for success
        setTimeout(() => window.location.href = "index.html", 2000); // 2 seconds delay
    })
    .catch(error => {
        message.textContent = "User already exists or server error!";
    });
});