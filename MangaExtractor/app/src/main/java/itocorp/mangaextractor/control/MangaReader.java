package itocorp.mangaextractor.control;

import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import itocorp.mangaextractor.control.event.BaseEvent;
import itocorp.mangaextractor.control.event.LoadMangaDetailEvent;
import itocorp.mangaextractor.control.event.LoadMangaListEvent;
import itocorp.mangaextractor.model.Manga;
import itocorp.mangaextractor.model.MangaDetail;
import itocorp.mangaextractor.model.SiteSource;
import itocorp.mangaextractor.model.Tag;
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
        Ion.with(context)
           .load(detail.mSiteSource.mBaseURL + detail.mURL)
           .asString()
           .setCallback(new GetDetailCallback(manga, detail));
    }

    private static class GetDetailCallback implements FutureCallback<String> {
        private final Manga mManga;
        private final MangaDetail mDetail;

        public GetDetailCallback(Manga manga, MangaDetail detail) {
            mDetail = detail;
            mManga = manga;
        }

        @Override
        public void onCompleted(Exception e, String result) {
            Log.e(">>>>>>", "load");
            Log.e(">>>>>>", "manga.....: " + mManga.mTitle);
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
                        Log.e(">>>>>>", "image.....: " + mDetail.mImage);
                    }

                    // Properties
                    div = doc.getElementById("mangaproperties");
                    Elements properties = div.getElementsByTag("td");
                    for (int i=0; i < properties.size(); i++) {
                        Element title = properties.get(i++);
                        if ("propertytitle".equals(title.attr("class"))) {
                            if (i >= properties.size()) {
                                break;
                            }
                            String titleValue = title.text();
                            Element propertieValue = properties.get(i);
                            if ("Author:".equals(titleValue.trim())) {
                                String value = propertieValue.text().trim();
                                if (value.contains("(Story & Art)")) {
                                    value = value.replace("(Story & Art)", "").trim();
                                    mManga.mArtist = value;
                                } else if (value.contains("(Story)")) {
                                    int index = value.indexOf("(Story)");
                                    if (value.contains("(Art)")) {
                                        mManga.mArtist = value.replace("(Art)", "").substring(value.indexOf(",", index) + 1).trim();
                                    }
                                    value = value.substring(0, index).trim();
                                }
                                mManga.mAuthor = value;
                                Log.e(">>>>>>", "author....: " + mManga.mAuthor);
                            } else if ("Artist:".equals(titleValue.trim())) {
                                String value = propertieValue.text().trim();
                                value = value.replace("(Story & Art)", "").trim();
                                if (value.length() > 0) {
                                    mManga.mArtist = value;
                                }
                                Log.e(">>>>>>", "artist....: " + mManga.mArtist);
                            } else if ("Genre:".equals(titleValue.trim())) {
                                Elements tags = propertieValue.getElementsByTag("span");
                                for (Element item : tags) {
                                    Tag tag = Tag.addTag(item.text());
                                    mDetail.addTag(tag);
                                }
                                Log.e(">>>>>>", "tags......: " + mDetail.getTags().size());
                            }

                        } else {
                            break;
                        }
                    }

                    // Description
                    div = doc.getElementById("readmangasum");
                    if (div != null) {
                        mDetail.mDescription = div.getElementsByTag("p").text().trim();
                        Log.e(">>>>>>", "descr.....: " + mDetail.mDescription);
                    }

                    // Chapters
                    div = doc.getElementById("chapterlist");
                    Elements chapters = div.getElementsByAttributeValue("class", "chico_manga");
                    mDetail.mTotalChapter = chapters.size();
                    Log.e(">>>>>>", "chapters..: " + mDetail.mTotalChapter);

                    mManga.save();
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
