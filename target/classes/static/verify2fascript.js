function goToLogin() {
    window.location.href = "login.html"; // Back to Login Page
}

function goToDashboard() {
    window.location.href = "dashboard.html"; // Next to Dashboard Page
}

let token = null;

// Fetch token on page load (assuming from localStorage or previous page)
window.addEventListener("load", function() {
    token = localStorage.getItem("token"); // Assuming token stored in localStorage
    if (!token) {
        document.getElementById("message").textContent = "No authentication token found!";
    }
});

document.getElementById("verify2FAForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Form default submit rokna

    const totpCode = document.getElementById("totpCode").value;
    const message = document.getElementById("message");

    // Simple validation
    if (!totpCode || totpCode.length !== 6) {
        message.textContent = "Please enter a valid 6-digit code!";
        return;
    }

    if (!token) {
        message.textContent = "Authentication required!";
        return;
    }

    // API call to /user/verify-2fa
    fetch("http://localhost:8024/user/verify-2fa", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token,
        },
        body: JSON.stringify({ totpCode }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("2FA verification failed");
        }
        return response.text();
    })
    .then(data => {
        message.textContent = "2FA verified successfully!";
        message.style.color = "#27ae60"; // Green for success
        setTimeout(() => goToDashboard(), 2000); // 2 seconds delay before redirect
    })
    .catch(error => {
        message.textContent = "Invalid 2FA code!";
    });
});