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
    const basicinfo = document.getElementById("basic_info").value;
    const skills = document.getElementById("skills").value;
    const experience = document.getElementById("experience").value;
    const education = document.getElementById("education").value;
     const projects = document.getElementById("projects").value;
        const description = document.getElementById("description").value;

    if (!token) {
        message.textContent = "Please login first!";
        return;
    }

    if (!skills || !experience || !education) {
        message.textContent = "Please fill all fields!";
        return;
    }

    const params = new URLSearchParams({
		basic_info:basicinfo,
        skills: skills,
        experience: experience,
        education: education,
            projects:projects,
        description:description,
        token: token
    }).toString();
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
function downloadPDF() {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    const resumeContent = document.getElementById("resumeContent").innerText;
    const lines = resumeContent.split("\n");
    let yPos = 10;

    doc.setFontSize(12);
    lines.forEach(line => {
        if (line.trim()) {
            doc.text(line.trim(), 10, yPos);
            yPos += 10;
            if (yPos > 280) {
                doc.addPage();
                yPos = 10;
            }
        }
    });

    doc.save("resume.pdf");
}
window.addEventListener("load", function() {
    const token = localStorage.getItem("token");
    const welcomeMessage = document.getElementById("welcomeMessage");
    const message = document.getElementById("message");

    if (!token && window.location.pathname.includes("dashboard.html")) {
        message.textContent = "Please login first!";
        return;
    }

    

    if (window.location.pathname.includes("resumegenerator.html")) {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get("token");
        const basic_info = urlParams.get("basic_info");
        const skills = urlParams.get("skills");
        const experience = urlParams.get("experience");
         const description = urlParams.get("description");
           const projects = urlParams.get("projects");
        const education = urlParams.get("education");
        const resumeContent = document.getElementById("resumeContent");
        const message = document.getElementById("message");
        console.log(projects);
        console.log(description);

        if (!token) {
            message.textContent = "Please login first!";
            return;
        }
		resumeContent.innerHTML = '<i class="fas fa-spinner loader"></i>';
        fetch("/resume/generate-resume", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
				basicInfo:basic_info,
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
        if (typeof data === "string") {
            if (data === "User not found") {
                message.textContent = data;
                resumeList.innerHTML = "";
            } else if (data === "No Resume saved") {
                resumeList.innerHTML = "<p>" + data + "</p>";
            }
        } else {
            let html = "<ul>";
            data.forEach(resume => {
                html += `<li>
                          
                            <span class="resume-name" onclick="editResume('${encodeURIComponent(resume.resumename)}')">${resume.resumename}</span>
                            <span class="delete-icon" onclick="deleteResume('${encodeURIComponent(resume.resumename)}')"><i class="fas fa-trash"></i></span>
                         </li>`;
            });
            html += "</ul>";
            resumeList.innerHTML = html;
        }
    })
        .catch(error => {
            message.textContent = `Failed to load saved resumes: ${error.message}`;
        });
    }
  //${resume.resumename}
      if (window.location.pathname.includes("editresume.html")) {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get("token");
        const resumeName = decodeURIComponent(urlParams.get("resumeName"));
        const resumeContent = document.getElementById("resumeContent");
        const message = document.getElementById("message");

        if (!token || !resumeName) {
            message.textContent = "Invalid request!";
            return;
        }

        fetch("/resume/get?resumename=" + encodeURIComponent(resumeName), { // Changed to 'resumename'
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
            },
        })
        .then(response => {
        if (!response.ok) {
            throw new Error(`Failed to fetch resume: ${response.status} - ${response.statusText}`);
        }
        return response.text();
    })
    .then(content => {
        resumeContent.innerHTML = formatResume(content); // Use formatResume directly
    })
    .catch(error => {
        message.textContent = `Failed to load resume: ${error.message}`;
        console.error("Error details: ", error);
        });
    }
});

function formatResume(content) {
    if (!content || !content.trim()) return "<p>No valid resume content generated.</p>";
    let formatted = "<pre style='margin: 0; padding: 0; word-break: break-all; overflow-wrap: anywhere;'>";
    const lines = content.split("\n");
    let indentLevel = 0;
    let currentSection = "";

    lines.forEach(line => {
        line = line.trim();
        if (!line) return;

        if (line.toLowerCase().match(/^(skills|experience|education):/)) {
            currentSection = line.split(":")[0].toLowerCase();
            formatted += "</pre><h3>" + line.replace(":", ":") + "</h3><pre>";
            indentLevel = 1;
        } else if (indentLevel > 0 && currentSection) {
            formatted += "    - " + line + "\n";
        } else {
            formatted += line + "\n";
        }
    });

    formatted += "</pre>";
    return formatted;
}

