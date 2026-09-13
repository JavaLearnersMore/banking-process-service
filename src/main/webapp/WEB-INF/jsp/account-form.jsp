<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Create Account</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
        }

        .container {
            width: 450px;
            margin: 50px auto;
            padding: 25px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 0 10px #ccc;
        }

        h2 {
            text-align: center;
        }

        label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            box-sizing: border-box;
        }

        button {
            width: 100%;
            margin-top: 25px;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            cursor: pointer;
            border-radius: 4px;
        }

        button:hover {
            background-color: #0056b3;
        }

        .success {
            color: green;
            margin-top: 15px;
        }

        .error {
            color: red;
            margin-top: 15px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Create Account</h2>

    <form action="${pageContext.request.contextPath}/accounts/create"
          method="post">

        <label>Account Number</label>
        <input type="text"
               name="accountNumber"
               placeholder="AC9998887770"
               required>

        <label>Owner Type</label>
        <select name="ownerType" required>
            <option value="">Select Owner Type</option>
            <option value="CUSTOMER">CUSTOMER</option>
            <option value="MERCHANT">MERCHANT</option>
        </select>

        <label>Owner Reference ID</label>
        <input type="number"
               name="ownerRefId"
               placeholder="5002"
               required>

        <label>IFSC</label>
        <input type="text"
               name="ifsc"
               placeholder="NETB0000001"
               required>

        <label>Account Type</label>
        <select name="accountType" required>
            <option value="">Select Account Type</option>
            <option value="SAVINGS">SAVINGS</option>
            <option value="CURRENT">CURRENT</option>
        </select>

        <button type="submit">
            Create Account
        </button>

    </form>

</div>

</body>
</html>
