<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Get Paginated and Sorted Apartments</title>
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

    .form-container input[type="number"],
    .form-container select {
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

<h2>Get Paginated and Sorted Apartments</h2>

<div class="form-container">
  <form action="app" method="POST">
    <label for="pageNumber">Page Number:</label>
    <input type="number" id="pageNumber" name="pageNumber" min="1" required>

    <label for="pageSize">Page Size:</label>
    <input type="number" id="pageSize" name="pageSize" min="1" required>

    <label for="sortParam">Sort by:</label>
    <select name="sortParam" id="sortParam">
      <option value="apartmentid">Apartment ID</option>
      <option value="price">Price</option>
      <option value="availability">Availability</option>
      <option value="clientname">Client Name</option>
    </select>

    <input type="hidden" name="option" value="getPaginatedAndSortedApartments">
    <input type="submit" value="Get Apartments">
  </form>
</div>

</body>
</html>
