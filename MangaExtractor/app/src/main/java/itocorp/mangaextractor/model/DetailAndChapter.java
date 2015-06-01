package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "DETAIL_AND_CHAPTER")
class DetailAndChapter extends Model {
    @Column(name = "DETAIL", onDelete=Column.ForeignKeyAction.CASCADE)
    public MangaDetail mDetail;
    @Column(name = "CHAPTER", onDelete=Column.ForeignKeyAction.CASCADE)
    public Chapter mChapter;

    public DetailAndChapter() {}
}
