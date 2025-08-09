package com.Alcura.FactoryPattern.ServiceFactory;

import com.Alcura.FactoryPattern.Service.LoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class BaseLoginHandler implements LoginHandler
{
    protected final HttpServletResponse response;
    protected final HttpSession session;
    protected final PrintWriter out;
    protected final String redirectSuccess;
    protected final String redirectFailure;

    public BaseLoginHandler(HttpServletResponse response, HttpSession session,
                            String redirectSuccess, String redirectFailure) throws IOException, IOException {
        this.response = response;
        this.session = session;
        this.redirectSuccess = redirectSuccess;
        this.redirectFailure = redirectFailure;
        this.response.setContentType("text/html");
        this.out = response.getWriter();
        out.println("<script type='text/javascript'>");
    }

    protected void handleSuccess() {
        out.println("alert('Login Successful!');");
        out.println("window.location.href = '" + redirectSuccess + "';");
    }

    protected void handleForbidden(String message) {
        out.println("alert('" + escapeJavaScript(message) + "');");
        out.println("window.location.href = '" + redirectFailure + "';");
    }

    protected void handleFailure(String message) {
        out.println("alert('" + escapeJavaScript(message) + "');");
        out.println("window.location.href = '" + redirectFailure + "';");
    }

    protected void handleError() {
        out.println("alert('An Error Occurred During login. Please try again!');");
        out.println("window.location.href = '" + redirectFailure + "';");
    }

    protected String escapeJavaScript(String input) {
        if (input == null) return "";
        return input.replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public void close() {
        out.println("</script>");
        out.close();
    }
}
