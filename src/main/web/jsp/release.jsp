<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Release Apartment</title>
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

    .form-container label {
      display: block;
      margin: 10px 0 5px;
    }

    .form-container input[type="text"] {
      width: 100%;
      padding: 8px;
      margin-bottom: 15px;
      border: 1px solid #ccc;
      border-radius: 4px;
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

<h2>Release Apartment</h2>

<div class="form-container">
  <form action="app" method="POST">
    <label for="apartmentId">Apartment ID:</label>
    <input type="text" id="apartmentId" name="apartmentId" required>

    <input type="hidden" name="option" value="releaseApartment">
    <input type="submit" value="Confirm Release">
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
