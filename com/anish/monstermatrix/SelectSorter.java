package com.anish.monstermatrix;

public class SelectSorter<T extends Comparable<T>> implements Sorter<T> {
    
    private T[][] a;
    private String plan = "";

    @Override
    public void load(T[][] a) {
        this.a = a;
    }

    private void swap(int i1, int i2, int j1, int j2) {
        T temp;
        temp = a[i1][i2];
        a[i1][i2] = a[j1][j2];
        a[j1][j2] = temp;
        plan += "" + a[i1][i2] + "<->" + a[j1][j2] + "\n";
    }

    @Override
    public void sort() {
        for(int row = a.length - 1; row >= 0; row--){
            for(int col = a[row].length - 1; col >= 0; col--){
                int maxi = row, maxj = col;
                for(int j = col - 1; j >= 0; j--){
                    if(a[maxi][maxj].compareTo(a[row][j]) < 0) {
                        maxi = row;
                        maxj = j;
                    }
                }
                for (int i = row - 1; i >= 0; i--) {
                    for(int j = a[i].length - 1; j >= 0; j--){
                        if(a[maxi][maxj].compareTo(a[i][j]) < 0) {
                            maxi = i;
                            maxj = j;
                        }
                    }
                }
                if(row != maxi || col != maxj){
                    swap(row, col, maxi, maxj);
                }
            }
        }
    }

    @Override
    public String getPlan() {
        return this.plan;
    }

}