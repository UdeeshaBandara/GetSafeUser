package lk.hd192.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.otaliastudios.autocomplete.AutocompletePresenter;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import java.util.List;

import lk.hd192.project.Utils.GetSafeServices;

public class TownsPresenter extends RecyclerViewPresenter<NearestTowns> {


    protected Adapter adapter;
    GetSafeServices getSafeServices = new GetSafeServices();
    List<String> nearestTownsArr;


    public TownsPresenter(Context context) {
        super(context);


    }

    @Override
    protected AutocompletePresenter.PopupDimensions getPopupDimensions() {
        AutocompletePresenter.PopupDimensions dims = new AutocompletePresenter.PopupDimensions();
        dims.width = 600;
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dims;
    }

    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new Adapter();
        return adapter;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {

        List<NearestTowns> all = NearestTowns.TOWNS;

        adapter.setData(all);
//        Log.e("TownsPresenter", "found " + all.size() + " users for query " + query);

        adapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<NearestTowns> data;

        public class Holder extends RecyclerView.ViewHolder {
            private View root;
            private TextView fullname;


            public Holder(View itemView) {
                super(itemView);
                root = itemView;
                fullname = ((TextView) itemView.findViewById(R.id.full_name));

            }
        }

        public void setData(List<NearestTowns> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return (isEmpty()) ? 1 : data.size();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.pick_drop_list, parent, false));
        }

        private boolean isEmpty() {
            return data == null || data.isEmpty();
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            if (isEmpty()) {
                holder.fullname.setText("Searching..");
                holder.root.setOnClickListener(null);
                return;
            }
            final NearestTowns town = data.get(position);
            holder.fullname.setText(town.getTownName());

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchClick(town);
                }
            });
        }
    }


}
