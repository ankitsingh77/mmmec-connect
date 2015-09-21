package com.quipmate2.features; 

import java.util.ArrayList;
import org.json.JSONArray;
import com.example.quipmate2.R;
import com.quipmate2.adapter.NavigationItemsAdapter;
import com.quipmate2.utils.CommonMethods;
import com.quipmate2.utils.NetworkHelper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class WelcomeActivity extends FragmentActivity {

	private DrawerLayout dLayout;
	private ListView dList;
	private CharSequence mTitle, dTitle;
	private String[] navTitles;
	private TypedArray navIcons;
	private ArrayList<NavigationListItem> arraylist;
	private ActionBarDrawerToggle drawerToggle;
	Session session;
	String start="0";
	JSONArray adata;

	public class ListItemClicked implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if (NetworkHelper.checkNetworkConnection(WelcomeActivity.this)) {
				displayItem(position); 
			} else {
				CommonMethods.ShowInfo(WelcomeActivity.this,
						getString(R.string.network_error)).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		mTitle = getTitle();
		dTitle = "Menu";
		navTitles = getResources().getStringArray(R.array.navigation_titles);
		navIcons = getResources().obtainTypedArray(R.array.navigation_icons);

		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		dList = (ListView) findViewById(R.id.drawer_list);

		arraylist = new ArrayList<NavigationListItem>();

		for (int i = 0; i < navTitles.length; i++) {
			arraylist.add(new NavigationListItem(navTitles[i], navIcons.getResourceId(i, -1)));
		} 

		NavigationItemsAdapter adapter = new NavigationItemsAdapter(this,
				arraylist);
		dList.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		drawerToggle = new ActionBarDrawerToggle(this, dLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				getActionBar().setTitle(dTitle);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

		};
		dLayout.setDrawerListener(drawerToggle);
		dList.setOnItemClickListener(new ListItemClicked());
		navIcons.recycle();
		if (savedInstanceState == null) {
			displayItem(0);
		}
		
		session = new Session(getApplicationContext());
	}

	// called on invalidateOptionsMenu()
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean drawerOpen = dLayout.isDrawerOpen(dList);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActionBar().setTitle("Home");
	}

	private void displayItem(int position) {
		try {
			

			if (position == 0) {
				Intent msgs = new Intent(WelcomeActivity.this,
				Message.class);
				startActivity(msgs);
			} 
			else if (position == 1) {
				Intent msgs = new Intent(WelcomeActivity.this,
				CoWorkers.class);
				startActivity(msgs);
			}

			// close the navigation drawer
			dList.setItemChecked(position, true);
			setTitle(arraylist.get(position).name);
			dLayout.closeDrawer(dList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// dummy fragment
	public static class NewFragment extends Fragment {

		public NewFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home, container,
					false);
			ImageView fragmentImg = (ImageView) rootView
					.findViewById(R.id.fragment_image);
			fragmentImg.setImageResource(getArguments().getInt("Image"));
			TextView fragmentTxt = (TextView) rootView
					.findViewById(R.id.fragment_text);
			fragmentTxt.setText(getArguments().getString("Text"));
			return rootView;
		}
	}
}
