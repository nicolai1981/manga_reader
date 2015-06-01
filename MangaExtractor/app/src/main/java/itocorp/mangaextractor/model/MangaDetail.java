package itocorp.mangaextractor.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "MANGA_DETAIL")
public class MangaDetail extends Model {
    @Column(name = "SITE_SOURCE")
    public SiteSource mSiteSource;
    @Column(name = "URL")
    public String mURL;
    @Column(name = "TOTAL_CHAPTER")
    public int mTotalChapter;
    @Column(name = "DESCRIPTION")
    public String mDescription;
    @Column(name = "IMAGE")
    public String mImage;

    public void addTag(Tag value) {
        List<Tag> tags = getTags();
        Tag tag = null;
        for (Tag item : tags) {
            if (item.mTitle.equals(value.mTitle)) {
                tag = item;
                break;
            }
        }
        if (tag == null) {
            // new value
            Log.e(">>>>>", "detail: " + value.save());

            DetailAndTag detailAndTag = new DetailAndTag();
            detailAndTag.mDetail = this;
            detailAndTag.mTag = value;
            Log.e(">>>>>", "relation: " + detailAndTag.save());
        }
    }

    public List<Tag> getTags() {
        List<Tag> result = new ArrayList<Tag>();
        List<DetailAndTag> list = getMany(DetailAndTag.class, "DETAIL");
        for (DetailAndTag item : list) {
            result.add(item.mTag);
        }
        return result;
    }
}
