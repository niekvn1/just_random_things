package knickknacker.niekserver.Databasing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class UserContract {
    private UserContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
            + UserEntry._ID + " INTEGER PRIMARY KEY "
            + UserEntry.COLUMN_NAME_TITLE + " TEXT, "
            + UserEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
}
