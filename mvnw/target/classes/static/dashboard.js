// checking valid token (with userrole) and loading data)
window.addEventListener("DOMContentLoaded", validateClient)
window.addEventListener("DOMContentLoaded", getBalance)
//window.addEventListener("DOMContentLoaded", getPortfolio)


const balance = document.querySelector("#balanceValue")
const portfolioValue = document.querySelector("#portfolioValue")


// FUNCTIONS:

function getBalance(){
    fetch(`http://localhost:8080/getBalance`, {
        method: 'POST',
        body: `${localStorage.getItem('token')}`
    })
        .then(res => res.text())
        .then(it => {
            balance.innerHTML = it
        })
}

// TODO: Json string ophalen en omzetten in html
// const portfolioData = '{"voorbeelddata":"hierkomt crypto", "date":"1986-12-14", "city":"New York"}';
// const portfolioString = JSON.parse(portfolioData);
// portfolioString.date = new Date(portfolioString.date);
// document.getElementById("portfolioValue").innerHTML = portfolioString.name + ", " + portfolioString.date;



// // TODO: totaalwaarde uit Json string halen
function getPortfolio(){
    fetch(`http://localhost:8080/portfolio`, {
        method: 'POST',
        body: `${localStorage.getItem('token')}`
    })
        .then(res => res.text())
        .then(it => {
            portfolioValue.innerHTML = it
        })
}




// function getPortfolioValue(){
//     fetch(`http://localhost:8080/getPortfolio`, {
//         method: 'POST',
//         body: `${localStorage.getItem('token')}`
//     })
//         .then(res => res.text())
//         .then(it => {
//             const portfolio = it
//         })
// }