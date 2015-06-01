package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "TAG")
public class Tag extends Model {
    @Column(name = "NAME", unique = true)
    public String mTitle;

    public static Tag addTag(String tagName) {
        Tag result = new Select().from(Tag.class).where("NAME=?", tagName).executeSingle();
        if (result == null) {
            result = new Tag();
            result.mTitle = tagName;
            result.save();
        }
        return result;
    }
}
