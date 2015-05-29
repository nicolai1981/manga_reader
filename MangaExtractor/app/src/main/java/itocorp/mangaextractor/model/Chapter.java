package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Table(name = "TAG")
public class Chapter extends Model {
    @Column(name = "URL", unique = true)
    public String mURL;
    @Column(name = "TITLE")
    public String mTitle;
    @Column(name = "NUMBER")
    public int mNumber;
    @Column(name = "DATE_PUBLISHED")
    public Date mPublishedDate;
    @Column(name = "PAGES")
    public int mTotalPages;
    @Column(name = "PAGE_LIST")
    public List<String> mPageURLList = new ArrayList<String>();

}
