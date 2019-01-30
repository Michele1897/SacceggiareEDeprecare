package com.example.michele.bozze.Data;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.example.michele.bozze.R;

public class GlobalVariables extends Application {

    private int floor = 0;
    private int walls = 0;

    //private int[] oggTrovati = {0,0,0,0,0,0,0};
    private int trovatiRossi = 0;
    private int trovatiGialli = 0;
    private int trovatiVerdi = 0;
    private int trovatiBlu = 0;
    private int trovatiMarroni = 0;
    private int trovatiNeri = 0;

    private boolean sixthColorSearched = true;
        //di default mostro tutti i 6 riquadri per scegliere gli oggetti da raccogliere
        //sarà compito dell'utente premere conferma sulle impostazioni iniziali

    private HashMap<String,Integer> colors;
    private HashMap<String,Pair<Integer,Integer>> objectsToCollect;

    public void selectColors(){
        selectColors(null,null);
        colors.remove("Bianco");
    }
    public void selectColors(String ifloor, String iwalls){
        colors = new HashMap<>();
        colors.put("Rosso",getResources().getColor(R.color.Rosso));
        colors.put("Nero",getResources().getColor(R.color.Nero));
        colors.put("Bianco",getResources().getColor(R.color.Bianco));
        colors.put("Blu",getResources().getColor(R.color.Blu));
        colors.put("Verde",getResources().getColor(R.color.Verde));
        colors.put("Giallo",getResources().getColor(R.color.Giallo));
        colors.put("Marrone",getResources().getColor(R.color.Marrone));

        floor = colors.get(ifloor);
        walls = colors.get(iwalls);

        colors.remove(ifloor);
        colors.remove(iwalls);

        sixthColorSearched=colors.size()==6;
    }

    public void setObjectsToCollect(int a[]){
        objectsToCollect = new HashMap<>();
        Object keySet [] = colors.keySet().toArray();
        for(int i=0;i<a.length;i++){
            objectsToCollect.put(keySet[i].toString(),new Pair(colors.get(keySet[i].toString()),a[i]));
        }
    }
        //dato un colore, ritorna il numero di elementi ancora da cercare
    public int getCounter(String color){
        return objectsToCollect.get(color).getFirst();
    }

        //metodi getter e setter vari autogenerati
    public int getFloor() {
        return floor;
    }
    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getWalls() {
        return walls;
    }
    public void setWalls(int walls) {
        this.walls = walls;
    }

    public boolean isSixthColorSearched() {
        return sixthColorSearched;
    }
    public void setSixthColorSearched(boolean sixthColorSearched) {
        this.sixthColorSearched = sixthColorSearched;
    }

    public HashMap<String, Integer> getColors() {
        return colors;
    }
    public void setColors(HashMap<String, Integer> colors) {
        this.colors = colors;
    }

    public HashMap<String, Pair<Integer, Integer>> getObjectsToCollect() {
        return objectsToCollect;
    }
    public void setObjectsToCollect(HashMap<String, Pair<Integer, Integer>> objectsToCollect) {
        this.objectsToCollect = objectsToCollect;
    }

    public void modificaTrovati(int colore, int quantit)
    {
        switch (colore)
            {
                case 0 : {
                    trovatiRossi = trovatiRossi + quantit;
                }
                case 1 : {
                    trovatiGialli = trovatiGialli + quantit;
                }
                case 2 : {
                    trovatiVerdi = trovatiVerdi + quantit;
                }
                case 3 : {
                    trovatiBlu = trovatiBlu + quantit;
                }
                case 4 : {
                    trovatiMarroni = trovatiMarroni + quantit;
                }
                case 5 : {
                    trovatiNeri = trovatiNeri + quantit;
                }

            }
    }

    public int[] giveTrovati()
        {
            int[] res = {trovatiRossi, trovatiGialli, trovatiVerdi, trovatiBlu, trovatiMarroni, trovatiNeri};
            return res;
        }




    //il primo sarà l'RGB, il secondo il numero di oggetti da raccogliere
    private class Pair<F,S> {
        private F first;
        private S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public F getFirst() {
            return first;
        }

        public void setFirst(F first) {
            this.first = first;
        }

        public S getSecond() {
            return second;
        }

        public void setSecond(S second) {
            this.second = second;
        }
    }
}

