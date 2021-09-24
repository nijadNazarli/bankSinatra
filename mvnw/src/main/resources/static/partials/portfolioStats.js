var portMap = null
var assets = null

window.addEventListener("DOMContentLoaded", async () => {
    await getPortfolioStats()
    createPortGraph()
    await getAssetData()
    createPieChart()
})

async function getPortfolioStats(){
    await fetch(`${url.origin}/portfolioStats`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    })
        .then(res => res.json())
        .then (it => {
            portMap = it
            console.log(it)
        })
}

function createPortGraph() {
    var portDataPoints = []
    var chart = new CanvasJS.Chart("chartContainer", {
        animationEnabled: false,
        //backgroundColor: "rgb(2,34,41)",
        backgroundColor: "transparent",
        toolTip: {
            fontFamily: "tahoma",
            fontColor: "rgb(24,20,1)",
            cornerRadius: 30,
            borderColor: "rgb(81,123,117)",
            backgroundColor: "rgb(81,123,117, .6)",
            contentFormatter: function ( e ) {
                return "$" +  e.entries[0].dataPoint.y;
            },
        },
        toolTipContent: "{y}",
        // title:{
        //     text: "Total portfolio value",
        //     fontFamily: "tahoma",
        //     fontColor: "rgb(245,232,204)",
        // },
        axisY: {
            valueFormatString: "#,###.##",
            // suffix: ".-",
            prefix: "$",
            labelFontColor: "rgb(171,160,132)",
            labelFontFamily: "tahoma",
            labelFontSize: 12,
            gridColor: "rgb(24,20,1)",
            tickLength: 0,
            lineThickness: 0,
        },
        axisX: {
            valueFormatString: "DD-MM-YY",
            labelAngle: -60,
            interval: 1,
            intervalType: "week",
            labelFontColor: "rgb(171,160,132)",
            labelFontFamily: "tahoma",
            labelFontSize: 12,
            tickLength: 0,
            lineColor: "rgb(24,20,1)",
        },
        data: [{
            type: "area",
            //color: "rgba(81,123,117,.7)",
            color: "rgba(255,165,0,0.6)",
            markerSize: 5,
            //xValueType: "DD-MM-YY",
            xValueFormatString: "DD-MM-YY",
            yValueFormatString: "$#,###.##",
            dataPoints: portDataPoints
        }],
    })
    for (let [key, value] of Object.entries(portMap)) {
        portDataPoints.push({
            x: new Date(key),
            y: Number(value)
        })
    }

    chart.render();

}// end

async function getAssetData(){
    await fetch(`${url.origin}/portfolio/assets`, {
        method: 'GET',
        headers: { "Authorization": `${localStorage.getItem('token')}`}
    })
        .then(res => res.json())
        .then (it => {
            assets = it
            console.log(it)
            console.log(assets[1].crypto.name)
            console.log(assets.length)
        })
}

function createPieChart(){
    var assetDataPoints = []
    CanvasJS.addColorSet("sinatraColors",
        [//colorSet Array

            "#FFA50099",
            "#517b75",
            "#f5e8cc",
            "#654d19",
            "#f5b512",
            "#92bbb7",
            "#181401",


        ]);
    var chart = new CanvasJS.Chart("piechartContainer", {
        animationEnabled: false,
        backgroundColor: "transparent",
        colorSet: "sinatraColors",
        // legend:{
        //     cursor: "pointer",
        //     fontFamily: "tahoma",
        //     FontSize: 14,
        //     fontColor: "rgb(24,20,1)",
        //     legendText: "{label}",
        // },
        toolTip: {
            fontFamily: "tahoma",
            fontColor: "rgb(0,0,0)",
            cornerRadius: 30,
            borderColor: "rgb(81,123,117)",
            backgroundColor: "rgb(81,123,117, .6)",
            contentFormatter: function ( e ) {
                return "$" +  e.entries[0].dataPoint.y;
            },
        },
        title: {
            text: "Your crypto: ",
            fontFamily: "tahoma",
            fontSize: 18,
            fontColor: "rgb(24,20,1)",
        },
        axisY: {
            tickLength: 0,
            lineThickness: 0,
        },
        data: [{
            type: "pie",
            itemclick: explodePie,
            // showInLegend: true,
            indexLabelFontSize: 14,
            indexLabelFontColor: "rgb(171,160,132)",
            indexLabelFontFamily: "tahoma",
            startAngle: 240,
            yValueFormatString: "##0.00\"%\"",
            indexLabel: "{name}",
            dataPoints: assetDataPoints
        }]
    });


    for (let i = 0; i < assets.length; i++) {
        assetDataPoints.push({
            name: assets[i].crypto.name,
            y: Number(assets[i].currentValue).toFixed(2)
        })
    }

    function explodePie (e) {
        if (typeof (e.dataSeries.dataPoints[e.dataPointIndex].exploded) === "undefined" || !e.dataSeries.dataPoints[e.dataPointIndex].exploded) {
            e.dataSeries.dataPoints[e.dataPointIndex].exploded = true;
        } else {
            e.dataSeries.dataPoints[e.dataPointIndex].exploded = false;
        }
    }

    chart.render();
}