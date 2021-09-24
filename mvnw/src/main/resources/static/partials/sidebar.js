window.addEventListener("DOMContentLoaded", setupSidebar)
let firstName

async function setupSidebar(){
    await getFirstName().then(fillName)
}

async function getFirstName(){
    await fetch (`${url.origin}/getNameUser`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    }).then(res => {
        return res.text();
    })
        .then(data => {
            return firstName = data;
        })
}

function fillName(){
    let boldName = firstName.bold()
    $("#intro").append(boldName);
}