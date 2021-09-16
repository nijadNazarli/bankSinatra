// Loginsectie
const btnRegister = document.querySelector("#btnRegister")
const overlay = document.querySelector("#overlay")
const loginForm = document.querySelector("#loginForm")
const registerForm = document.querySelector("#registerFields")
const closeOverlayBtn = document.querySelector("#close-overlay-btn")
const adminBtn = document.querySelector("#admin")
const clientBtn = document.querySelector("#client")

let url = new URL(window.location.href)
const userRegister = `${url.origin}/register`;
const adminRegister = `${url.origin}/admin/register`;
let currentLogin = `${url.origin}/login`;
let currentRegister = `${url.origin}/register`;
let userDashBoardUrl = `${url.origin}/dashboard.html`
let adminDashBoardUrl = `${url.origin}/admin.html`

adminBtn.addEventListener('click', switchToAdmin);
clientBtn.addEventListener('click', switchToClient);
loginForm.addEventListener('submit', function(e) {
    if(loginForm.children.namedItem("email").validity.valid && loginForm.children.namedItem("password").validity.valid) {
        e.preventDefault();
        doLogin();
    }
});
registerForm.addEventListener('submit', function (e) {
    if (checkRegistrationFields()) {
        e.preventDefault();
        doRegister(currentRegister);
    }
})

function doRegister(currentRegister) {
    let payload =
        {email: `${document.querySelector("#email-reg").value}`,
            password: `${ document.querySelector("#password-reg").value}`,
            firstName: `${document.querySelector("#firstName").value}`,
            prefix: `${document.querySelector("#prefix").value}`,
            lastName: `${document.querySelector("#lastName").value}`,
            dateOfBirth: `${document.querySelector("#dob").value}`,
            bsn: parseInt(`${document.querySelector("#bsn").value}`),
            address: {
                city: `${document.querySelector("#city").value}`,
                zipCode: `${document.querySelector("#zipcode").value}`,
                street: `${document.querySelector("#street").value}`,
                houseNumber: `${document.querySelector("#houseNumber").value}`,
                houseNumberExtension: `${document.querySelector("#hNrE").value}`
            }
        }

    let jsonString = JSON.stringify(payload);

    fetch(`${currentRegister}`,
        {
            method: 'POST',
            header: { "Content-Type": "application/json" },
            body: jsonString
        })
        .then(res => {
            if (res.status === 201) {
                console.log(res.text());
                alert("Thank you. Your requested has been received. We will process it accordingly and come back to you as soon as possible.")
            } else if (res.status === 409) {
                alert("Registration failed. User with this email address already exists")
                return;
            } else {
                alert("Registration failed");
                return;
            }
            return res.text();
        })
        .then(it => {
            console.log(it.statusText);
        })
        .catch()
}

function doLogin(){
    let payload =
        {email: `${document.querySelector("#email").value}`,
        password: `${ document.querySelector("#password").value}`}

    let jsonString = JSON.stringify(payload);

    fetch(`${currentLogin}`,
        {
            method: 'POST',
            header: { "Content-Type": "application/json" },
            body: jsonString
        }).then(res => {
            if (res.status === 200) {
                res.json().then(it => {
                    redirectUserAfterLogin(it.userRole);
                    localStorage.setItem('token', it.token);
                    localStorage.setItem('role', it.userRole);
                });
                return;
            } else {
                res.json().then(it => {
                    alert(it.message);
                })
            }
        })
}

function switchToAdmin(){
    currentRegister = adminRegister;
}

function switchToClient(){
    currentRegister = userRegister;
}

function checkRegistrationFields() {
    return registerForm.children.namedItem("email-reg").validity.valid
    && registerForm.children.namedItem("password-reg").validity.valid
    && registerForm.children.namedItem("firstName-reg").validity.valid
    && registerForm.children.namedItem("prefix-reg").validity.valid
    && registerForm.children.namedItem("lastName-reg").validity.valid
    && registerForm.children.namedItem("bsn-reg").validity.valid
    && registerForm.children.namedItem("city-reg").validity.valid
    && registerForm.children.namedItem("zipcode-reg").validity.valid
    && registerForm.children.namedItem("street-reg").validity.valid
    && registerForm.children.namedItem("housenumber-reg").validity.valid
    && registerForm.children.namedItem("extension-reg").validity.valid
}

function redirectUserAfterLogin(role) {
    if (role === 'client') {
        window.location.replace(userDashBoardUrl)
    } else {
        window.location.replace(adminDashBoardUrl)
    }
}

// jQuery Functions
$(document).ready(function (){
    $(btnRegister).click(function (){
        $(overlay).show();
    });

    $(closeOverlayBtn).click(function (){
        $(overlay).hide();
        $(registerForm).trigger("reset");
    })

});

