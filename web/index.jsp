<%--
  Created by IntelliJ IDEA.
  User: rftl
  Date: 3/2/18
  Time: 2:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="resources/main.css"/>
    <link rel="stylesheet" type="text/css" href="resources/jquery-ui.css"/>
    <script src="resources/jquery-3.3.1.min.js"></script>
    <script src="resources/fn.js"></script>
    <script src="resources/jquery-ui.js"></script>
</head>
<body>
<h1>Test</h1>
<div class="main popup">
    <div class="main-form" id="searchDiv">
        <input type="text" class="form-field" placeholder="ID" id="id">
        <input type="text" class="form-field" placeholder="First Name" id="fname">
        <input type="text" class="form-field" placeholder="Last Name" id="lname">
        <input type="button" class="button" id="search" onclick="getResult()" value="SEARCH"></input>
        <input type="button" class="button" id="clear" onclick="clearForm()" value="CLEAR"></input>
    </div>
</div>

<div class="main popup" id="resultDiv"></div>


<div class="main popup" id="selectDiv">
    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>

    <div class="innerDiv" id="infoDiv">
        <table class="ctable" id="infoTable">
            <tbody>
            <tr>
                <td id="selectedName"></td>
            </tr>
            </tbody>
        </table>
    </div>

    <div class="innerDiv" id="availableDiv">
        <h3>Available Hours</h3>
        <button type="button" class="minmax" data-dismiss="alert" aria-hidden="true">-</button>
        <p class="resMsg" id="hourResMessage"></p>
    </div>

    <div class="innerDiv" id="calcDiv">
        <h3>Calculate Pay</h3>
        <button type="button" class="minmax" data-dismiss="alert" aria-hidden="true">-</button>
        <p class="rdSpan">
            <input class="rd" type="radio" name="calc" value="table" id="sTable"><label for="sTable">From table</label>
            <input class="rd" type="radio" name="calc" value="manual" id="sManual"><label for="sManual">Manual</label>
        </p>
        <table class="ctable" align="center" id="calcTable">
            <thead>
            <tr>
                <th>Week</th>
                <th>Hours</th>
                <th>Rate</th>
                <th>Withholdings</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td id="hWeek">
                    <select id="wkSelect"></select>
                </td>
                <td id="manHours"></td>
                <td id="hRate"></td>
                <td id="hWh"></td>
                <td>
                    <button onclick="calculatePay()">&#10004;</button>
                    <button onclick="clearPay()">&#10008;</button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div class="innerDiv" id="hoursDiv">
        <h3>Submit Hours</h3>
        <button type="button" class="minmax" data-dismiss="alert" aria-hidden="true">-</button>
        <table class="ctable" align="center" id="selectedTable">
            <thead>
            <tr>
                <th>ID</th>
                <th>Week</th>
                <th>Sun</th>
                <th>Mon</th>
                <th>Tue</th>
                <th>Wed</th>
                <th>Thu</th>
                <th>Fri</th>
                <th>Sat</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td id="selectedId"></td>
                <td><input class="hour-entry" type="text" id="datepicker"></td>
                <td><input class="hour-entry" type="text" id="sun"/></td>
                <td><input class="hour-entry" type="text" id="mon"/></td>
                <td><input class="hour-entry" type="text" id="tue"/></td>
                <td><input class="hour-entry" type="text" id="wed"/></td>
                <td><input class="hour-entry" type="text" id="thu"/></td>
                <td><input class="hour-entry" type="text" id="fri"/></td>
                <td><input class="hour-entry" type="text" id="sat"/></td>
                <td>
                    <button onclick="submitHours()">&#10004;</button>
                    <button onclick="clearHours()">&#10008;</button>
                </td>
            </tr>
            </tbody>
        </table>
        <p id="resposeText"></p>
    </div>
</div>
<div id="dialog" title="Hey listen!">
    <p></p>
</div>
</body>
</html>
