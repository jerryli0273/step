package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/question-data")
public class QuestionDataServlet extends HttpServlet {


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Map<String, Integer> questionVotes = new HashMap<>();
    
    Query query = new Query("Question").addSort("type", SortDirection.DESCENDING);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity e: results.asIterable()) {
        String q = (String) e.getProperty("type");
        int currentVotes = questionVotes.containsKey(q) ? questionVotes.get(q) : 0;
        questionVotes.put(q, currentVotes + 1);
    }

    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(questionVotes);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String q = request.getParameter("question");

    Entity questionEntity = new Entity("Question");
    questionEntity.setProperty("type", q);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(questionEntity);

    response.sendRedirect("/index.html#questions");
  }
}
