package edu.ubbcluj.cs.groutes.generalhelper;

import android.content.Context;
import android.util.Log;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;

import edu.ubbcluj.cs.groutes.model.RouteEntity;

public class RouteCalculator {
    private static final String TAG = "grm.RouteCalculator";
    private static RouteCalculator sInstance;

    private long[][] distMatrix;

    private Boolean[] megNincs;
    private int[] kor;
    private int[] routeColors;

    private int mLocationsNr = 0;
    private RouteEntity mRoute;

    public static RouteCalculator getInstance(){
        if(sInstance == null){
            sInstance = new RouteCalculator();
        }
        return sInstance;
    }

    public void orderLocations(Context pContext){
        mRoute = CacheManager.getInstance().getSearchRoute();
        mLocationsNr = mRoute.retSize();

        kor = new int[mLocationsNr];

        getDistances(pContext);

        int[] ord;
        if(mLocationsNr> SettingsManager.getInstance().getSettingEntity().getConfUseExactUntil()){
            ord = utazougynok();
        }else {
            ord = exact();
        }

        ArrayList<Integer> order = new ArrayList<>();
        for(int i=0; i<ord.length;i++){
            order.add(ord[i]);
        }

        mRoute.setOrder(order);
        CacheManager.getInstance().setSearchRoute(mRoute);
    }

    private void getDistances(Context pContext) {
        String auxLog;
        long distance;
        distMatrix = new long[mLocationsNr][mLocationsNr];

        try {
            DistanceMatrix result = DistanceMatrixApi.newRequest(GeoManager.getInstance().getGeoContext(pContext))
                    .mode(mRoute.retTravelMode())
                    .origins(mRoute.returnDirectionMatrixInput())
                    .destinations(mRoute.returnDirectionMatrixInput())
                    .await();

            for (int i = 0; i < mLocationsNr; i++) {
                for (int j = 0; j < mLocationsNr; j++) {

                    if(SettingsManager.getInstance().getSettingEntity().getConfDistanceTime() == 0){
                        distance = result.rows[i].elements[j].distance.inMeters;
                    }else {
                        distance = result.rows[i].elements[j].duration.inSeconds;
                    }
                    auxLog = result.originAddresses[i] + " - " + result.destinationAddresses[j] + ": ";

                    Log.i(TAG, auxLog + String.valueOf(distance));

                    distMatrix[i][j] = distance;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] utazougynok() {
        int[] minkor = new int[mLocationsNr];
        long tav, mintav;

        mintav = Long.MAX_VALUE;
        tav = Long.MAX_VALUE;

        for (int k = 0; k < mLocationsNr; k++) {
            tav = korHossz(k, tav);

            if (tav < mintav) {
                for (int i = 0; i < mLocationsNr; i++) {
                    minkor[i] = kor[i];
                }

                mintav = tav;
            }
        }

        legjobbHossz = mintav;
        return minkor;
    }

    private long korHossz(int k, long tav) {
        int elozo = 0, varos = 0;

        megNincs = new Boolean[mLocationsNr];

        for (int i = 0; i < mLocationsNr; i++) {
            megNincs[i] = true;
        }

        kor[0] = k;
        megNincs[k] = false;
        tav = 0;

        for (int i = 1; i < mLocationsNr; i++) {
            elozo = kor[i - 1];
            varos = legkozelebb(elozo);
            kor[i] = varos;
            megNincs[varos] = false;
            tav += distMatrix[elozo][varos];
        }

        //tav += distMatrix[varos][k];

        return tav;
    }

    private int legkozelebb(int i) {
        int min, jmin;

        min = Integer.MAX_VALUE;
        jmin = 0;

        for (int j = 0; j < mLocationsNr; j++) {
            if (megNincs[j] == true && min > distMatrix[i][j]) {
                min = (int) distMatrix[i][j];
                jmin = j;
            }
        }

        return jmin;
    }

    private int[] exact(){
        legjobbHossz = -1;
        legjobbUt = new int[mLocationsNr];
        seged = new int[mLocationsNr+1];
        halmaz = new int[mLocationsNr+1];

        permut(1);


        return legjobbUt;
    }

    private int[] seged;
    private int[] halmaz;

    private void permut(int index){
        if(index<1){
            return;
        }

        int szam;
        int osszeg=0;
        for (int j=1;j<=mLocationsNr;j++)
        {
            if(seged[j]==0)
            {
                seged[j]=1;
                halmaz[index]=j;
                if(index<mLocationsNr)
                {
                    permut(index+1);
                }
                else
                {
                    //kiir(halmaz,n);
                    for(int k=1;k<mLocationsNr;k++){
                        osszeg+=halmaz[k];
                        osszeg+=distMatrix[halmaz[k]-1][halmaz[k+1]-1];
                    }
                    if(legjobbHossz == -1 || legjobbHossz>osszeg){
                        legjobbHossz = osszeg;
                        for (int p=0;p<mLocationsNr;p++){
                            legjobbUt[p] = halmaz[p+1]-1;
                        }
                    }

                    seged[j]=0;
                    halmaz[index]=0;
                }
            }
        }
        if(halmaz[mLocationsNr]==0)
        {
            szam=halmaz[index-1];
            seged[szam]=0;
            halmaz[index-1]=0;
        }
    }

    private long legjobbHossz = -1;
    private int[] legjobbUt;
}
