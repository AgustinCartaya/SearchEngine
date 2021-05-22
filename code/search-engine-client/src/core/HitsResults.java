package core;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author ujarky
 */
public class HitsResults {
    
    private ArrayList<HitsResult> pages;
    
    public HitsResults(){
        this.pages = new ArrayList<>();
    }
    
    public void setPagesByJson(String Json){
        Gson gson = new Gson();
        Collections.addAll(pages, gson.fromJson(Json, HitsResult[].class));
    }
    
    public void addPage(HitsResult page){
        this.pages.add(page);
    }
    
    public void clearList(){
        this.pages.clear();
    }
    
    public ArrayList<HitsResult> getPages(){
        return this.pages;
    }
    
    public void sortPagesByAuthority(){
        this.pages.sort(new Comparator<HitsResult>(){
            @Override
            public int compare(HitsResult p1, HitsResult p2) {
                return (int)((p2.getAuthority()- p1.getAuthority())*1000000);
            }
        });
    }
    
    public void sortPagesByHub(){
        this.pages.sort(new Comparator<HitsResult>(){
            @Override
            public int compare(HitsResult p1, HitsResult p2) {
                return (int)((p2.getHub() - p1.getHub())*1000000);
            }
        });
    } 
    
}
