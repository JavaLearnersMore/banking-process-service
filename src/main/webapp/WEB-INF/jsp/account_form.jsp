<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Create Account</title>

    <style>
body {
    background: black;
    color: white;
}

.forms-container {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    gap: 30px;
    width: 100%;
    padding: 20px;
    box-sizing: border-box;
}

.account-section {
    width: 420px;
    flex-shrink: 0;
}

.account-form-container {
    width: 100%;
    box-sizing: border-box;
    padding: 20px;
    background: #111;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.account-form-container h2 {
    margin-top: 0;
    margin-bottom: 20px;
    font-size: 24px;
    color: white;
}

.account-form-container label {
    display: block;
    margin-top: 12px;
    margin-bottom: 5px;
    font-weight: bold;
    color: white;
}

.account-form-container input,
.account-form-container select {
    width: 100%;
    padding: 9px;
    box-sizing: border-box;
    background: #222;
    color: white;
    border: 1px solid #555;
}

.account-form-container button {
    width: 100%;
    margin-top: 20px;
    padding: 10px;
    cursor: pointer;

    background: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    font-weight: bold;
    font-size: 15px;
}

.account-form-container button:hover {
    background: #0056b3;
}

.response-message {
    width: 100%;
    max-width: 100%;
    margin-top: 15px;
    box-sizing: border-box;
}

.response-message h3 {
    font-size: 18px;
    margin-bottom: 10px;
    color: white;
}

.response-message pre {
    width: 100%;
    max-width: 100%;
    box-sizing: border-box;
    margin: 0;
    padding: 10px;
    font-size: 12px;
    white-space: pre-wrap;
    overflow-wrap: anywhere;
    word-break: break-word;
    overflow-x: auto;
    background: #222;
    color: white;
    border: 1px solid #555;
    border-radius: 5px;
}
    </style>
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


<script>

document.getElementById("accountForm").addEventListener("submit", function(event) {

    event.preventDefault();

    const accountData = {
        accountNumber: document.getElementById("accountNumber").value,
        ownerType: document.getElementById("ownerType").value,
        ownerRefId: Number(document.getElementById("ownerRefId").value),
        ifsc: document.getElementById("ifsc").value,
        accountType: document.getElementById("accountType").value
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
                "<pre>" + JSON.stringify(data, null, 2) + "</pre>";

        } else {

            document.getElementById("responseMessage").innerHTML =
                "<h3>Account Creation Failed</h3>" +
                "<pre>" + JSON.stringify(data, null, 2) + "</pre>";
        }

    })
    .catch(error => {

        console.error("Error:", error);

        document.getElementById("responseMessage").innerHTML =
            "<h3>Request Failed</h3>" +
            "<pre>" + error.message + "</pre>";
    });

});
</script>
</body>
</html>
