package ca.douglascollege.mobileproject.piggy;

public class SavingRecyclerView {
    private int _img;
    private String _text1;
    private String _text2;


    // Constructor for override image and text
    public SavingRecyclerView(int img, String text1, String text2 ){
        _img = img;
        _text1 = text1;
        _text2 = text2;
    }

    public void changeText1(String text1){
        _text1 = text1;
    }

    // Get method for image and text
    public int getImageResource(){
        return _img;
    }

    public String getText1(){
        return _text1;
    }

    public String getText2(){
        return _text2;
    }
}
