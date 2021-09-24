let url = new URL(window.location.href)

// checking valid token (with userrole) and loading data)
window.addEventListener("DOMContentLoaded", validateClient)



window.addEventListener("DOMContentLoaded", getBalance)
window.addEventListener("DOMContentLoaded", getPortfolio)
window.addEventListener("DOMContentLoaded", getPercentageIncrease)


const balance = document.querySelector("#balanceValue")
const portfolioValue = document.querySelector("#portfolioValue")
const percentage = document.querySelector("#percentage")
const nav1 = document.querySelector("#FS1")
const nav2 = document.querySelector("#FS2")
const nav3 = document.querySelector("#FS3")
const currencyFormat = {style: "currency", currency: "USD", minimumFractionDigits: 2}
const percentageFormat = {style: "percentage", minimumFractionDigits: 2}

nav1.addEventListener("click", function() {
    document.location.href ="account.html";
})

nav2.addEventListener("click", function() {
    document.location.href ="portfolio.html";
})

nav3.addEventListener("click", function() {
    document.location.href ="marketplace.html";
})



// FUNCTIONS:

function getBalance(){
    fetch(`${url.origin}/getBalance`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    })
        .then(res => res.text())
         // .then(it => parseFloat("it").toFixed(2))
        .then (it => {
            balance.innerHTML = parseFloat(it).toLocaleString('en-US', currencyFormat)
        })
}


function getPortfolio(){
    fetch(`${url.origin}/portfolio/totalPortfolioValue`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    })
        .then(res => res.text())
        .then(it => {
            portfolioValue.innerHTML = parseFloat(it).toLocaleString('en-US', currencyFormat)
        })
}

function getPercentageIncrease(){
    fetch(`${url.origin}/portfolio/percentageIncrease`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    })
        .then(res => res.text())
        .then(it => {
            if (parseFloat(it) < 0){
                percentage.innerHTML = "⮟" + parseFloat(it).toFixed(2) + "%"
            }
            else if (parseFloat(it) >= 0) {
                percentage.innerHTML = "⮝" + parseFloat(it).toFixed(2) + "%"
            }
        })
}