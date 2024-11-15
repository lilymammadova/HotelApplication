<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            background-color: #f9f9f9;
        }

        h1 {
            color: #e74c3c;
            text-align: center;
        }

        .back-link {
            margin-top: 20px;
            text-decoration: none;
            color: #e74c3c;
            font-size: 16px;
        }

        .back-link:hover {
            color: #c0392b;
        }
    </style>
</head>
<body>

<h1>${error}</h1>

<a href="${pageContext.request.contextPath}/index.jsp" class="back-link">Go back</a>

</body>
</html>
