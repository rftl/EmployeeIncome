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

    $("#dialog").dialog({
        autoOpen : false, modal : true, show : "blind", hide : "blind"
    });

})

function getResult() {
    $('#resultDiv').hide(100);
    $('#selectDiv').hide(100);
    $('#resultDiv').empty();
    $.ajax({
        type: 'GET',
        url: '/Records',
        data: {
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

                $.each(result, function (index, element) {
                    var $tr = $('<tr>').append(
                        $('<td>').text(element.emp_id),
                        $('<td>').text(element.first_name),
                        $('<td>').text(element.last_name),
                        $('<td>').text(element.rate),
                        $('<td>').text(element.withholdings),
                        $('<td>').html('<button onClick="selectEmployee(' + element.emp_id + ')">SELECT</button>')
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

function clearForm() {
    $('input[type=text]').val('');
    $('#resultDiv').slideUp(100);
    $('#selectDiv').slideUp(100);
    $('#resposeText').empty();
}

function selectEmployee(id) {
    $('#selectedId').text(id);
    $('#selectDiv').find('input[type=text]').val('');
    $('#resultDiv').hide(100);
    $('#selectDiv').slideDown(300);
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
                $('#resposeText').text("Hours submission: " + result.state +" - "+ result.msg).slide(200);
            },
            error: function (xhr) {
                $("#resultDiv").text("There was an error submitting the hours");
            }
        })
    }else{
        $('#dialog > p').text("Hour input is invalid. Please review");
        $('#dialog').dialog("open");
        return false
    }

}

function validateInput(input) {
    if($.isNumeric(input) && input >= 0 && input <= 24 )
        return true;
    else
        return false;
}

function close(item) {
    $(item).parents('div').hide(100);
    $('#resultDiv').slideDown(300);
    $('#resposeText').empty(200);
}

