package com.swuos.ALLFragment.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swuos.ALLFragment.card.MyItemDecoration;
import com.swuos.swuassistant.R;
import com.swuos.util.SALog;

import java.util.List;

/**
 * Created by ASUS on 2016/3/11.
 */
public class LibraryContentFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<BookCell> books;
    private List<BookCell> userInfo;
    private List<BookInfo> borrowInfo;
    private MyRecyclerViewAdapter adapter;
    private MyRecyclerViewAdapterUserinfo adapterUserinfo;

    private MyRecyclerViewAdapter_BorrowedInfo adapter_borrowedInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView= (RecyclerView) inflater.inflate(R.layout.library_layout_content,container,false);
        return recyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.addItemDecoration(new MyItemDecoration(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void UpdateHistory(List<BookCell> cells){
        this.books=cells;
        SALog.d("HttpLog", "LibraryContentFragment Books Updated!!");
        for(BookCell ce:cells){
            SALog.d("HttpLog", "LibraryContentFragment cell===>" + ce.getBookName());
        }
        adapter=new MyRecyclerViewAdapter(getActivity(),cells);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MyItemDecoration(getContext()));
        adapter.notifyDataSetChanged();
    }

    public void UpdateUserInfo(List<BookCell> info){
        this.userInfo=info;
        SALog.d("HttpLog", "LibraryContentFragment UserInfo Updated!!");
        for(BookCell s:userInfo){
            SALog.d("HttpLog", "LibraryContentFragment s===>" + s);
        }
        adapterUserinfo=new MyRecyclerViewAdapterUserinfo(getActivity(),userInfo);
        recyclerView.setAdapter(adapterUserinfo);
        adapterUserinfo.notifyDataSetChanged();
    }
    public void UpdateBorrowedInfo(List<BookInfo> info){
        this.borrowInfo=info;
        adapter_borrowedInfo=new MyRecyclerViewAdapter_BorrowedInfo(getActivity(),borrowInfo);
        recyclerView.addItemDecoration(new MyItemDecoration(getContext()));
        recyclerView.setAdapter(adapter_borrowedInfo);
        adapter_borrowedInfo.notifyDataSetChanged();
    }
}
