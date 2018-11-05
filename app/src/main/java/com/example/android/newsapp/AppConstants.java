package com.example.android.newsapp;

public final class AppConstants {

    private AppConstants() {
    }

    public static final String BASE_URL = "http://content.guardianapis.com/search";
    public static final String QUERY_API_KEY = "api-key";
    public static final String QUERY_SHOW_FIELD = "show-fields";
    public static final String QUERY_SHOW_THUMBNAIL = "thumbnail";
    public static final String QUERY_PARAM = "q";
    public static final String QUERY_VALUE = "udacity, google, android";
    public static final String API_KEY = BuildConfig.API_KEY_GUARDIAN;
    public static final String QUERY_AUTHOR = "show-tags";
    public static final String QUERY_AUTHOR_VALUE = "contributor";
    public static final String QUERY_PAGE = "page";

    public static final class JsonKeys {
        public static final String WEB_TITLE = "webTitle";
        public static final String TAGS = "tags";
        public static final String THUMBNAIL = "thumbnail";
        public static final String FIELDS = "fields";
        public static final String WEB_URL = "webUrl";
        public static final String WEB_PUBLICATION_DATE = "webPublicationDate";
        public static final String SECTION_NAME = "sectionName";
        public static final String RESULTS= "results";
        public static final String RESPONSE = "response";
    }
}
