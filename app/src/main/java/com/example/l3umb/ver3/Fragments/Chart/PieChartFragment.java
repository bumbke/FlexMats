package com.example.l3umb.ver3.Fragments.Chart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.Statiscal;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 11/2/2018.
 */

public class PieChartFragment extends Fragment {
    private PieChart chart;
    private String header;
    private int total_products, digital_2d, digital_3d, concept_art, characters, illustration, other, page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_pie_chart,container,false);

        page = getArguments().getInt("page");
        switch (page) {
            case 1: header = getResources().getString(R.string.chart_popular_post); break;
            case 2: header = getResources().getString(R.string.chart_popular_view); break;
        }
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getStatiscalProcess();
    }

    private void initLayout(View v) {
        chart = v.findViewById(R.id.pieChart1);

        chart.getDescription().setEnabled(false);
        chart.setCenterText(generateCenterText());
        chart.setCenterTextSize(10f);

        // radius of the center hole in percent of maximum radius
        chart.setHoleRadius(35f);
        chart.setTransparentCircleRadius(40f);
        chart.animateY(2000, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setYOffset(11f);
        l.setDrawInside(false);
    }

    private PieData generatePieData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        Log.d(Constants.TAG, digital_2d +"");
        if (digital_2d >0) {
            entries.add(new PieEntry((int) digital_2d, "Digital 2D"));
        }
        if (digital_3d >0) {
            entries.add(new PieEntry((int) digital_3d, "Digital 3D"));
        }
        if (concept_art >0) {
            entries.add(new PieEntry((int) concept_art, "Concept Art"));
        }
        if (characters >0) {
            entries.add(new PieEntry((int) characters, "Characters"));
        }
        if (illustration >0) {
            entries.add(new PieEntry((int) illustration, "Illustration"));
        }
        if (other >0) {
            entries.add(new PieEntry((int) other, "Other"));
        }

        PieDataSet ds1 = new PieDataSet(entries, header);
        ds1.setColors(ColorTemplate.MATERIAL_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);
        ds1.setValueFormatter(new IValueFormatter() {
            private DecimalFormat mFormat = new DecimalFormat("###,###,###");

            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return mFormat.format(value);
            }
        });

        PieData d = new PieData(ds1);

        return d;
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Popular\nOn 2018");
        s.setSpan(new RelativeSizeSpan(2f), 0, 8, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
        return s;
    }

    private void getStatiscalProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Statiscal statiscal = new Statiscal();
        statiscal.setCategory(page);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CATEGORY_STATISCAL_OPERATION);
        request.setStatiscal(statiscal);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)) {
                    total_products = resp.getStatiscal().getTotal_products();
                    digital_2d = resp.getStatiscal().getDigital_2D();
                    digital_3d = resp.getStatiscal().getDigital_3D();
                    concept_art = resp.getStatiscal().getConcept_art();
                    characters = resp.getStatiscal().getCharacters();
                    illustration = resp.getStatiscal().getIllustration();
                    other = resp.getStatiscal().getOther();

                    chart.setData(generatePieData());
                    chart.notifyDataSetChanged();
                    chart.invalidate();
                    Log.d(Constants.TAG,"chart success");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
