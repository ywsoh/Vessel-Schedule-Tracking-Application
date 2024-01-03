function padSingleDigit(k) {
    if (k < 10) {
        return "0" + k;
    }
    else {
        return k;
    }
}

function getDatesBetween(startDate, stopDate) {
    var [DD, MM, YYYY] = startDate.split('-');
    startDate = new Date(YYYY, MM - 1, DD);
    var [DD, MM, YYYY] = stopDate.split('-');
    stopDate = new Date(YYYY, MM - 1, DD);
    var dateArray = new Array();
    var currentDate = startDate;
    while (currentDate <= stopDate) {
        dateArray.push(padSingleDigit(currentDate.getDate()) + "-" + padSingleDigit(currentDate.getMonth() + 1) + "-" + currentDate.getFullYear());
        currentDate.setDate(currentDate.getDate() + 1);
    }
    return dateArray;
}

function compareSets(as, bs) {
    if (as.size !== bs.size) return false;
    for (var a of as) if (!bs.has(a)) return false;
    return true;
}

function currentTime() {
    var date = new Date();
    [day, month, year] = getDate(date);
    [hour, min, sec] = getTime(date);
    document.getElementById("time-now").innerText = day + " : " + month + " : " + year + " - " + hour + " : " + min + " : " + sec;
    var t = setTimeout(function () { currentTime() }, 1000);
}

function getDate(date) {
    var day = padSingleDigit(date.getDate());
    var month = padSingleDigit(date.getMonth()+1);
    var year = date.getFullYear();
    return [day, month, year]
}

function getTime(date) {
    var hour = padSingleDigit(date.getHours());
    var min = padSingleDigit(date.getMinutes());
    var sec = padSingleDigit(date.getSeconds());
    return [hour, min, sec];
}