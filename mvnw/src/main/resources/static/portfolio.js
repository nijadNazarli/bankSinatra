//Page elements
const contentFeature = document.querySelector("#contentFeature");
const marketBtn = document.querySelector("#market");
const sellBtn = document.querySelector("#sell");
let cryptoChosen;
let availableUnits;
let unitsForSale;
let unitsForSaleWithPrice;
let salePrice;
let featureContentIsFilled = false;
let confirmBtn;
let unitsToSellToBank;
let unitsToSellToBankInput = document.querySelector("#unitsToSellToBank");
let url = new URL(window.location.href);
let buyBtn = document.querySelector("#buy");
marketBtn.addEventListener('click', setUpMarketAsset);
sellBtn.addEventListener('click', setupSellAsset);
buyBtn.addEventListener('click', ()=> {
    window.location.replace(`${url.origin}/marketplace.html`);
});


//Total values
const totalBalance = document.getElementById('totalBalance')
const totalPortfolioValue = document.getElementById('totalPortfolioValue')
const currencyFormat = {style: "currency", currency: "USD", minimumFractionDigits: 2}

//Create table with header row
const assetTable = document.getElementById('assetTable')
$(assetTable).append("<tr><th>Crypto</th> <th>Symbol</th> <th>Units</th> <th>Price</th> <th>Value</th> <th>24h %</th></tr>")

//Modal - overlay with crypto statistics
const cryptoOverlay = document.getElementById('cryptoOverlay')
const closeCryptoOverlay = document.getElementsByClassName('closeCryptoOverlay')[0]
closeCryptoOverlay.addEventListener('click', () => {
    $(cryptoOverlay).hide();
    $(contentFeature).empty();
    featureContentIsFilled = false;
})
window.onclick = function (event) {
    if (event.target === cryptoOverlay) {
        cryptoOverlay.style.display = "none"
    }
}
const cryptoName = document.getElementById('cryptoName')
let dataMap = null //globale placeholder voor de statistical data
const overlayDetails = document.getElementById('overlayDetails')

//Load total portfolio values
window.addEventListener("DOMContentLoaded", () => {
    getBalance();
    getTotalPortfolioValue();
    getAssets();
})

function getBalance() {
    fetch(`/getBalance`, {
        method: 'GET',
        headers: {'Authorization': localStorage.getItem('token')}
    })
        .then(res => res.text())
        .then(it => {
            totalBalance.innerHTML += parseFloat(it).toLocaleString(
                'en-US', {style: "currency", currency: "USD", currencyDisplay: 'code',
                    minimumFractionDigits: 2, maximumFractionDigits: 2})
        })
}

function getTotalPortfolioValue() {
    fetch('/portfolio/totalPortfolioValue', {
        method: 'GET',
        headers: {'Authorization': localStorage.getItem('token')}
    })
        .then(res => res.text())
        .then(value => {
            totalPortfolioValue.innerHTML += parseFloat(value).toLocaleString(
                'en-US', {style: "currency", currency: "USD", currencyDisplay: 'code',
                    minimumFractionDigits: 2, maximumFractionDigits: 2})
        })
}

function getAssets() {
    fetch('/portfolio/assets', {
        method: 'GET',
        headers: {'Authorization': localStorage.getItem('token')}
    })
        .then(res => res.json())
        .then(async json => {
            console.log(json)
            let nrOfCells = assetTable.rows[0].cells.length
            //For every asset, create a row
            for (let asset of json) {
                const row = document.createElement('tr')
                row.id = asset.crypto.symbol
                row.addEventListener('click', () => openDetails(asset))
                assetTable.appendChild(row)
                //Prepare the required data-cells
                let cells = []
                for (let i = 0; i < nrOfCells; i++) {
                    cells.push(document.createElement('td'))
                }
                cells[0].append(getCryptoLogo(asset.crypto.symbol), asset.crypto.name)
                cells[1].innerHTML = asset.crypto.symbol
                cells[2].innerHTML = asset.units.toLocaleString("en-US", {style: 'decimal', minimumFractionDigits: 2})
                cells[3].innerText = asset.crypto.cryptoPrice.toLocaleString('en-US', currencyFormat)
                cells[4].innerHTML = asset.currentValue.toLocaleString('en-US', currencyFormat)
                let yesterday = new Date()
                yesterday.setDate(yesterday.getDate() - 1)
                let delta = await getAssetValueDeltas(asset.crypto.symbol, yesterday).then(value => value.text()) + "%"
                console.log(delta)
                cells[5].innerHTML = delta
                cells.forEach(cell => row.appendChild(cell))
            }
        })
}

function getAssetValueDeltas(symbol, pastDateTime) {
    return fetch('/portfolio/assetDeltaPct', {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('token'),
            'Symbol': symbol,
            'DateTime': pastDateTime.toISOString(),
            'Content-Type': 'application/json'
        }
    })
}

function getCryptoLogo(symbol) {
    let logo = document.createElement('img')
    logo.src = `/images/cryptoLogos/logo_${symbol}.png`
    logo.classList.add('cryptoLogo')
    return logo
}

