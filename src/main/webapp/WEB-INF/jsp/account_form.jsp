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
    
    <!-- POST TRANSACTION FORM -->
<div class="account-section">

    <div class="account-form-container">

        <h2>Post Transaction</h2>

        <form id="transactionForm">

            <label>External Reference</label>
            <input type="text" id="externalRef" name="externalRef" placeholder="PGTXN20260823101500ABCDEF" required>

            <label>Debit Account</label>
            <input type="text" id="debitAccount" name="debitAccount" placeholder="AC1000200030" required>

            <label>Credit Account</label>
            <input type="text" id="creditAccount" name="creditAccount"  placeholder="Credit Account" required>

            <label>Amount</label>
            <input type="number" id="amount" name="amount" step="0.01"
                   min="0.01"
                   placeholder="Amount"
                   required>

            <label>Transaction Type</label>
            <select id="transactionType"
                    name="transactionType"
                    required>

                <option value="">Select Transaction Type</option>
                <option value="PG_DEBIT">PG_DEBIT</option>
				<option value="PG_CREDIT">PG_CREDIT</option>
				<option value="NETBANKING">NETBANKING</option>
				<option value="NEFT">NEFT</option>
				<option value="IMPS">IMPS</option>
				<option value="REFUND">REFUND</option>
				<option value="CHARGEBACK">CHARGEBACK</option>
				<option value="FEE">FEE</option>
				<option value="ADJUSTMENT">ADJUSTMENT</option>

            </select>

            <label>Narration</label>
            <input type="text" id="narration" name="narration" placeholder="Payment for order ORD-2026-000123"
                   required>

            <button type="submit"> Post Transaction</button>

        </form>

        <div id="transactionResponseMessage"
             class="response-message">
        </div>
    </div>
</div>

<!-- ACCOUNT STATEMENT FORM -->
<div class="statement-section">

    <div class="account-form-container">

        <h2>Account Statement</h2>

        <form id="statementForm">

            <label>Account Number</label>
            <input type="text"
                   id="statementAccountNumber"
                   name="statementAccountNumber"
                   placeholder="AC1000200030"
                   required>

            <button type="submit">
                Get Statement
            </button>

        </form>

        <div id="statementResponseMessage"
             class="response-message">
        </div>

    </div>

</div>
<!-- REVERSE TRANSACTION FORM -->
<div class="account-section">

    <div class="account-form-container">

        <h2>Reverse Transaction</h2>

        <form id="reverseTransactionForm">

            <label>Original External Reference</label>

            <input type="text"
                   id="reverseExternalRef"
                   name="reverseExternalRef"
                   placeholder="PGTXN20260823101500ABCDEF"
                   required>

            <button type="submit">
                Reverse Transaction
            </button>

        </form>

        <div id="reverseTransactionResponseMessage"
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

/* POST TRANSACTION API */

document.getElementById("transactionForm")
    .addEventListener("submit", function(event) {

        event.preventDefault();

        const transactionData = {

            externalRef:
                document.getElementById("externalRef").value,

            debitAccount:
                document.getElementById("debitAccount").value,

            creditAccount:
                document.getElementById("creditAccount").value,

            amount:
                Number(
                    document.getElementById("amount").value
                ),

            type:
                document.getElementById("transactionType").value,

            narration:
                document.getElementById("narration").value
        };


        fetch(
            "${pageContext.request.contextPath}/core/api/v1/transactions/post",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(transactionData)
            }
        )

        .then(async response => {

            const data = await response.json();

            console.log(
                "Transaction API Response:",
                data
            );


            if (response.ok) {

                document.getElementById(
                    "transactionResponseMessage"
                ).innerHTML =
                    "<h3>Transaction Posted Successfully</h3>" +
                    "<pre>" +
                    JSON.stringify(data, null, 2) +
                    "</pre>";

            } else {

                document.getElementById(
                    "transactionResponseMessage" ).innerHTML =
                    "<h3>Transaction Failed</h3>" +
                    "<pre>" +
                    JSON.stringify(data, null, 2) +
                    "</pre>";
            }

        })

        .catch(error => {

            console.error(
                "Transaction Error:",
                error
            );

            document.getElementById( "transactionResponseMessage").innerHTML =
                "<h3>Request Failed</h3>" +
                "<pre>" +
                error.message +
                "</pre>";
        });

    });
    
/* ACCOUNT STATEMENT API */

document.getElementById("statementForm")
    .addEventListener("submit", function(event) {

        event.preventDefault();

        const accountNumber =
            document.getElementById("statementAccountNumber").value;

        const url =
            "${pageContext.request.contextPath}/core/api/v1/accounts/"
            + encodeURIComponent(accountNumber)
            + "/statement";


        fetch(url, {

            method: "GET",

            headers: {
                "Content-Type": "application/json"
            }

        })

        .then(async response => {

            const data = await response.json();

            console.log(
                "Statement API Response:",
                data
            );

            if (response.ok) {

                document.getElementById(
                    "statementResponseMessage"
                ).innerHTML =
                    "<h3>Account Statement</h3>" +
                    "<pre>" +
                    JSON.stringify(data, null, 2) +
                    "</pre>";

            } else {

                document.getElementById(
                    "statementResponseMessage"
                ).innerHTML =
                    "<h3>Failed to Get Statement</h3>" +
                    "<pre>" +
                    JSON.stringify(data, null, 2) +
                    "</pre>";
            }

        })

        .catch(error => {

            console.error(
                "Statement Error:",
                error
            );

            document.getElementById(
                "statementResponseMessage"
            ).innerHTML =
                "<h3>Request Failed</h3>" +
                "<pre>" +
                error.message +
                "</pre>";
        });

    });
    
/* REVERSE TRANSACTION API */

document.getElementById("reverseTransactionForm")
    .addEventListener("submit", function(event) {

        event.preventDefault();

        const externalRef =
            document.getElementById("reverseExternalRef")
                .value
                .trim();

        const responseMessage =
            document.getElementById(
                "reverseTransactionResponseMessage"
            );

        if (!externalRef) {

            responseMessage.innerHTML =
                "<h3>Validation Failed</h3>" +
                "<pre>External Reference is required</pre>";

            return;
        }

        const url =
            "${pageContext.request.contextPath}"
            + "/core/api/v1/transactions/"
            + encodeURIComponent(externalRef)
            + "/reverse";


        fetch(url, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            }

        })

        .then(async response => {

            const data = await response.json();

            console.log(
                "Reverse Transaction Response:",
                data
            );


            if (response.ok) {

                responseMessage.innerHTML =
                    "<h3>Transaction Reversed Successfully</h3>" +
                    "<pre>" +
                    JSON.stringify(data, null, 2) +
                    "</pre>";

            } else {

                responseMessage.innerHTML =
                    "<h3>Transaction Reversal Failed</h3>" +
                    "<pre>" +
                    JSON.stringify(data, null, 2) +
                    "</pre>";
            }

        })

        .catch(error => {

            console.error(
                "Reverse Transaction Error:",
                error
            );

            responseMessage.innerHTML =
                "<h3>Request Failed</h3>" +
                "<pre>" +
                error.message +
                "</pre>";
        });

    });

</script>

</body>
</html>