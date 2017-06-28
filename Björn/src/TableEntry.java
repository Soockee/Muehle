package Björn.src;

/**
 * Created by xXThermalXx on 13.06.2017.
 * -Datenklasse die zum Speichern von hashWerten
 */
public class TableEntry {

    private int flag;
    private int value;

    public TableEntry(int value, int flag) {
        this.value = value;
        this.flag = flag;
    }//Constructor

    public int getFlag() {
        return flag;
    }//getFlag

    public int getValue() {
        return value;
    }//getValue

}//class
