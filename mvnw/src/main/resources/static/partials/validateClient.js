// checking valid token (with userrole) and loading data)
document.documentElement.classList.remove('no-js')
window.addEventListener("DOMContentLoaded", validateClient)

// specific validation of jwt and userrole
function validateClient(){
    fetch(`http://localhost:8080/validateClient`, {
        method: 'POST',
        body: `${localStorage.getItem('token')}`
    })
        .then(res => {
            if (res.status === 200) {
                console.log("no problemo")
            } else {
                console.log("your token is bad and you should feel bad")
                window.location.replace("/index.html");
            }
        })
}