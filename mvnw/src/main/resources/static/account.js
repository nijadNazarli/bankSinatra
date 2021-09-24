let url = new URL(window.location.href);
const transactionsBuyer = []
const transactionsSeller = []
const transactionTableBuyer = document.getElementById("transactionTableBuyer")
const transactionTableSeller = document.getElementById("transactionTableSeller")
const buyer = "buyer"
const seller = "seller"

window.addEventListener("DOMContentLoaded", setupPage)

async function setupPage(){
    await getAccount()
    await getTransactionsBuyer()
    await getTransactionsSeller()
    await createTable(transactionsBuyer, transactionTableBuyer, seller)
    await createTable(transactionsSeller, transactionTableSeller, buyer)
}

async function getAccount(){
    fetch(`${url.origin}/getAccount`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    }).then(res=> res.json())
        .then(async it => {
            for (const key of Object.keys(it)) {
                if (key === "iban") {
                    let iban = it[key]
                    $("#iban").append(iban);
                } else if (key === "balance") {
                    $("#balance").append(it[key].toFixed(2))
                }
            }
        })
}

async function getTransactionsBuyer(){
    await fetch(`${url.origin}/transactionsBuyer`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    }).then(res=> {
        if (res.status === 200){
            console.log("dit werkt")
        } else if (res.status === 404){
            console.log("hoiiiii")
        }
        return res.json();
    }).then(it =>{
            it.forEach(it => transactionsBuyer.push(it))
        })
    return transactionsBuyer
}

async function getTransactionsSeller(){
    await fetch(`${url.origin}/transactionsSeller`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    }).then(res=> {
        if (res.status === 200){
            console.log("dit werkt")
        } else if (res.status === 404){
            console.log("hoiiiii")
        }
        return res.json();
    }).then(it =>{
        it.forEach(it => transactionsSeller.push(it))
    })
    return transactionsSeller
}

async function createTable(array, table, transactionParty){
    if(array !== undefined){
        await fillTable(array, table, transactionParty)
    }
}

async function fillTable(array, table, transactionParty) {
    for (let i = 0; i < array.length; i++) {
        const tr = document.createElement("tr")
        tr.id = `tr${i}`
        let transaction = Object.assign({}, array[i])
        let symbol = getSymbol(transaction)

        for (const key of Object.keys(transaction)) {
            const td = document.createElement("td")
            if (key === "transactionDate") {
                td.id = `date${i}`
                let date = new Date(transaction[key])
                td.textContent = formatDate(date)
                tr.append(td)
            } else if (key === transactionParty) {
                td.id = `transactionParty${i}`
                td.textContent = await getName(transaction[key])
                tr.append(td)
            } else if (key === "units") {
                td.id = `purchase${i}`
                td.textContent = transaction[key].toFixed(2).concat(" " + symbol)
                tr.append(td)
            } else if (key === "transactionPrice") {
                td.id = `price${i}`
                td.textContent = "$" + transaction[key].toFixed(2)
                tr.append(td)
            } else if (key === "bankCosts") {
                td.id = `fee${i}`
                td.textContent = "$" + transaction[key].toFixed(2)
                tr.append(td)
            }
            $(table).append(tr)
        }
    }
}

function getSymbol(transaction){
    for (const key of Object.keys(transaction)){
        if(!(key === "crypto")){
        } else {
            let symbol = "symbol"
            let crypto = Object.assign({}, transaction[key])
            return crypto[symbol]
        }
    }
}

async function getName(accountId){
    let name;
    await fetch(`${url.origin}/requestName`, {
        method: 'POST',
        headers: { "Content-Type": "text/plain"},
        body: accountId
    }).then(res => res.json())
        .then(data => name = data)
    return name
}

function formatDate(date){
    const year = date.getFullYear()
    const month = addZero(date.getMonth() + 1)
    const day = addZero(date.getDate())
    const hour = addZero(date.getHours())
    const minute = addZero(date.getMinutes())
    return `${year}-${month}-${day} ${hour}:${minute}`
}

function addZero(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
}

$('th').click(function(){
    const table = $(this).parents('table').eq(0);
    let rows = table.find('tr:gt(0)').toArray().sort(comparer($(this).index()));
    this.asc = !this.asc
    if (!this.asc){rows = rows.reverse()}
    for (let i = 0; i < rows.length; i++){table.append(rows[i])}
})
function comparer(index) {
    return function(a, b) {
        let valA = getCellValue(a, index), valB = getCellValue(b, index);
        if (hasNumber(valA)){
            valA = valA.replace(/[^0-9]/g, '')
        }
        if(hasNumber(valB)){
            valB = valB.replace(/[^0-9]/g, '')
        }
        if ($.isNumeric(valA) && $.isNumeric(valB)) {
            return valA - valB
        } else {
            return valA.toString().localeCompare(valB)
        }
    }
}
function getCellValue(row, index){ return $(row).children('td').eq(index).text() }

function hasNumber(myString) {
    return /\d/.test(myString);
}