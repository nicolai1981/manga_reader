package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

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
}