function openDetails(asset) {
    console.log(asset)
    $(cryptoName).text(`${asset.crypto.name} (${asset.crypto.symbol})`);
    $(cryptoName).prepend(getCryptoLogo(asset.crypto.symbol))
    $(overlayDetails).empty()
    $(overlayDetails).append(`<br><h3>What makes ${asset.crypto.name} unique?</h3>`)
    $(overlayDetails).append(`<br><p>${asset.crypto.description}</p><br>`)
    $(overlayDetails).append(`<p>Units owned: ${asset.units}</p>`)
    $(overlayDetails).append(`<p>Value owned: ${asset.currentValue.toLocaleString("en-US", currencyFormat)}</p>`)
    $(cryptoOverlay).show();
    $(contentFeature).css("height", "400px"); // dit in css-bestand regelen?
    $(contentFeature).css("width", "auto");
    let daysBack = 100; //Vanaf 220 vult hij niet, data vanuit sql is dan null
    createGraph(asset.crypto.symbol, asset.crypto.cryptoPrice, daysBack)
    featureContentIsFilled = true;
    cryptoChosen = asset.crypto.symbol;
    availableUnits = asset.units.toFixed(2);
}

async function createGraph(symbol, price, daysBack) {
    await getAssetStats(symbol, daysBack)
    var dataPoints1 = [], dataPoints2 = [];
    var stockChart = new CanvasJS.StockChart("contentFeature", {
        animationEnabled: true,
        // theme: "light2",
        backgroundColor: "#022229",
        // colorSet: "greenShades",
        title: {
            text: `Price chart`,
            fontSize: 20,
            fontFamily: "Palatino,Optima,Arial,sans-serif",
            fontColor: "#f5e8cc"
        },
        subtitles: [{
            text:`Current price: ${price.toLocaleString('en-US', currencyFormat)}`,
            fontFamily: "Palatino,Optima,Arial,sans-serif",
            fontColor: "#f5e8cc"
        }],
        charts: [{
            toolTip: {
                shared: true
            },
            axisX: {
                valueFormatString: "D MMM",
                labelFontColor: "#f5e8cc"
            },
            axisY: {
                prefix: "USD ",
                labelFontColor: "#f5e8cc"
            },
            data: [{
                name: "Min-Max",
                type: "rangeArea",
                color: "rgb(255,237,203)",
                markerSize: 0,
                xValueFormatString: "DD-MM-YYY",
                yValueFormatString: "$#,###.##",
                dataPoints: dataPoints1
            }, {
                name: "Average",
                type: "line",
                color: "rgb(189,139,16)",
                markerSize: 0,
                yValueFormatString: "$#,###.##",
                dataPoints: dataPoints2
            }]
        }],
        navigator: {
            data: [{
                dataPoints: dataPoints2,
                color: "#61BAADFF"
            }],
            axisX: {
                labelFontColor: "transparent",
                labelFontWeight: "bolder",
            },
            slider: {
                minimum: new Date(2020, 0o0),
                maximum: new Date(2021, 12),
                // backgroundColor: "#f5e8cc"
            }
        },
        rangeSelector: {
            // backgroundColor: "#f5e8cc",
            // fontColor: "#f5e8cc",
            inputFields: {
                style: {
                    backgroundColor: "#f5e8cc",
                    fontColor: "#004C4D"
                }
            },
            buttonStyle: {
                backgroundColor: "#f5e8cc",
                labelFontColor: "#004C4D",
                backgroundColorOnHover: "#004C4D"
            },
            buttons: [{
                label: "1D",
                range: 1,
                rangeType: "day"
            }, {
                label: "7D",
                range: 7,
                rangeType: "day"
            }, {
                label: "1M",
                range: 1,
                rangeType: "month"
            }, {
                label: "All",
                range: null,
                rangeType: "all"
            }]
        }
    });
    for (let [key, value] of Object.entries(dataMap)) {
        dataPoints1.push({x: new Date(key), y:[Number(value.min), Number(value.max)]});
        dataPoints2.push({x: new Date(key), y:Number(value.avg)});
    }
    stockChart.render();
}