function showSaveDialog() {
    document.getElementById("saveDialog").style.display = "block";
}

function hideSaveDialog() {
    document.getElementById("saveDialog").style.display = "none";
}

function saveResume() {
    const token = new URLSearchParams(window.location.search).get("token");
    const resumeContent = document.getElementById("resumeContent").innerText;
    const resumeName = document.getElementById("resumeName").value;
    const message = document.getElementById("message");

    if (!token) {
        message.textContent = "Please login first!";
        return;
    }

    if (!resumeContent.trim()) {
        message.textContent = "Please generate or edit resume first!";
        return;
    }
    console.log(resumeName);

    if (!resumeName.trim()) {
        message.textContent = "Please enter a resume name!";
        return;
    }

    fetch("/resume/save-resume", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
		
			 content: resumeContent,
            resumename: resumeName,
           
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
        hideSaveDialog();
        document.getElementById("resumeName").value = ""; // Clear input
    })
    .catch(error => {
        message.textContent = `Failed to save resume: ${error.message}`;
    });
}

function saveEditedResume() {
    const token = new URLSearchParams(window.location.search).get("token");
    const resumeName = decodeURIComponent(new URLSearchParams(window.location.search).get("resumeName"));
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
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            resumename: resumeName,
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
        window.location.href = `editresume.html?token=${token}&resumeName=${resumeName}`;
    }
}

function goToDashboard() {
    window.location.href = "dashboard.html";
}

function exportResume() {
    const token = new URLSearchParams(window.location.search).get("token");
    const message = document.getElementById("message");
   const urlParams = new URLSearchParams(window.location.search);
    const resumeName = decodeURIComponent(urlParams.get("resumeName") || "resume");
    const resumeContent = document.getElementById("resumeContent").innerText;

    if (!token) {
        message.textContent = "Please login first!";
        return;
    }

    // Assuming getContent() is a function or using resumeContent.innerText
    const content = document.getElementById("resumeContent").innerText;
    if (!content.trim()) {
        message.textContent = "Please generate a resume first!";
        return;
    }

    // Create a temporary div to hold the content
    const tempDiv = document.createElement("div");
    tempDiv.style.padding = "15px";
    tempDiv.style.maxWidth = "550px";
    tempDiv.style.wordBreak = "break-all";
    tempDiv.style.overflowWrap = "break-word";
    tempDiv.style.whiteSpace = "pre-wrap";
    tempDiv.textContent = content;

    // Convert to PDF
    if (window.location.pathname.includes("editresume.html")) {
    html2pdf().from(tempDiv).set({
        margin: 0.5,
        
        filename: `${resumeName}.pdf`,
        html2canvas: { scale: 2, useCORS: true },
        jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
    }).save().then(() => {
        message.textContent = "Resume downloaded successfully!";
        message.style.color = "#27ae60";
    }).catch(error => {
        message.textContent = `Failed to download resume: ${error.message}`;
    });
    }
    else if (window.location.pathname.includes("resumegenerator.html")) {
html2pdf().from(tempDiv).set({
        margin: 0.5,
        
        filename: 'resume.pdf',
        html2canvas: { scale: 2, useCORS: true },
        jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
    }).save().then(() => {
        message.textContent = "Resume downloaded successfully!";
        message.style.color = "#27ae60";
    }).catch(error => {
        message.textContent = `Failed to download resume: ${error.message}`;
    });
    }
    // Clean up
  
}

function deleteResume(resumeName) {
    const token = new URLSearchParams(window.location.search).get("token");
    const message = document.getElementById("message");

    if (!token) {
        message.textContent = "Please login first!";
        return;
    }

    if (!confirm(`Are you sure you want to delete resume: ${decodeURIComponent(resumeName)}?`)) {
        return;
    }

    fetch(`/resume/delete-resume?resumeName=${encodeURIComponent(resumeName)}`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to delete resume");
        }
        return response.text();
    })
    .then(data => {
        // Remove the resume from the UI instantly
        const resumeList = document.getElementById("resumeList");
        const liToRemove = resumeList.querySelector(`li[data-resumename="${encodeURIComponent(resumeName)}"]`);
        if (liToRemove) {
            liToRemove.remove();
        }

        // Update message
        message.textContent = "Resume deleted successfully!";
        message.style.color = "#27ae60";
        window.location.reload();

        // Check if list is empty after removal
        const remainingItems = resumeList.getElementsByTagName("li");
        if (remainingItems.length === 0) {
            resumeList.innerHTML = "<p>Saved resumes are 0</p>";
        }
    })
    .catch(error => {
        message.textContent = `Failed to delete resume: ${error.message}`;
    });
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
