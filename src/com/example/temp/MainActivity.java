package com.example.temp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.example.temp.SideBar.OnTouchingLetterChangedListener;

public class MainActivity extends Activity {

	ExpandableListView expandableListView;
	private SideBar mSideBar;
	private TextView mDialog;
	ListViewAdapter treeViewAdapter;

	public String[] groups;
	List<String> group;

	public String[][] child;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		group = getBrandListFromJson(testJsonString());
		groups = group.toArray(new String[group.size()]);
		child = new String[groups.length][1];
		for (int i = 0; i < child.length; i++) {
			for (int j = 0; j < child[i].length; j++) {
				child[i][j] = "";
			}
		}

		treeViewAdapter = new ListViewAdapter(this, ListViewAdapter.PaddingLeft >> 1, group);
		expandableListView = (ExpandableListView) this.findViewById(R.id.expandableListView);

		List<ListViewAdapter.TreeNode> treeNode = treeViewAdapter.GetTreeNode();
		for (int i = 0; i < groups.length; i++) {

			ListViewAdapter.TreeNode node = new ListViewAdapter.TreeNode();
			node.parent = groups[i];
			for (int ii = 0; ii < child[i].length; ii++) {
				node.childs.add(child[i][ii]);
			}
			treeNode.add(node);
		}

		treeViewAdapter.UpdateTreeNode(treeNode);
		expandableListView.setAdapter(treeViewAdapter);

		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				//				setSelection(groupPosition);
				for (int i = 0; i < groups.length; i++) {
					if (groupPosition != i) {
						expandableListView.collapseGroup(i);
					}

				}

			}
		});

		mSideBar = (SideBar) findViewById(R.id.sidrbar);
		mDialog = (TextView) findViewById(R.id.dialog);

		mSideBar.setTextView(mDialog);
		mSideBar.setString(getSideStrings(testJsonString()));
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				System.out.println(s);
				int position = getPositionForSection(s);
				// getFirstLetter(s);
				if (position != -1) {
					// myExpandableListView.setSelectionFromTop(position, y);
					expandableListView.setSelectedGroup(position);
				}

			}
		});

	}

	/**
	 * 获取其第一次出现该首字母的位置
	 */
	private int getPositionForSection(String s) {
		if (s.equals("#")) {

			for (int i = 0; i < group.size(); i++) {
				String sortStr = group.get(i);
				if (!getFirstLetter(sortStr).matches("^[a-zA-Z]*")) {
					return i;
				}
			}

		} else {

			for (int i = 0; i < group.size(); i++) {
				String sortStr = group.get(i);
				if (getFirstLetter(sortStr).equals(s)) {
					return i;
				}
			}
		}

		return -1;
	}

	private String getFirstLetter(String str) {
		int size = HanziToPinyin.getInstance().get(str).size();
		String firstString = "";
		String secodString = "";

		String target = "";
		String result = "";

		if (size > 0) {
			firstString = HanziToPinyin.getInstance().get(str).get(0).target;
			target = HanziToPinyin.getInstance().get(str).get(0).target;
			if (size > 1) {
				secodString = HanziToPinyin.getInstance().get(str).get(1).target;
			}
		}
		if (target != null && !"".equals(target)) {
			result = HanziToPinyin.getInstance().get(str).get(0).target.substring(0, 1);
		}
		if ("ZHONG".equals(firstString) && "QING".equals(secodString)) {
			result = "C";
		}

		return result;
	}

	public String[] getSideStrings(String json) {
		List<String> letterList = new ArrayList<String>();

		try {

			JSONObject jsonObject = new JSONObject(json);
			JSONObject jsonObject2 = jsonObject.getJSONObject(KEY.DATA);
			String odd = null;
			for (Iterator<String> iter = jsonObject2.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				if (key.matches("^[a-zA-Z]*")) {
					letterList.add(key);
				} else {
					odd = "#";
				}

			}
			Collections.sort(letterList);
			if (odd != null) {
				letterList.add(odd);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] sideStrings = (String[]) letterList.toArray(new String[letterList.size()]);
		return sideStrings;
	}

	public List<String> getBrandListFromJson(String json) {

		List<String> groups = new ArrayList<String>();
		String[] b = getSideStrings(json);

		try {

			JSONObject jsonObject = new JSONObject(json);
			JSONObject jsonObject2 = jsonObject.getJSONObject(KEY.DATA);

			for (int i = 0; i < b.length; i++) {

				if (jsonObject2.getJSONArray(b[i]) != null) {
					JSONArray jsonArray = jsonObject2.getJSONArray(b[i]);

					for (int j = 0; j < jsonArray.length(); j++) {

						JSONObject jsonObject3 = jsonArray.getJSONObject(j);
						String brandName = jsonObject3.getString("brand");

						groups.add(brandName);

					}

				}
			}
			return groups;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public String testJsonString() {
		String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
		String[] hanZi = { "爱丽舍", "奔驰", "车前", "东风", "鄂尔多斯", "法拉利", "高乐高", "哈登", "I", "吉利", "卡宴",
				"雷克萨斯", "摩托罗拉", "诺基亚", "欧莱雅", "飘柔", "七匹狼", "容声", "声东击西", "台电", "U", "V", "渥太华",
				"小天鹅", "雅马哈", "真维斯", "00" };
		String jsonresult = "";//定义返回字符串  
		JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串  
		try {

			int brandCount = 0;
			JSONObject jsonObj2 = new JSONObject();
			for (int k = 0; k < b.length; k++) {

				JSONArray jsonArray1 = new JSONArray();

				for (int j = 0; j < 3; j++) {

					JSONObject jsonObj1 = new JSONObject();
					jsonObj1.put("brand_id", String.valueOf(brandCount));
					jsonObj1.put("letter", b[k]);
					jsonObj1.put("brand", hanZi[k] + String.valueOf(j));
					jsonArray1.put(jsonObj1);
					brandCount++;
				}

				jsonObj2.put(b[k], jsonArray1);

			}
			object.put("data", jsonObj2);//向总对象里面添加包含pet的数组  
			jsonresult = object.toString();//生成返回字符串  
		} catch (JSONException e) {
			// TODO Auto-generated catch block  
			e.printStackTrace();
		}
		//		Log.i("test", "生成的json串为:" + jsonresult);
		return jsonresult;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
