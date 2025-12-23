package com.tictactoe;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession();

        Field field = extractField(session);
        int index = getSelectedIndex(req);

        Sign currentSign = field.getField().get(index);

        if (Sign.EMPTY != currentSign) {
            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        field.getField().put(index, Sign.CROSS);

        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
        }

        List<Sign> data = field.getFieldData();

        session.setAttribute("data", data);
        session.setAttribute("field", field);

        resp.sendRedirect("index.jsp");
    }

    private Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (Field.class != field.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) field;
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }
}
