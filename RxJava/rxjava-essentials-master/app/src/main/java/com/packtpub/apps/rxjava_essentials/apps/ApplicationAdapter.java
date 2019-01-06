package com.packtpub.apps.rxjava_essentials.apps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.packtpub.apps.rxjava_essentials.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

  private List<AppInfo> mApplications;

  private int mRowLayout;

  public ApplicationAdapter(List<AppInfo> applications, int rowLayout) {
    mApplications = applications;
    mRowLayout = rowLayout;
  }

  public void addApplications(List<AppInfo> applications) {
    mApplications.clear();
    mApplications.addAll(applications);
    notifyDataSetChanged();
  }

  public void addApplication(int position, AppInfo appInfo) {
    if (position < 0) {
      position = 0;
    }
    mApplications.add(position, appInfo);
    notifyItemInserted(position);
  }

  @Override public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(final ViewHolder viewHolder, int i) {
    final AppInfo appInfo = mApplications.get(i);
    viewHolder.name.setText(appInfo.getName());

    SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
    String date =  formatter.format(new Date(appInfo.getLastUpdateTime()));

    viewHolder.date.setText(date);

    getBitmap(appInfo.getIcon()).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(viewHolder.image::setImageBitmap);
  }

  @Override public int getItemCount() {
    return mApplications == null ? 0 : mApplications.size();
  }

  private Observable<Bitmap> getBitmap(String icon) {
    return Observable.create(subscriber -> {
      subscriber.onNext(BitmapFactory.decodeFile(icon));
      subscriber.onCompleted();
    });
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView date;
    public ImageView image;

    public ViewHolder(View itemView) {
      super(itemView);
      name = (TextView) itemView.findViewById(R.id.name);
      date = (TextView) itemView.findViewById(R.id.date);
      image = (ImageView) itemView.findViewById(R.id.image);
    }
  }
}
