// checking valid token (with userrole) and loading data)
document.documentElement.classList.remove('no-js')
window.addEventListener("DOMContentLoaded", validateClient)

// specific validation of jwt and userrole
function validateClient(){
    fetch('/validateClient', {
        method: 'POST',
        body: `${localStorage.getItem('token')}`
    })
        .then(res => {
            if (res.status === 200) {
                console.log("User validated.")
            } else {
                console.log("Login validation failed, please login again.")
                window.location.replace("/index.html");
            }
        })
}