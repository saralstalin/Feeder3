package net.azurewebsites.fishprice.feeder3;

public class SpinnerClass {

    private  int value;
    private String text;

    public SpinnerClass ( int value , String text ) {
        this.value = value;
        this.text = text;
    }

    public int getId () {
        return value;
    }

    public String getText () {
        return text;
    }

    @Override
    public String toString () {
        return text;
    }

}