async function getAssetStats(symbol, daysBack) {
    await fetch(`/cryptoStats?symbol=${symbol}&daysBack=${daysBack}`, {
        method: 'GET',
        headers: {'Authorization': localStorage.getItem('token')}
    })
        .then(res => res.json())
        .then(json => {
            dataMap = json
        })
}
async function setUpMarketAsset() {
    if (featureContentIsFilled) {
        $(contentFeature).empty();
        $(contentFeature).css("height", "");
        $(contentFeature).css("width", "");
    }
    const tooltip = $('<span></span>');
    tooltip.css({"visibility": "hidden",
        "width": "165px",
        "background-color": "#001d23",
        "color": "#fff",
        "text-align": "left",
        "border-radius": "6px",
        "padding": "5px 5px 5px 5px",
        "position": "relative",
        "right": "150px",
        "display": "inline-block",
        "height": "40px",
        "z-index": "1"})

    const table = $('<table class="marketTable" style="margin-left: auto; margin-right: auto"></table>');
    $(table).append("<tr><th>Available Units</th><th>Units for sale</th><th>Price per unit</th></tr>");
    const tr = document.createElement("tr");
    const units = $(`<td id="unitsAvailable">${availableUnits}</td>`)
    $(tr).append(units).append(`<td><input id='unitsToSell' type='number' min='0'></td>`);
    $(tr).append("<td>$<input id='pricePerUnit' type='number' min='0'></td>");
    $(table).append(tr);
    $(contentFeature).append(tooltip).append(table).append('<br>')
        .append(`<button id="confirm" class="market" onclick="marketAsset()">Confirm</button>`);
    confirmBtn = $("#confirm");
    featureContentIsFilled = true;
    await getUnitsForSaleWithPrice();

    let unitsOnSale;
    let unitSalePrice;
    for (const key in unitsForSaleWithPrice) {
        unitsOnSale = key;
        unitSalePrice = unitsForSaleWithPrice[key];
    }

    $(units).hover(() => {
        $(tooltip).html(`Units for sale: ${unitsOnSale} <br> Price per unit: $ ${unitSalePrice}`);
        $(tooltip).css('visibility', 'visible');
    }, () => {$(tooltip).css('visibility', 'hidden')});

}

function marketAsset() {
    unitsForSale = $("#unitsToSell").val();
    salePrice = $("#pricePerUnit").val();

    if (!(unitsForSale > 0)) {
        alert("Units for sale must be larger than 0");
        return;
    } else if (!(salePrice > 0)) {
        alert("Price per unit must be larger than 0");
        return;
    }

    let payload = {
        crypto: {
            symbol: cryptoChosen
        },
        units: parseFloat(availableUnits),
        unitsForSale: parseFloat(unitsForSale),
        salePrice: parseFloat(salePrice),
        symbol: cryptoChosen
    }
    fetch(`${url.origin}/marketAsset`, {
        method: 'PUT',
        headers: {
            'Authorization': localStorage.getItem('token'),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    }).then(response => {
        if (response.status === 200) {
            return response.text().then( text => {
                alert(text);
                window.location.replace(`${url.origin}/portfolio.html`);
            });
        }
        return response.text.then(text => {
            alert(text);
        })
    })
}

async function setupSellAsset() {
    if (featureContentIsFilled) {
        $(contentFeature).empty();
        $(contentFeature).css("height", "");
        $(contentFeature).css("width", "");
    }
    const header = $('<h3>Sell your units to Bank Sinatra for their current market value*</h3>')
    const footnote = $('<p>*Bank fees apply</p>')
    const table = $('<table class="sellTable"></table>')
    const sellBankbtn = $('<button class="sellToBankButton" onclick="sellToBank()">Sell</button>')
    $(table).append("<tr><th>Total units</th><th>Current value</th><th>Units to sell</th></tr>")
    const tr = document.createElement("tr")
    const units = document.createElement("td")
    const value = document.createElement("td")
    units.id = "unitsAvailable"
    value.id = "currentValue"
    units.innerText = availableUnits
    value.innerText = "$" + await getCurrentValue(cryptoChosen)
    tr.append(units)
    tr.append(value)
    $(tr).append("<td><input id='unitsToSellToBank' type='number' max={availableUnits} min='0'></td>")
    $(table).append(tr)
    $(contentFeature).append(header, table, footnote, sellBankbtn);
    console.log(getCurrentValue(cryptoChosen))
    featureContentIsFilled = true;
}

async function getCurrentValue(symbol) {
    let value;
    await fetch(`${url.origin}/latestPrice`, {
        method: 'POST',
        headers: {"Authorization": `${localStorage.getItem('token')}`},
        body: symbol
    }).then(res => res.json())
        .then(data => value = data)
    return value
}

async function sellToBank(){
    unitsToSellToBank = $(`#unitsToSellToBank`).val();

    let sellerId = await getIdCurrentUser();
    let payload ={
        buyer: 1,
        seller: parseInt(sellerId),
        crypto: {
            symbol: cryptoChosen
        },
        units: parseFloat(unitsToSellToBank)
    }
    console.log(payload);

    await fetch(`${url.origin}/buy`, {
        method: 'POST',
        headers: {"Authorization": `${localStorage.getItem('token')}`},
        body: JSON.stringify(payload)
    }).then(res=> {
        if(res.status === 200){
            alert("Units successfully sold to Bank Sinatra. Thank you.")
            window.location.replace(`${url.origin}/portfolio.html`);
        } else {
            return res.text().then(it => alert(it));
        }
    })
}

async function getIdCurrentUser() {
    let userId;
    await fetch(
        `${url.origin}/getUserId`, {
            method: 'POST',
            headers: { "Authorization": `${localStorage.getItem('token')}`}
        }).then(res => {
        if (res.status === 200) {
            return res.text().then(it => { userId = it;})
        } else {
            alert("Not a valid token anymore");
        }
    })
    return userId;
}

async function getUnitsForSaleWithPrice() {
    unitsForSaleWithPrice =
        await fetch(`${url.origin}/getUnitsForSale`, {
        method: 'POST',
        headers: {"Authorization": `${localStorage.getItem('token')}`},
        body: cryptoChosen
    }).then(res => {return res.json()})
}
