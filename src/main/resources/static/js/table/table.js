function customSort(sortName, sortOrder, data) {
    var order = sortOrder === 'desc' ? -1 : 1
    data.sort(function (a, b) {
        if (sortName === "avgSpeed") {
            // Removes any HTML tags
            var aa = +((a[sortName] + '').replaceAll(/(<([^>]+)>)/ig, ""));
            var bb = +((b[sortName] + '').replaceAll(/(<([^>]+)>)/ig, ""));

            // Parse 0 into empty string
            if (aa === 0) {
                aa = '';
            }
            if (bb === 0) {
                bb = ''
            }

        } else {
            // Gets value of table cell
            var aa = a[sortName]
            var bb = b[sortName]
            if (sortName === "berthingTime" || sortName === "depatureTime") {
                aa = parseDateToISODate(aa);
                bb = parseDateToISODate(bb);
            }
        }

        // Ignore blanks in sort
        if (aa === '') {
            return 1;
        }
        if (bb === '') {
            return -1;
        }

        // Fix #161: undefined or null string sort bug.
        if (aa === undefined || aa === null) {
            aa = '';
        }
        if (bb === undefined || bb === null) {
            bb = '';
        }

        // IF both values are numeric, do a numeric comparison
        if ($.isNumeric(aa) && $.isNumeric(bb)) {
            // Convert numerical values form string to float.
            aa = parseFloat(aa);
            bb = parseFloat(bb);
            if (aa < bb) {
                return order * -1;
            }
            return order;
        }

        if (aa === bb) {
            return 0;
        }

        // If value is not a string, convert to string
        if (typeof aa !== 'string') {
            aa = aa.toString();
        }

        if (aa.localeCompare(bb) === -1) {
            return order * -1;
        }

        return order;

    })
};
function dateFilter(row, filter) {
    let matches = false;
    for (const key in filter) {
        var dataDate = row[key].split(" ")[0];
        if ((Array.isArray(filter[key]) && filter[key].includes(dataDate)) || (!Array.isArray(filter[key]) && dataDate === filter[key])) {
            matches = true
        }
    }
    return matches;
};
function parseDateToISODate(date) {
    var datetime = date.split(" ");
    var [DD, MM, YYYY] = datetime[0].split('-');
    return YYYY + "-" + MM + "-" + DD + "T" + datetime[1];
};