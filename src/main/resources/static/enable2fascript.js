function goToLogin() {
    window.location.href = "login.html"; // Back to Login Page
}

function goToVerify2FA() {
    window.location.href = "verify2fa.html"; // Next to Verify 2FA Page
}

let token = null;

// Fetch QR code on page load
window.addEventListener("load", function() {
    const message = document.getElementById("message");
    const qrCode = document.getElementById("qrCode");

    // Get token from login page (assuming passed via URL or session)
    token = localStorage.getItem("token"); // Assuming token stored in localStorage
    
         console.log("oenablken   " +token);
    if (!token) {
        message.textContent = "No authentication token found!";
        return;
    }

    // API call to /user/enable-2fa
    fetch("/user/enable-2fa", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to fetch QR code");
        }
        return response.blob(); // QR code as image
    })
    .then(blob => {
        const url = URL.createObjectURL(blob);
        qrCode.innerHTML = `<img src="${url}" alt="QR Code">`;
    })
    .catch(error => {
        message.textContent = "Failed to load QR code. Try again!";
    });
});
