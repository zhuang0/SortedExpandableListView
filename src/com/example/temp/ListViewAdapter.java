package com.example.temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewAdapter extends BaseExpandableListAdapter {
	public static final int ItemHeight = 48;// 每项的高度
	public static final int PaddingLeft = 36;// 每项的高度
	private int myPaddingLeft = 0;
	private List<String> group;

	private MyGridView toolbarGrid;

	private List<TreeNode> treeNodes = new ArrayList<TreeNode>();

	private Context parentContext;

	private LayoutInflater layoutInflater;

	static public class TreeNode {
		Object parent;
		List<Object> childs = new ArrayList<Object>();
	}

	public ListViewAdapter(Context context, int myPaddingLeft, List<String> group) {
		this.group = group;
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentContext = context;
		this.myPaddingLeft = myPaddingLeft;
	}

	public List<TreeNode> GetTreeNode() {
		return treeNodes;
	}

	public void UpdateTreeNode(List<TreeNode> nodes) {
		treeNodes = nodes;
	}

	public void RemoveAll() {
		treeNodes.clear();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return treeNodes.get(groupPosition).childs.get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return treeNodes.get(groupPosition).childs.size();
	}

	static public TextView getTextView(Context context) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		return textView;
	}

	/**
	 * 可自定义ExpandableListView
	 */
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		/*if (convertView == null) {
			layoutInflater = (LayoutInflater) parentContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.view, null);
			toolbarGrid = (MyGridView) convertView.findViewById(R.id.GridView_toolbar);
			toolbarGrid.setNumColumns(4);// 设置每行列数
			toolbarGrid.setGravity(Gravity.CENTER);// 位置居中
			toolbarGrid.setHorizontalSpacing(10);// 水平间隔
			toolbarGrid.setAdapter(getMenuAdapter(getchildList(groupPosition), groupPosition));// 设置菜单Adapter
			toolbarGrid.setOnItemClickListener(this);
		}
		return convertView;*/
		View childView;
		ViewChildHolder vh = null;

		if (convertView == null) {
			childView = layoutInflater.inflate(R.layout.gridview, null);
			vh = new ViewChildHolder(childView);
			childView.setTag(vh);
		} else {
			childView = convertView;
			vh = (ViewChildHolder) childView.getTag();
		}

		vh.gridview.setNumColumns(4);// 设置每行列数
		vh.gridview.setGravity(Gravity.CENTER);// 位置居中
		vh.gridview.setHorizontalSpacing(10);// 水平间隔
		vh.gridview.setAdapter(getMenuAdapter(getchildList(groupPosition), groupPosition));// 设置菜单Adapter
		final int po = groupPosition;
		vh.gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(parentContext,
						"click " + getchildList(po).get(position).get(String.valueOf(po)),
						Toast.LENGTH_SHORT).show();
			}
		});

		return childView;
	}

	class ViewChildHolder {

		MyGridView gridview;

		public ViewChildHolder(View view) {
			gridview = (MyGridView) view.findViewById(R.id.GridView_toolbar);
		}

	}

	private SimpleAdapter getMenuAdapter(List<HashMap<String, String>> data, int position) {

		SimpleAdapter simperAdapter = new SimpleAdapter(parentContext, data, R.layout.grid_item,
				new String[] { String.valueOf(position) }, new int[] { R.id.item_text });
		return simperAdapter;
	}

	public List<HashMap<String, String>> getchildList(int groupPosition) {
		String brandName = group.get(groupPosition).toString();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 10; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(String.valueOf(groupPosition), brandName + "-" + String.valueOf(i));
			list.add(map);

		}
		return list;
	}

	/**
	 * 可自定义list
	 */
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {

		View groupView;
		ViewGroupHolder vh = null;

		if (convertView == null) {
			groupView = layoutInflater.inflate(R.layout.group_bar, parent, false);
			vh = new ViewGroupHolder(groupView);
			groupView.setTag(vh);
		} else {
			groupView = convertView;
			vh = (ViewGroupHolder) groupView.getTag();
		}

		if (groupPosition == 0) {

			vh.letter.setVisibility(View.VISIBLE);

		} else {

			String preLetter = getFirstLetter(group.get(groupPosition - 1).toString());
			String thisLetter = getFirstLetter(group.get(groupPosition).toString());

			if (thisLetter.matches("^[a-zA-Z]*")) {
				if (!thisLetter.equals(preLetter)) {
					vh.letter.setVisibility(View.VISIBLE);
				} else {
					vh.letter.setVisibility(View.GONE);
				}

			}

			if (!thisLetter.matches("^[a-zA-Z]*")) {
				if (preLetter.matches("^[a-zA-Z]*")) {
					vh.letter.setVisibility(View.VISIBLE);
				} else {
					vh.letter.setVisibility(View.GONE);
				}

			}
		}

		String brandName = group.get(groupPosition).toString();
		vh.title.setText(brandName);

		String letter = getFirstLetter(group.get(groupPosition).toString()).toUpperCase();
		if (letter.matches("^[a-zA-Z]*")) {
			vh.letter.setText(letter);
		} else {
			vh.letter.setText("#");
		}

		return groupView;
	}

	class ViewGroupHolder {
		TextView title = null;
		TextView letter = null;

		public ViewGroupHolder(View view) {
			title = (TextView) view.findViewById(R.id.group_title);
			letter = (TextView) view.findViewById(R.id.group_letter);
		}
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getGroup(int groupPosition) {
		return treeNodes.get(groupPosition).parent;
	}

	public int getGroupCount() {
		return treeNodes.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
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

}