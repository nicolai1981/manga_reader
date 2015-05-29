package itocorp.mangaextractor.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

@Table(name = "MANGA")
public class Manga extends Model {
    @Column(name = "TITLE", unique = true)
    public String mTitle;
    @Column(name = "AUTHOR")
    public String mAuthor;
    @Column(name = "ARTIST")
    public String mArtist;
    @Column(name = "COMPLETED")
    public boolean mIsComplete = false;

    public static final List<Manga> getList() {
        return new Select().from(Manga.class).orderBy("TITLE").execute();
    }

    public static Manga getByTitle(String title) {
        return new Select().from(Manga.class).where("TITLE=?", title).executeSingle();
    }

    public void addDetail(MangaDetail value) {
        List<MangaDetail> details = getDetails();
        MangaDetail detail = null;
        for (MangaDetail item : details) {
            if (item.mSiteSource.mTitle.equals(value.mSiteSource.mTitle)) {
                detail = item;
                break;
            }
        }
        if (detail == null) {
            // new value
            Log.e(">>>>>", "detail: " + value.save());

            MangaAndDetail mangaAndDetail = new MangaAndDetail();
            mangaAndDetail.mManga = this;
            mangaAndDetail.mDetail = value;
            Log.e(">>>>>", "relation: " + mangaAndDetail.save());

        } else {
            // update
            detail.mDescription = value.mDescription;
            detail.mImage = value.mImage;
            detail.mTotalChapter = value.mTotalChapter;
            detail.mSiteSource = value.mSiteSource;
            detail.mURL = value.mURL;
            Log.e(">>>>>", "detail: " + detail.save());
        }
    }

    public List<MangaDetail> getDetails() {
        List<MangaDetail> result = new ArrayList<MangaDetail>();
        List<MangaAndDetail> list = getMany(MangaAndDetail.class, "MANGA");
        for (MangaAndDetail item : list) {
            result.add(item.mDetail);
        }
        return result;
    }

    public MangaDetail getDetailBySource(String source) {
        MangaDetail result = null;
        for (MangaDetail item : getDetails()) {
            if (item.mSiteSource.mTitle.equals(source)) {
                result = item;
            }
        }
        return result;
    }
}
