package itocorp.mangaextractor.control;

import android.os.AsyncTask;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.greenrobot.event.EventBus;
import itocorp.mangaextractor.control.event.BaseEvent;
import itocorp.mangaextractor.control.event.LoadMangaDetailEvent;
import itocorp.mangaextractor.control.event.LoadMangaListEvent;
import itocorp.mangaextractor.model.Manga;
import itocorp.mangaextractor.model.MangaDetail;
import itocorp.mangaextractor.model.SiteSource;
import itocorp.mangaextractor.view.LoadFragment;

public class MangaReader {
    private final SiteSource mSource;

    public MangaReader() {
        mSource = SiteSource.getByName(Configurator.MANGA_READER);
    }

    public void getMangaList(LoadFragment context) {
        Ion.with(context)
           .load(mSource.mURL)
           .asString()
           .setCallback(new GetListCallback());
    }

    private static class GetListCallback implements FutureCallback<String> {
        @Override
        public void onCompleted(Exception e, String result) {
            if (e != null) {
                EventBus eventBus = EventBus.getDefault();
                LoadMangaListEvent event = new LoadMangaListEvent();
                event.mResult = BaseEvent.RESULT.ERROR;
                event.mMessage = "Load failed";
                eventBus.post(event);
                return;
            }

            new AsyncTask<String, String, String>() {
                EventBus mEventBus = EventBus.getDefault();

                @Override
                protected String doInBackground(String... params) {
                    SiteSource source = SiteSource.getByName(Configurator.MANGA_READER);

                    Document doc = Jsoup.parse(params[0]);
                    Elements classList = doc.getElementsByClass("series_alpha");
                    for (Element item : classList) {
                        if (!"ul".equals(item.tagName())) {
                            continue;
                        }
                        for (Element liItem : item.getElementsByTag("li")) {
                            Elements aItem = liItem.getElementsByTag("a");
                            String title = aItem.get(0).text().trim();
                            Manga manga = Manga.getByTitle(title);
                            if (manga == null) {
                                manga = new Manga();
                                manga.mTitle = title;
                            }
                            manga.mIsComplete = (liItem.getElementsByAttributeValue("class", "mangacompleted").size() != 0);

                            LoadMangaListEvent event = new LoadMangaListEvent();
                            event.mResult = BaseEvent.RESULT.UPDATE;
                            event.mMessage = "id: " + manga.save() + " - " + title;
                            mEventBus.postSticky(event);

                            MangaDetail detail = new MangaDetail();
                            detail.mSiteSource = source;
                            detail.mURL = aItem.attr("href");
                            manga.addDetail(detail);
                        }
                    }
                    return "Load finished";
                }

                @Override
                protected void onPostExecute(String value) {
                    LoadMangaListEvent event = new LoadMangaListEvent();
                    event.mResult = BaseEvent.RESULT.SUCCESS;
                    event.mMessage = "Finished";
                    mEventBus.post(event);
                }

            }.execute(result);
        }
    }

    public void getMangaDetails(Manga manga, LoadFragment context) {
        MangaDetail detail = manga.getDetailBySource(Configurator.MANGA_READER);
        Log.e(">>>>>>", "loading: " + detail.mSiteSource.mBaseURL + detail.mURL);
        Ion.with(context)
           .load(detail.mSiteSource.mBaseURL + detail.mURL)
           .asString()
           .setCallback(new GetDetailCallback(detail));
    }

    private static class GetDetailCallback implements FutureCallback<String> {
        private final MangaDetail mDetail;

        public GetDetailCallback(MangaDetail detail) {
            mDetail = detail;
        }

        @Override
        public void onCompleted(Exception e, String result) {
            Log.e(">>>>>>", "load");
            if (e != null) {
                EventBus eventBus = EventBus.getDefault();
                LoadMangaDetailEvent event = new LoadMangaDetailEvent();
                event.mResult = BaseEvent.RESULT.ERROR;
                event.mMessage = "Load detail failed";
                eventBus.post(event);
                return;
            }

            new AsyncTask<String, String, String>() {
                EventBus mEventBus = EventBus.getDefault();

                @Override
                protected String doInBackground(String... params) {
                    Document doc = Jsoup.parse(params[0]);
                    Element div = doc.getElementById("mangaimg");
                    if (div != null) {
                        mDetail.mImage = div.getElementsByTag("img").get(0).attr("src");
                        Log.e(">>>>>>", "image: " + mDetail.mImage);
                    }
                    div = doc.getElementById("mangaproperties");
                    Elements properties = div.getElementsByTag("td");
                    mDetail.save();

                    return "Load detail finished";
                }

                @Override
                protected void onPostExecute(String value) {
                    LoadMangaDetailEvent event = new LoadMangaDetailEvent();
                    event.mResult = BaseEvent.RESULT.SUCCESS;
                    event.mMessage = value;
                    mEventBus.post(event);
                }

            }.execute(result);
        }
    }
}
