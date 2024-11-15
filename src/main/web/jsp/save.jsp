<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Save State</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 20px;
    }

    h2 {
      text-align: center;
    }

    .form-container {
      max-width: 400px;
      margin: 0 auto;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 8px;
      background-color: #f9f9f9;
    }

    .form-container input[type="submit"] {
      width: 100%;
      padding: 10px;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 16px;
      cursor: pointer;
    }

    .form-container input[type="submit"]:hover {
      background-color: #45a049;
    }

    .message {
      text-align: center;
      margin-top: 20px;
    }
  </style>
</head>
<body>

<h2>Save Current State</h2>

<div class="form-container">
  <form action="${pageContext.request.contextPath}/app/save" method="POST">
    <input type="submit" value="Save State">
  </form>
</div>

<% String message = (String) request.getAttribute("message");
  if (message != null) { %>
<div class="message" style="color: green;"><%= message %></div>
<% } %>

<% String error = (String) request.getAttribute("error");
  if (error != null) { %>
<div class="message" style="color: red;"><%= error %></div>
<% } %>

</body>
</html>
