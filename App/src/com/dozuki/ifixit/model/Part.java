package com.dozuki.ifixit.model;

import java.io.Serializable;

public class Part implements Serializable {
   private static final long serialVersionUID = 2884598684003517264L;

   protected String mNote;
   protected String mTitle;
   protected String mUrl;
   protected String mThumb;

   public Part(String title, String url, String thumb, String notes) {
      mNote = notes;
      mTitle = title;
      mUrl = url;
      mThumb = thumb;
   }

   public void setTitle(String title) {
      mTitle = title;
   }

   public String getTitle() {
      return mTitle;
   }

   public void setUrl(String url) {
      mUrl = url;
   }

   public String getUrl() {
      return mUrl;
   }

   public void setThumb(String thumb) {
      mThumb = thumb;
   }

   public String getThumb() {
      return mThumb;
   }

   public void setNote(String note) {
      mNote = note;
   }

   public String getNote() {
      return mNote;
   }

   public String toString() {
      return "{Part: " + mTitle + ", " + mThumb +  ", " + mUrl +
       ", " + mNote + "}";
   }
}