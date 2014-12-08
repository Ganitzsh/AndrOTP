package lol.otp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author ganich_j
 *
 */
public class AccountListAdapter extends ArrayAdapter<Account> {
	private final Context context;
	private final List<Account> values;

	public AccountListAdapter(Context context, int rowLayout,
			List<Account> objects) {
		super(context, rowLayout, objects);
		this.context = context;
		this.values = objects;
	}

	static class ViewHolder {
		private	TextView	tv_token;
		private	TextView	tv_accountName;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_row, parent, false);
			viewHolder.tv_accountName = (TextView) convertView.findViewById(R.id.account_name);
			viewHolder.tv_token = (TextView) convertView.findViewById(R.id.token);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_token.setText(values.get(position).getCode());
		viewHolder.tv_accountName.setText(values.get(position).getAccountName());
		return (convertView);
	}
}