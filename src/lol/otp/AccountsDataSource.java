package lol.otp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

public class AccountsDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_MAIL, MySQLiteHelper.COLUMN_TOKEN };

	public AccountsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Account createAccount(String account, String token) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MAIL, account);
		values.put(MySQLiteHelper.COLUMN_TOKEN, token);
		long insertId = database.insert(MySQLiteHelper.TABLE_ACCOUNTS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCOUNTS,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Account newAccount = cursorToAccount(cursor);
		cursor.close();
		return newAccount;
	}

	public void deleteAccount(Account account) {
		long id = account.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_ACCOUNTS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Account> getAllAccounts() {
		List<Account> comments = new ArrayList<Account>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCOUNTS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Account comment = cursorToAccount(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}
	
	private Account cursorToAccount(Cursor cursor) {
		Account account = new Account();
		account.setId(cursor.getLong(0));
		account.setAccountName(cursor.getString(1));
		account.setOriginalSecret(cursor.getString(2));
		return account;
	}
}
