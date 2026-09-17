<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Account Management</title>
   <link rel="stylesheet" href="${pageContext.request.contextPath}/css/account.css">
</head>

<body>

<div class="forms-container">

    <!-- CREATE ACCOUNT FORM -->
    <div class="account-section">

        <div class="account-form-container">

            <h2>Create Account</h2>

            <form id="accountForm">

                <label>Account Number</label>
                <input type="text"
                       id="accountNumber"
                       name="accountNumber"
                       placeholder="AC9998887770"
                       required>

                <label>Owner Type</label>
                <select id="ownerType" name="ownerType" required>
                    <option value="">Select Owner Type</option>
                    <option value="CUSTOMER">CUSTOMER</option>
                    <option value="MERCHANT">MERCHANT</option>
                </select>

                <label>Owner Reference ID</label>
                <input type="number"
                       id="ownerRefId"
                       name="ownerRefId"
                       placeholder="5002"
                       required>

                <label>IFSC</label>
                <input type="text"
                       id="ifsc"
                       name="ifsc"
                       placeholder="NETB0000001"
                       required>

                <label>Account Type</label>
                <select id="accountType" name="accountType" required>
                    <option value="">Select Account Type</option>
                    <option value="SAVINGS">SAVINGS</option>
                    <option value="CURRENT">CURRENT</option>
                </select>

                <button type="submit">
                    Create Account
                </button>

            </form>

            <div id="responseMessage" class="response-message"></div>

        </div>

    </div>


    <!-- ACCOUNT BALANCE FORM -->
    <div class="account-section">

        <div class="account-form-container">

            <h2>Account Balance</h2>

            <form id="balanceForm">

                <label>Account Number</label>

                <input type="text"
                       id="balanceAccountNumber"
                       name="balanceAccountNumber"
                       placeholder="AC1000200030"
                       required>

                <button type="submit">
                    Get Balance
                </button>

            </form>

            <div id="balanceResponseMessage"
                 class="response-message">
            </div>

        </div>

    </div>

</div>


<script>

document.getElementById("accountForm").addEventListener("submit", function(event) {

    event.preventDefault();

    const accountData = {

        accountNumber:
            document.getElementById("accountNumber").value,

        ownerType:
            document.getElementById("ownerType").value,

        ownerRefId:
            Number(document.getElementById("ownerRefId").value),

        ifsc:
            document.getElementById("ifsc").value,

        accountType:
            document.getElementById("accountType").value
    };


    fetch("${pageContext.request.contextPath}/core/api/v1/accounts", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(accountData)

    })

    .then(async response => {

        const data = await response.json();

        console.log("API Response:", data);

        if (response.ok) {

            document.getElementById("responseMessage").innerHTML =
                "<h3>Account Created Successfully</h3>" +
                "<pre>" +
                JSON.stringify(data, null, 2) +
                "</pre>";

        } else {

            document.getElementById("responseMessage").innerHTML =
                "<h3>Account Creation Failed</h3>" +
                "<pre>" +
                JSON.stringify(data, null, 2) +
                "</pre>";
        }

    })

    .catch(error => {

        console.error("Error:", error);

        document.getElementById("responseMessage").innerHTML =
            "<h3>Request Failed</h3>" +
            "<pre>" +
            error.message +
            "</pre>";
    });

});



/* ACCOUNT BALANCE API */

document.getElementById("balanceForm").addEventListener("submit", function(event) {

    event.preventDefault();

    const accountNumber =
        document.getElementById("balanceAccountNumber").value;


    const url =
        "${pageContext.request.contextPath}/core/api/v1/accounts/"
        + encodeURIComponent(accountNumber)
        + "/balance";


    fetch(url, {

        method: "GET",

        headers: {
            "Content-Type": "application/json"
        }

    })

    .then(async response => {

        const data = await response.json();

        console.log("Balance API Response:", data);

        if (response.ok) {

            document.getElementById("balanceResponseMessage").innerHTML =
                "<h3>Account Balance</h3>" +
                "<pre>" +
                JSON.stringify(data, null, 2) +
                "</pre>";

        } else {

            document.getElementById("balanceResponseMessage").innerHTML =
                "<h3>Failed to Get Account Balance</h3>" +
                "<pre>" +
                JSON.stringify(data, null, 2) +
                "</pre>";
        }

    })

    .catch(error => {

        console.error("Error:", error);

        document.getElementById("balanceResponseMessage").innerHTML =
            "<h3>Request Failed</h3>" +
            "<pre>" +
            error.message +
            "</pre>";
    });

});

</script>

</body>
</html>