/*
 *  @Author elbertvw
 *  This file contains various JS functions necessary for the functioning of the admin dashboard (/admin.html).
 *  Validation is handled by /partials/validateAdmin.js !
 */

// PAGE SETUP
let url = new URL(window.location.href);

// --------------------

// LOGOUT
const logout = document.querySelector("#logout")
// CHANGING BANK FEE
const btnBankFee = document.querySelector("#bankFee")
const feeForm = document.querySelector("#feeForm")
const btnCloseOverlay = document.querySelector("#close-overlay-btn")
const overlay = document.querySelector("#overlay")
// FETCHING USER
let user = null
const findUserForm = document.querySelector("#findUserForm")
const userTable = $("#userTable")
const btnToggleBlock = document.querySelector("#toggleBlock")
$(btnToggleBlock).hide()
// CLIENT PORTFOLIO & ACCOUNT BALANCE MANAGEMENT
const portfolioData = $("#portfolioData")
const assetTable = $("#assetTable")
let assets = {}
const btnSubmitChanges = document.querySelector("#submitChanges")

// --------------------

// CONFIRMATION PROMPT
function confirmationPrompt(action) {
    return confirm(`Are you sure you want to ${action}?`)
}

// LOGOUT
logout.addEventListener("click", function() {
    if (confirmationPrompt("logout")) {
        window.localStorage.clear();
        window.location.replace("/index.html");
    }
})

// CHANGE BANK FEE
$(document).ready(function (){
    $(btnBankFee).click(function (){
        getCurrentFee()
        $(overlay).show()
    });

    $(btnCloseOverlay).click(function (){
        $(overlay).hide();
        $(feeForm).trigger("reset")
    })
});

feeForm.addEventListener('submit', function (e) {
    if (confirmationPrompt("change the bank costs")) {
        e.preventDefault()
        updateFee()
    }
})

function updateFee(){
    let payload =
        {token: `${localStorage.getItem('token')}`,
            fee: `${document.querySelector("#fee-input").value}`}
    console.log(payload);
    fetch(`${url.origin}/admin/updateFee`,
        {
            method: 'PUT',
            header: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if (res.status === 200) {
                console.log("Bank costs updated.")
            } else {
                console.log("An error was encountered while updating the bank fee.")
            }
        })
}

function getCurrentFee(){
    fetch(`${url.origin}/admin/getBankFee`,
        {
            method: 'GET',
            headers: {"Authorization": localStorage.getItem('token')}
        })
        .then(res => {
            if (res.status === 200) {
                res.json().then(it => {
                    console.log(it)
                    document.getElementById("fee-input").placeholder = `Current fee: ${it}`
                })
            }
        })

}

// LOAD USER
findUserForm.addEventListener('submit', function (e) {
    e.preventDefault()
    user = document.querySelector("#email-input").value
    loadUser(user)
})

function loadUser(user){
    if ($('#userTable tr').length !== 0) {
        userTable.empty()
    }
    if ($('#assetTable tr').length !== 0) {
        assetTable.empty()
    }

    fetch(`${url.origin}/admin/getUserData?email=${user}`,
        {
            method: 'GET',
            headers: { "Authorization": localStorage.getItem('token') }
        })
        .then(res => {
            if (res.status === 200) {
                res.json().then(it => {
                    fillUserTable(it)
                    $(btnToggleBlock).show()
                    if (it["dateOfBirth"] != null) { // only show assets when loading a client; admins don't have assets
                        showPortfolio(user)
                    } else {
                        $(portfolioData).hide()
                    }
                })
            } else {
                console.log("An error was encountered while fetching user data.")
            }
        })
}

function fillUserTable(it) {
    for (const key in it) {          // loop through all properties of the imported user json
        if (it[key] !== null && key !== "account") {
            if (key === "address") { // "address" contains properties of its own, so needs to be looped through separately
                let obj = it["address"];
                for (const key in obj) {
                    $(userTable).append(`<tr><td>${key}</td><td>${obj[key]}</td></tr>`)
                }
            } else {
                $(userTable).append(`<tr><td>${key}</td><td>${it[key]}</td></tr>`)
            }
        }
    }
}

// BLOCK/UNBLOCK USER
btnToggleBlock.addEventListener("click", function() {
    if (confirmationPrompt("change this user's block status")) {
        blockUser(user)
    }
})

function blockUser(user) {
    fetch(`${url.origin}/admin/toggleBlock?email=${user}`,
        {
            method: 'POST',
            headers: { "Authorization": localStorage.getItem('token') }
        }).then(res => {
        if (res.status === 200) {
            loadUser(user) // reload user user after block is toggled to update info
            console.log("Block status changed.")
        } else {
            res.json().then(it => {
                console.log(it.message)
            })
        }
    })
}

// ADD OR REMOVE ASSETS
btnSubmitChanges.addEventListener("click", function() {
    if (confirmationPrompt("modify this user's assets")) {
        applyAssetChanges()
    }
})

async function showPortfolio(user) { // this function is only called in loadUser above when user is a client (admin users have no portfolio)
    await getAssets(user)
    $(portfolioData).show()
}

async function getAssets(user) {
    await fetch(`${url.origin}/admin/getAssets?email=${user}`, {
        method: 'GET',
        headers: {"Authorization": `${localStorage.getItem('token')}`},
    }).then(res => {
        if (res.status === 200) {
            console.log("Assets fetched succesfully.")
        } else {
            console.log("An error was encountered while fetching assets.")
        }
        return res.json().then(it => {
            assets = it
            fillAssetTable()
        })
    })
}

function fillAssetTable() {
    for (const key in assets) {
        const tr = document.createElement("tr")
        const td1 = document.createElement("td")
        td1.innerText = key;
        const td2 = document.createElement("td")
        td2.innerText = parseFloat(assets[key]).toFixed(2)
        const td3 = document.createElement("input")
        td3.type = "number"
        td3.step = "0.01"
        td3.value = "0.00"
        td3.id = `${key}input`
        tr.append(td1, td2, td3)
        if (key === 'USD') { // move USD to top of table for convenience, as it is somewhat distinct from other assets
            $(assetTable).prepend(tr)
        } else {
            $(assetTable).append(tr)
        }
    }
    $(assetTable).prepend("<tr><th>Symbol</th> <th>Amount owned</th> <th>Add/subtract amount</th></tr>"); // place header
}

async function applyAssetChanges() {
    let changes = {}
    for (const key in assets) {
        changes[key] = document.querySelector(`#${key}input`).value
    }

    console.log(changes)
    fetch(`${url.origin}/admin/updateAssets?email=${user}`,
        {
            method: 'PUT',
            headers: {  "Content-Type": "application/json",
                        "Authorization": `${localStorage.getItem('token')}`},
            body: JSON.stringify(changes)
        })
        .then(res => {
            if (res.status === 200) {
                console.log("Assets updated.")
            } else {
                console.log("An error was encountered while updating assets.")
            }
        })
    loadUser(user) // Wrap up by reloading user to reflect changes made.
}
