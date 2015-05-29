package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "SITE_SOURCE")
public class SiteSource extends Model {
    @Column(name = "NAME", unique = true)
    public String mTitle;
    @Column(name = "URL")
    public String mURL;
    @Column(name = "BASE_URL")
    public String mBaseURL;

    public static final SiteSource getByName(String name) {
        return new Select().from(SiteSource.class).where("NAME=?", name).executeSingle();
    }
}
