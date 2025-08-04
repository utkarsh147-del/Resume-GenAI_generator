function goToHome() {
    window.location.href = "index.html";
}

function enable2FA() {
    window.location.href = "enable2fa.html";
}

function goToDashboard() {
    window.location.href = "dashboard.html";
}

function goToLogin() {
    window.location.href = "login.html";
}

function goToVerify2FA() {
    window.location.href = "verify2fa.html";
}

function generateResume() {
    const token = localStorage.getItem("token");
    const message = document.getElementById("message");
    const skills = document.getElementById("skills").value;
    const experience = document.getElementById("experience").value;
    const education = document.getElementById("education").value;
      const projects = document.getElementById("projects").value;
        const description = document.getElementById("description").value;
    

    if (!token) {
        message.textContent = "Please login first!";
        return;
    }

    

    const params = new URLSearchParams({
        skills: skills,
        experience: experience,
        education: education,
        projects:projects,
        description:description,
        
        token: token
    }).toString();
    
        console.log(projects);
        console.log(description);
    window.location.href = `resumegenerator.html?${params}`;
}

function viewSavedResumes() {
    const token = localStorage.getItem("token");
    const message = document.getElementById("message");

    if (!token) {
        message.textContent = "Please login first!";
        return;
    }

    window.location.href = `saveresume.html?token=${token}`;
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = "index.html";
}

window.addEventListener("load", function() {
    const token = localStorage.getItem("token");
    const welcomeMessage = document.getElementById("welcomeMessage");
    const message = document.getElementById("message");

    if (!token && window.location.pathname.includes("dashboard.html")) {
        message.textContent = "Please login first!";
        return;
    }

    if (window.location.pathname.includes("dashboard.html")) {
        fetch("/user/profile", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
            },
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch user data");
            }
            return response.json();
        })
        .then(data => {
            welcomeMessage.textContent = `Welcome, ${data.username}!`;
        })
        .catch(error => {
            message.textContent = "Failed to load user data!";
        });
    }

    if (window.location.pathname.includes("resumebuilder.html")) {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get("token");
        const skills = urlParams.get("skills");
        const experience = urlParams.get("experience");
        const projects = urlParams.get("projects");
        const description = urlParams.get("descrption");
        const education = urlParams.get("education");
        const resumeContent = document.getElementById("resumeContent");
        const message = document.getElementById("message");

        if (!token) {
            message.textContent = "Please login first!";
            return;
        }

        fetch("/resume/generate-resume", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                skills: skills,
                experience: experience,
                education: education,
                projects:projects,
                description:description
                
            }),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to generate resume");
            }
            return response.text();
        })
        .then(resume => {
            const formattedResume = formatResume(resume);
            resumeContent.innerHTML = formattedResume;
        })
        .catch(error => {
            message.textContent = `Failed to generate resume: ${error.message}`;
        });
    }

    if (window.location.pathname.includes("saveresume.html")) {
        const token = new URLSearchParams(window.location.search).get("token");
        const resumeList = document.getElementById("resumeList");
        const message = document.getElementById("message");

        if (!token) {
            message.textContent = "Please login first!";
            return;
        }

        fetch("/resume/all", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
            },
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch saved resumes");
            }
            return response.json();
        })
        .then(data => {
            let html = "<ul>";
            data.forEach(resume => {
                html += `<li onclick="editResume('${resume.name}')">${resume.name}</li>`;
            });
            html += "</ul>";
            resumeList.innerHTML = html;
        })
        .catch(error => {
            message.textContent = `Failed to load saved resumes: ${error.message}`;
        });
    }

    if (window.location.pathname.includes("editresume.html")) {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get("token");
        const resumeName = urlParams.get("resumeName");
        const resumeContent = document.getElementById("resumeContent");
        const message = document.getElementById("message");

        if (!token || !resumeName) {
            message.textContent = "Invalid request!";
            return;
        }

        fetch("/resume/get?name=" + encodeURIComponent(resumeName), {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
            },
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch resume");
            }
            return response.text();
        })
        .then(content => {
            resumeContent.innerHTML = formatResume(content);
        })
        .catch(error => {
            message.textContent = `Failed to load resume: ${error.message}`;
        });
    }
});

function formatResume(content) {
    if (!content || !content.trim()) return "<p>No valid resume content generated.</p>";
    let formatted = "<pre style='margin: 0; padding: 0;'>";
    const lines = content.split("\n");
    let indentLevel = 0;
    let currentSection = "";

    lines.forEach(line => {
        line = line.trim();
        if (!line) return;

        if (line.toLowerCase().match(/^(skills|experience|education|projects|description):/)) {
            currentSection = line.split(":")[0].toLowerCase();
            formatted += "</pre><h3>" + line.replace(":", ":") + "</h3><pre>";
            indentLevel = 1;
        } else if (indentLevel > 0 && currentSection) {
            formatted += "    - " + line + "\n";
        } else {
            formatted += line + "\n";
        }
    });

    formatted += "</pre>";
    return formatted;
}

function saveResume() {
    const token = new URLSearchParams(window.location.search).get("token");
    const resumeContent = document.getElementById("resumeContent").innerText;
    const message = document.getElementById("message");

    if (!token) {
        message.textContent = "Please login first!";
        return;
    }

    if (!resumeContent.trim()) {
        message.textContent = "Please generate or edit resume first!";
        return;
    }

    let resumeName = prompt("Enter a name for your resume:");
    if (resumeName) {
        fetch("/resume/save", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                name: resumeName,
                content: resumeContent
            }),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to save resume");
            }
            return response.text();
        })
        .then(data => {
            message.textContent = "Resume saved successfully!";
            message.style.color = "#27ae60";
        })
        .catch(error => {
            message.textContent = `Failed to save resume: ${error.message}`;
        });
    } else {
        message.textContent = "Save cancelled!";
    }
}

function saveEditedResume() {
    const token = new URLSearchParams(window.location.search).get("token");
    const resumeName = new URLSearchParams(window.location.search).get("resumeName");
    const resumeContent = document.getElementById("resumeContent").innerText;
    const message = document.getElementById("message");

    if (!token || !resumeName) {
        message.textContent = "Invalid request!";
        return;
    }

    if (!resumeContent.trim()) {
        message.textContent = "Please edit resume first!";
        return;
    }

    fetch("/resume/update", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            name: resumeName,
            content: resumeContent
        }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to update resume");
        }
        return response.text();
    })
    .then(data => {
        message.textContent = "Resume updated successfully!";
        message.style.color = "#27ae60";
    })
    .catch(error => {
        message.textContent = `Failed to update resume: ${error.message}`;
    });
}

function editResume(resumeName) {
    const token = new URLSearchParams(window.location.search).get("token");
    if (token) {
        window.location.href = `editresume.html?token=${token}&resumeName=${encodeURIComponent(resumeName)}`;
    }
}

function goToDashboard() {
    window.location.href = "dashboard.html";
}

function goToSavedResumes() {
    window.location.href = "saveresume.html?token=" + new URLSearchParams(window.location.search).get("token");
}

document.getElementById("loginForm")?.addEventListener("submit", function(event) {
    // Login form logic (kept for other pages)
});

document.getElementById("verify2FAForm")?.addEventListener("submit", function(event) {
    // Verify 2FA form logic (kept for other pages)
});
