package lol.otp;

import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *
 * @author ganich_j
 *
 */
public class MainActivity extends Activity {

	private TextView tv_cd;
	private ListView lv_accountList;
	private EditText et_account;
	private EditText et_token;
	private ProgressBar progressBar;

	private AccountsDataSource dataSource;

	private LayoutInflater li;
	private View promptsView;

	private AccountListAdapter adapter;

	private List<Account> myList;
	private CountDownTimer firstCountDown;
	private CountDownTimer normalTimer;

	private long countDownTime;

	private AlertDialog alertDialog;

	public void updateTokens() {
		for (Account acc : myList) {
			String code = TOTPUtility.generateDaCode(acc.getDecodedSecret());
			acc.setCode(code);
		}
		adapter.notifyDataSetChanged();
	}

	public void generateDialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Create, add and update
								if (et_account.getText().toString().length() > 0
										&& et_token.getText().toString()
												.length() > 0
										&& et_token.getText().toString()
												.length() == 32) {
									Account tmp = dataSource.createAccount(
											et_account.getText().toString(),
											et_token.getText().toString()
													.replaceAll("\\s+", "")
													.toUpperCase(Locale.US));
									myList.add(tmp);
									updateTokens();
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		alertDialog = alertDialogBuilder.create();
	}

	public void generateDialogDeletion(final int position) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setMessage("Are you sure you want to delete it?");
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dataSource.deleteAccount(myList.get(position));
								myList.remove(position);
								updateTokens();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// create alert dialog
		alertDialogBuilder.create().show();
	}

	public void updateStuff() {
		int progress = (int) ((long) (TOTPUtility.TIME_STEP) - countDownTime);
		countDownTime = TOTPUtility.getTimeTillNextTick(TOTPUtility
				.getUnixTime());
		tv_cd.setText(String.valueOf(countDownTime));
		progressBar.setProgress(progress);
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dataSource = new AccountsDataSource(this);
		dataSource.open();
		myList = dataSource.getAllAccounts();
		li = LayoutInflater.from(this);
		promptsView = li.inflate(R.layout.dialog, null);
		et_account = (EditText) promptsView.findViewById(R.id.et_account_name);
		et_token = (EditText) promptsView.findViewById(R.id.et_token);
		lv_accountList = (ListView) findViewById(R.id.list);
		lv_accountList
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						generateDialogDeletion(position);
						return false;
					}
				});
		tv_cd = (TextView) findViewById(R.id.countdown);
		adapter = new AccountListAdapter(this, R.layout.list_row, myList);
		lv_accountList.setAdapter(adapter);
		countDownTime = TOTPUtility.getTimeTillNextTick(TOTPUtility
				.getUnixTime());
		tv_cd.setText(String.valueOf(countDownTime));
		generateDialog();
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		firstCountDown = new CountDownTimer(countDownTime * 1000, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				updateStuff();
			}

			@Override
			public void onFinish() {
				updateStuff();
				updateTokens();
				normalTimer.start();
			}
		};
		normalTimer = new CountDownTimer(TOTPUtility.TIME_STEP * 1000, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				updateStuff();
			}

			@Override
			public void onFinish() {
				updateStuff();
				updateTokens();
				this.start();
			}
		};
		updateTokens();
		firstCountDown.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add) {
			alertDialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
