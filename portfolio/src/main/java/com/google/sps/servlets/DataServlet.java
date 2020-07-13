// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.sps.comment.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery results = datastore.prepare(query);

		int numComments = getNumComments(request);
		String languageForComments = request.getParameter("languageForComments");
		Translate translate = TranslateOptions.getDefaultInstance().getService();

		ArrayList < Comment > comments = new ArrayList < >();
		for (Entity entity: results.asIterable()) {
			long id = entity.getKey().getId();
			String commentBody = (String) entity.getProperty("body");
			long timestamp = (long) entity.getProperty("timestamp");

			Translation translation = translate.translate(commentBody, Translate.TranslateOption.targetLanguage(languageForComments));
			String translatedText = translation.getTranslatedText();
			Comment comment = new Comment(id, translatedText, timestamp);
			if (numComments > 0) {
				comments.add(comment);
				numComments--;
			}
		}
		Gson gson = new Gson();

		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(gson.toJson(comments));
	}

	/**
   * Converts an ArrayList instance into a JSON string using the Gson library.
   */
	private String convertToJsonUsingGson(ArrayList < String > a) {
		Gson gson = new Gson();
		String json = gson.toJson(a);
		return json;
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Get the input from the form.
		String text = getParameter(request, "text-input", "");
		long timestamp = System.currentTimeMillis();

		Entity commentEntity = new Entity("Comment");
		commentEntity.setProperty("body", text);
		commentEntity.setProperty("timestamp", timestamp);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(commentEntity);

		// Redirect to the main page.
		response.sendRedirect("/index.html#comment");
	}

	/**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
	private String getParameter(HttpServletRequest request, String name, String defaultValue) {
		String value = request.getParameter(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/** 
   * @return the max number of comments allowed for the portfolio.
   */
	public int getNumComments(HttpServletRequest request) {
		String stringNum = request.getParameter("numComments");
		int numComments;
		try {
			numComments = Integer.parseInt(stringNum);
		} catch(NumberFormatException e) {
			System.err.println("Could not convert to int: " + stringNum);
			return - 1;
		}
		return numComments;
	}
}