package itocorp.mangaextractor.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "DETAIL_AND_TAG")
class DetailAndTag extends Model {
    @Column(name = "DETAIL", onDelete=Column.ForeignKeyAction.CASCADE)
    public MangaDetail mDetail;
    @Column(name = "TAG", onDelete=Column.ForeignKeyAction.CASCADE)
    public Tag mTag;

    public DetailAndTag() {}
}
