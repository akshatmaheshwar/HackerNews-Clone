<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Hacker News</title>
</head>
<body>
<h2>Login</h2>
<form action="loginservlet" method="post">
        Username: <input type="text" name="username" required><br><br>
        Password: <input type="password" name="password" required><br><br>
        <input type="submit" value="login">
    </form>
    
<h2>Sign up</h2>
<form action="signupservlet" method="post">
        Username: <input type="text" name="username" required><br><br>
        Password: <input type="password" name="password" required><br><br>
        Choose the type of content<br>
          <input type="checkbox" name="hackernews" value="1">
		  <label for="hackernews"> Hacker news</label><br>
		  <input type="checkbox" name="othernews" value="1">
		  <label for="othernews"> Other news</label><br><br>
        <input type="submit" value="sign up">
    </form>
</body>
</html>