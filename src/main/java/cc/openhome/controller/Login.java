package cc.openhome.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/login")
public class Login extends HttpServlet {
	private final String USERS = "C:/Users/hao/eclipse-workspace/pro-gossip/users";
	private final String SUCCESS_PATH = "member";
	private final String ERROR_PATH = "index.html";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		String page;
		if(login(username, password)) {
			if(request.getSession(false) != null) {
				request.changeSessionId();
			}
			request.getSession().setAttribute("login", username);
			page = SUCCESS_PATH;
		} else {
			page = ERROR_PATH;
		}
		
		response.sendRedirect(page);
		
	}
	
	private boolean login(String username, String password) throws IOException{
		if(username != null && username.trim().length() != 0 && password != null) {
			Path userhome = Paths.get(USERS, username);
			return Files.exists(userhome) && isCorrectPassword(password, userhome);
		}
		return false;
	}
	
	private boolean isCorrectPassword(String password, Path userhome) throws IOException{
		Path profile = userhome.resolve("profile");
		try(BufferedReader reader = Files.newBufferedReader(profile)){
			String[] data = reader.readLine().split("\t");
			int encrypt = Integer.parseInt(data[1]);
			int salt = Integer.parseInt(data[2]);
			return password.hashCode() + salt == encrypt;
		}
	}

}
