package com.google.sps.comment;

/** An item of a comment. */
public final class Comment {

  private final long id;
  private final String body;
  private final long timestamp;

  public Comment(long id, String body, long timestamp) {
    this.id = id;
    this.body = body;
    this.timestamp = timestamp;
  }
}