package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "MANGA_AND_DETAIL")
class MangaAndDetail extends Model {
    @Column(name = "MANGA", onDelete=Column.ForeignKeyAction.CASCADE)
    public Manga mManga;
    @Column(name = "DETAIL", onDelete=Column.ForeignKeyAction.CASCADE)
    public MangaDetail mDetail;

    public MangaAndDetail() {}
}
