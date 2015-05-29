package itocorp.mangaextractor.control;

import android.util.Log;

import itocorp.mangaextractor.model.SiteSource;

public class Configurator {
    public static final String MANGA_READER = "Manga Reader";
    public static final String MANGA_FOX = "Manga Fox";
    public static final String MANGA_EDEN = "Manga Eden";
    public static final String MANGA_HERE = "Manga Here";
    public static final String BATOTO = "Batoto";

    public void setup() {
        SiteSource site = new SiteSource();
        site.mTitle = MANGA_READER;
        site.mURL = "http://www.mangareader.net/alphabetical";
        site.mBaseURL = "http://www.mangareader.net";
        Log.e(">>>>>>", "id: " + site.save());

        site = new SiteSource();
        site.mTitle = MANGA_FOX;
        site.mURL = "http://www.mangareader.net/alphabetical";
        site.mBaseURL = "http://www.mangareader.net";
        Log.e(">>>>>>", "id: " + site.save());

        site = new SiteSource();
        site.mTitle = MANGA_EDEN;
        site.mURL = "http://www.mangareader.net/alphabetical";
        site.mBaseURL = "http://www.mangareader.net";
        Log.e(">>>>>>", "id: " + site.save());

        site = new SiteSource();
        site.mTitle = MANGA_HERE;
        site.mURL = "http://www.mangareader.net/alphabetical";
        site.mBaseURL = "http://www.mangareader.net";
        Log.e(">>>>>>", "id: " + site.save());

        site = new SiteSource();
        site.mTitle = BATOTO;
        site.mURL = "http://www.mangareader.net/alphabetical";
        site.mBaseURL = "http://www.mangareader.net";
        Log.e(">>>>>>", "id: " + site.save());
    }
}
