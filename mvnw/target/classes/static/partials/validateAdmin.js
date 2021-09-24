// checking valid token (with userrole) and loading data)
window.addEventListener("DOMContentLoaded", validateAdmin)

// specific validation of jwt and userrole
function validateAdmin(){
    fetch(`http://localhost:8080/validateAdmin`, {
        method: 'POST',
        body: `${localStorage.getItem('token')}`
    })
        .then(res => {
            if (res.status === 200) {
                console.log("no problemo")
            } else {
                console.log("you do not have the power of an admin and you shall not pass")
                window.location.replace("/index.html");
            }
        })
}