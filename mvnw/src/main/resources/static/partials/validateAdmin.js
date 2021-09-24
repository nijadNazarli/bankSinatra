// checking valid token (with userrole) and loading data)
window.addEventListener("DOMContentLoaded", validateAdmin)

// specific validation of jwt and userrole
function validateAdmin(){
    fetch(`${url.origin}/validateAdmin`, {
        method: 'POST',
        body: `${localStorage.getItem('token')}`
    })
        .then(res => {
            if (res.status === 200) {
                console.log("Admin validated")
            } else if (res.status === 403) {
                console.log("You are logged in as a client, please log in using an admin account to view this page.")
                window.location.replace("/dashboard.html")
            } else {
                console.log("Login validation failed, please login again.")
                window.location.replace("/index.html")
            }
        })
}

