package jp.caliconography.chroneco.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import jp.caliconography.chroneco.R;
import jp.caliconography.chroneco.adapter.MemberAdapter;
import jp.caliconography.chroneco.model.parseobject.Member;
import jp.caliconography.chroneco.util.ToastHelper;
import jp.caliconography.chroneco.util.Utils;

/**
 * A list fragment representing a list of Members. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MemberListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Member selectdMember) {
        }
    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private MemberAdapter mMeberAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MemberListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);

        mMeberAdapter = new MemberAdapter(this.getActivity());
        setListAdapter(mMeberAdapter);
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(
//                getActivity(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                DummyContent.ITEMS));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // ネットワークに接続できないとき
        if (Utils.isOffline(getActivity())) {
            ToastHelper.makeText(getActivity(), getString(R.string.network_disconncted), Toast.LENGTH_LONG).show();
            return;
        }

        Member clickedMember = (Member) listView.getAdapter().getItem(position);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(clickedMember);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.member_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // ネットワークに接続できないとき
        if (Utils.isOffline(getActivity())) {
            ToastHelper.makeText(getActivity(), getString(R.string.network_disconncted), Toast.LENGTH_LONG).show();
            return false;
        }

        int id = item.getItemId();
        if (id == R.id.refresh) {
            MemberAdapter adapter = (MemberAdapter) this.getListAdapter();
            adapter.clear();
            adapter.loadObjects();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ネットワークに接続できないとき
        if (Utils.isOffline(getActivity())) {
            ToastHelper.makeText(getActivity(), getString(R.string.network_disconncted), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(Member selectdMember);
    }
}
