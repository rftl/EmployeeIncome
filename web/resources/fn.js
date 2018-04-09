$(document).ready(function () {
    $('#id').keydown(function (e) {
        if (e.which === 13) {
            $('#search').click();
        }
    });
    $('#fname').keydown(function (e) {
        if (e.which === 13) {
            $('#search').click();
        }
    });
    $('#lname').keydown(function (e) {
        if (e.which === 13) {
            $('#search').click();
        }
    });

    $(function () {
        $("#datepicker").datepicker();
    });

    $('.close').on("click", function () {
        close(this);
    });

    $('.minmax').on("click", function () {
        minmax(this);
    });

    $("input[name='calc']").change(function(){
        checkRadio();
    });

    $("#wkSelect").on("change",function () {
        getWeekHours();
    });

    $("#dialog").dialog({
        autoOpen: false, modal: true, show: "blind", hide: "blind"
    });
})

function getResult() {
    $('#resultDiv').hide(100);
    $('#selectDiv').hide(100);
    $('#resultDiv').empty();
    $('#resposeText').empty();
    $.ajax({
        type: 'GET',
        url: '/Records',
        data: {
            type: 'records',
            id: $('#id').val(),
            fname: $('#fname').val(),
            lname: $('#lname').val()
        },
        dataType: 'json',
        success: function (result) {
            if (result.length > 0) {
                var table = $('<table class="ctable" align="center"><thead></thead><tbody></tbody></table>').attr('id', 'resTable');
                $('#resultDiv').append(table);
                var header = $('<tr>');
                var keys = [];
                for (var k in result[0]) keys.push(k);
                $.each(keys, function (index, key) {
                    header.append($('<th>').text(key.replace("_", " ").toUpperCase()));
                });

                header.appendTo('#resTable thead');

                $.each(result, function (index, employee) {
                    var $tr = $('<tr>').append(
                        $('<td>').text(employee.emp_id),
                        $('<td>').text(employee.first_name),
                        $('<td>').text(employee.last_name),
                        $('<td>').text(employee.rate),
                        $('<td>').text(employee.withholdings),
                        $('<td>').html('<button onClick="selectEmployee(' + employee.emp_id + ',\'' + employee.first_name +
                        '\',\'' + employee.last_name + '\',' + employee.rate + ',' + employee.withholdings + ')">SELECT</button>')
                    ).appendTo('#resTable tbody');
                })
                $('#resultDiv').slideDown(300);
            }
            else {
                $('#resultDiv').text("No results");
                $('#resultDiv').slideDown(300);
            }

        },
        error: function (xhr) {
            $("#resultDiv").text("There was an error retrieving the results");
        }
    })
}

function getHours(id) {
    $('#resultDiv').hide(100);
    $('#selectDiv').hide(100);
    $('#hrTable').remove();
    $('#hourResMessage').text('');
    $.ajax({
        type: 'GET',
        url: '/Records',
        data: {
            type: 'hours',
            id: id
        },
        dataType: 'json',
        success: function (result) {
            if (result.length > 0) {
                var table = $('<table class="ctable" align="center"><thead></thead><tbody></tbody></table>').attr('id', 'hrTable');
                $('#availableDiv').append(table);
                var header = $('<tr>');
                var keys = [];
                for (var k in result[0]) keys.push(k);
                $.each(keys, function (index, key) {
                    header.append($('<th>').text(key.replace("_", " ").toUpperCase()));
                });

                header.appendTo('#hrTable thead');

                $.each(result, function (index, eHours) {
                    var $tr = $('<tr id="'+eHours.week+'">').append(
                        $('<td>').text(eHours.week),
                        $('<td>').text(eHours.sun),
                        $('<td>').text(eHours.mon),
                        $('<td>').text(eHours.tue),
                        $('<td>').text(eHours.wed),
                        $('<td>').text(eHours.thu),
                        $('<td>').text(eHours.fri),
                        $('<td>').text(eHours.sat),
                        $('<td>').text(eHours.total)
                    ).appendTo('#hrTable tbody');

                    var opt = $('<option></option>').attr("value",eHours.week).text(eHours.week).appendTo('#wkSelect');
                })
            }
            else {
                $('#hourResMessage').text("No hours available for processing");
            }

        },
        error: function (xhr) {
            $("#resultDiv").text("There was an error retrieving the results");
        }
    })
}

function submitHours() {
    $('#resposeText').empty();
    var sunV = validateInput($('#sun').val());
    var monV = validateInput($('#mon').val());
    var tueV = validateInput($('#tue').val());
    var wedV = validateInput($('#wed').val());
    var thuV = validateInput($('#thu').val());
    var friV = validateInput($('#fri').val());
    var satV = validateInput($('#sat').val());

    if (sunV && monV && tueV && wedV && thuV && friV && satV) {
        $.ajax({
            type: 'POST',
            url: '/Records',
            data: {
                id: $('#selectedId').text(),
                week: $('#datepicker').val(),
                sun: $('#sun').val(),
                mon: $('#mon').val(),
                tue: $('#tue').val(),
                wed: $('#wed').val(),
                thu: $('#thu').val(),
                fri: $('#fri').val(),
                sat: $('#sat').val()
            },
            dataType: 'json',
            success: function (result) {
                $('#resposeText').text("Hours submission: " + result.state + " - " + result.msg).slide(200);
            },
            error: function (xhr) {
                $("#resultDiv").text("There was an error submitting the hours");
            }
        })
    } else {
        $('#dialog > p').text("Hour input is invalid. Please review");
        $('#dialog').dialog("open");
        return false
    }

}

function selectEmployee(id, fname, lname, rate, wh) {
    $('#selectedId').text(id);
    $('#selectedName').text(fname + " " + lname);
    $('#hRate').text(rate);
    $('#hWh').text(wh);
    $('#selectDiv').find('input[type=text]').val('');
    getHours(id);
    $('#resultDiv').hide(100);
    $('#selectDiv').slideDown(300);
}

function calculatePay(id, fname, lname, rate, wh) {
    var value = $('input[name=calc]:checked').val();
    if(value === 'table'){
        alert($('#manHours').text());
    }else if(value === 'manual'){
        alert($('#manHoursB').val());
    }
}


function validateInput(input) {
    if ($.isNumeric(input) && input >= 0 && input <= 24)
        return true;
    else
        return false;
}

function clearForm() {
    $('input[type=text]').val('');
    $('#resultDiv').slideUp(100);
    $('#selectDiv').slideUp(100);
    $('#calcDiv').slideUp(100);
    $('#resposeText').empty();
}

function clearHours() {
    $('.hour-entry').val('');
}

function close(item) {
    $(item).parents('div').hide(100);
    $('#resultDiv').slideDown(300);
    $('#resposeText').empty(200);
    $('.innerDiv > .ctable').show();
}

function minmax(item) {
    $(item).parent().children('table, p').toggle("slide", { direction: "up" }, 100);
    var icon = $(item).text();
    $(item).text(icon == "-" ? "+" : "-");
}

function checkRadio(){
    var value = $('input[name=calc]:checked').val();
    if(value === 'table'){
        $('#manHours').html('');
        $('#calcTable td:first-child, #calcTable th:first-child').show(100);
    }else if(value === 'manual'){
        $('#manHours').html('<input class="hour-entry" type="text" id="manHoursB"/>');
        $('#calcTable td:first-child, #calcTable th:first-child').hide(100);
    }
}

function getWeekHours(){
    var selected = $('#wkSelect').find(":selected").text();
    var hours = $('#'+ selected+ ' td:last-child').text();
    $('#manHours').text(hours);
}