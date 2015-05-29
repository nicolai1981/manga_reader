package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "TAG")
public class Tag extends Model {
    @Column(name = "NAME", unique = true)
    public String mTitle;
}
